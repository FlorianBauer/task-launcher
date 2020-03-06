package de.fau.servers.task_launcher;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import lombok.NonNull;
import sila2.de.fau.utilities.tasklaunchcontroller.v1.TaskLaunchControllerGrpc;
import sila2.de.fau.utilities.tasklaunchcontroller.v1.TaskLaunchControllerOuterClass;
import sila2.org.silastandard.SiLAFramework;
import sila_java.library.core.sila.types.SiLAErrors;
import sila_java.library.server_base.command.observable.ObservableCommandManager;
import sila_java.library.server_base.command.observable.ObservableCommandTaskRunner;

@Slf4j
class TaskLaunchController extends TaskLaunchControllerGrpc.TaskLaunchControllerImplBase
        implements AutoCloseable {

    private static final int MAX_QUEUE_SIZE = 3;
    private static final int MAX_CONCURRENT_CALLS = 1;
    private int exitValue = 1;

    private ObservableCommandManager<
            TaskLaunchControllerOuterClass.StartTask_Parameters, TaskLaunchControllerOuterClass.StartTask_Responses> executionManager
            = new ObservableCommandManager<>(
                    new ObservableCommandTaskRunner(MAX_QUEUE_SIZE, MAX_CONCURRENT_CALLS),
                    command -> {
                        final int argListCount = command.getParameter().getArgumentListCount();
                        ArrayList<String> commandAndArgs = new ArrayList<>(argListCount + 1);
                        commandAndArgs.add(0, command.getParameter().getExecutable().getValue());
                        for (int i = 0, j = 1; i < argListCount; i++) {
                            final String arg = command.getParameter().getArgumentList(i).getValue();
                            if (arg.isEmpty()) {
                                // skip empty arguments
                                continue;
                            }
                            commandAndArgs.add(j, arg);
                            j++;
                        }

                        ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
                        try {
                            Process proc = pb.start();
                            exitValue = proc.waitFor();
                        } catch (IOException ex) {
                            log.error("Execption: ", ex);
                            exitValue = 2;
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }

                        return TaskLaunchControllerOuterClass.StartTask_Responses
                                .newBuilder()
                                .build();
                    }, Duration.ofMinutes(1));

    @Override
    public void close() {
        this.executionManager.close();
    }

    @Override
    public void startTask(
            @NonNull final TaskLaunchControllerOuterClass.StartTask_Parameters req,
            @NonNull final StreamObserver<SiLAFramework.CommandConfirmation> responseObserver
    ) {
        if (!req.hasExecutable()) {
            responseObserver.onError(SiLAErrors.generateValidationError(
                    "Executable",
                    "The parameter to the executable was not set. Specify a executable with at "
                    + "least one character."));
            return;
        }
        this.executionManager.addCommand(req, responseObserver);
    }

    @Override
    public void startTaskInfo(
            @NonNull final SiLAFramework.CommandExecutionUUID request,
            @NonNull final StreamObserver<SiLAFramework.ExecutionInfo> responseObserver
    ) {
        this.executionManager.get(request).addStateObserver(responseObserver);
    }

    @Override
    public void startTaskResult(
            @NonNull final SiLAFramework.CommandExecutionUUID request,
            @NonNull final StreamObserver<TaskLaunchControllerOuterClass.StartTask_Responses> responseObserver
    ) {
        // Throw a error istead of returning the result value.
        // This recommended by the SiLA2 Standard Part A at "Command Responses", "Best Practice"
        if (exitValue != 0) {
            responseObserver.onError(SiLAErrors.generateDefinedExecutionError(
                    "ExitNotNull",
                    "The executable task returned with the non-null value " + exitValue
                    + ", which indicates the run was not successfull."));
        } else {
            this.executionManager.get(request).sendResult(responseObserver);
            responseObserver.onCompleted();
        }
    }
}
