package de.fau.servers.task_launcher;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import sila_java.library.core.encryption.SelfSignedCertificate;
import sila_java.library.server_base.SiLAServer;
import sila_java.library.server_base.identification.ServerInformation;
import sila_java.library.server_base.utils.ArgumentHelper;
import java.io.IOException;
import static sila_java.library.core.utils.FileUtils.getResourceContent;
import static sila_java.library.core.utils.Utils.blockUntilStop;

/**
 * SiLA Server which can launch programs and scripts on the local host.
 */
@Slf4j
public class TaskLaunchServer implements AutoCloseable {

    // Every SiLA Server needs to define a type
    public static final String SERVER_TYPE = "Task Launch Control Server";

    // SiLA Server constructed in constructor
    private final SiLAServer server;

    /**
     * Application Class using command line arguments
     *
     * @param argumentHelper Custom Argument Helper
     */
    TaskLaunchServer(@NonNull final ArgumentHelper argumentHelper) {
        final ServerInformation serverInfo = new ServerInformation(
                SERVER_TYPE,
                "A Task Launch Server",
                "www.cs7.tf.fau.de",
                "v0.0"
        );

        try {
            /*
            A configuration file has to be given if the developer wants to persist server
            configurations (such as the generated UUID)
             */
            final SiLAServer.Builder builder;
            if (argumentHelper.getConfigFile().isPresent()) {
                builder = SiLAServer.Builder.withConfig(argumentHelper.getConfigFile().get(),
                        serverInfo);
            } else {
                builder = SiLAServer.Builder.withoutConfig(serverInfo);
            }

            if (argumentHelper.useEncryption()) {
                builder.withSelfSignedCertificate();
            }

            /*
            Additional optional arguments are used, if no port is given it's automatically chosen,
            if no network interface is given, discovery is not enabled.
             */
            argumentHelper.getPort().ifPresent(builder::withPort);
            // Enable network discovery on localhost.
            builder.withDiscovery("lo");
            argumentHelper.getInterface().ifPresent(builder::withDiscovery);

            builder.addFeature(
                    getResourceContent("TaskLaunchController.sila.xml"),
                    new TaskLaunchController()
            );

            this.server = builder.start();
        } catch (IOException | SelfSignedCertificate.CertificateGenerationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        this.server.close();
    }

    /**
     * Simple main function that starts the server and keeps it alive
     *
     * @param args The server arguments. Network Interface: `-n`, `--networkInterface`. Server
     * config file holding UUID and ServerName: `-c`, `--configFile`. Only list System Information:
     * `-l`, `--listNetworks`. Encrypt communication or not: `-e`, `--encryption`. Specify (if
     * supported) to start the server with simulation mode enabled: `-s`, `--simulation`. - Display
     * server version: `-v`, `--version`.
     */
    public static void main(final String[] args) {
        final ArgumentHelper argumentHelper = new ArgumentHelper(args, SERVER_TYPE);

        try (final TaskLaunchServer launcherServer = new TaskLaunchServer(argumentHelper)) {
            Runtime.getRuntime().addShutdownHook(new Thread(launcherServer::close));
            blockUntilStop();
        }
        log.info("Termination complete.");
    }

//    static class LauncherImpl extends TaskLaunchControllerGrpc.TaskLaunchControllerImplBase {
//        @Override
//        public void startTask(
//                TaskLaunchControllerOuterClass.StartTask_Parameters req,
//                StreamObserver<TaskLaunchControllerOuterClass.StartTask_Responses> responseObserver
//        ) {
//            if (!req.hasExecutable()) {
//                responseObserver.onError(SiLAErrors.generateValidationError(
//                        "Executable",
//                        "Executable parameter was not set. Specify a executable with at least one "
//                        + "character."
//                ));
//                return;
//            }
//
//            // Custom ValidationError example
//            String command = req.getExecutable().getValue();
//            ArrayList<String> commandAndArgs = new ArrayList<>(req.getArgumentListCount() + 1);
//            commandAndArgs.add(0, command);
//            for (int i = 1; i < req.getArgumentListCount();) {
//                String str = req.getArgumentList(i).getValue();
//                if (str.isEmpty()) {
//                    continue;
//                }
//                Logger.getLogger(TaskLaunchServer.class.getName()).log(Level.INFO,
//                        "arg" + i + ": '" + str + "'");
//                commandAndArgs.add(i, str);
//                i++;
//            }
//
//            ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
//            try {
//                Process proc = pb.start();
//                int exitValue = proc.waitFor();
//                Logger.getLogger(TaskLaunchServer.class.getName()).log(Level.INFO,
//                        "exec return: " + String.valueOf(exitValue));
//            } catch (IOException | InterruptedException ex) {
//                Logger.getLogger(TaskLaunchServer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
}
