package de.fau.servers.task_launcher;

import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import lombok.NonNull;
import sila2.de.fau.utilities.tasklaunchcontroller.v1.TaskLaunchControllerGrpc;
import sila2.de.fau.utilities.tasklaunchcontroller.v1.TaskLaunchControllerOuterClass;
import sila2.org.silastandard.SiLAFramework;
import sila_java.library.core.sila.errors.SiLAErrors;
import sila_java.library.server_base.command.observable.ObservableCommandManager;
import sila_java.library.server_base.command.observable.ObservableCommandTaskRunner;

class TaskLaunchController extends TaskLaunchControllerGrpc.TaskLaunchControllerImplBase
        implements AutoCloseable {

    private static final int MAX_QUEUE_SIZE = 3;
    private static final int MAX_CONCURRENT_CALLS = 1;
    private int exitValue = -1;
    private long expRetVal = 0;

    private final ObservableCommandManager<
            TaskLaunchControllerOuterClass.StartTask_Parameters, TaskLaunchControllerOuterClass.StartTask_Responses> executionManager
            = new ObservableCommandManager<>(
                    new ObservableCommandTaskRunner(MAX_QUEUE_SIZE, MAX_CONCURRENT_CALLS),
                    command -> {
                        final String cmd = command.getParameter().getTaskCommand().getValue().trim();
                        expRetVal = command.getParameter().getExpectedReturnValue().getValue();
                        final List<String> commandAndArgs = List.of(cmd.split(" "));
                        ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
                        try {
                            Process proc = pb.start();
                            exitValue = proc.waitFor();
                        } catch (IOException ex) {
                            System.err.println(ex.getMessage());
                            exitValue = 2;
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }

                        return TaskLaunchControllerOuterClass.StartTask_Responses
                                .newBuilder()
                                .build();
                    }, Duration.ofSeconds(30));

    @Override
    public void close() {
        this.executionManager.close();
    }

    @Override
    public void startTask(
            @NonNull final TaskLaunchControllerOuterClass.StartTask_Parameters req,
            @NonNull final StreamObserver<SiLAFramework.CommandConfirmation> responseObserver) {
        if (!req.hasTaskCommand()
                || req.getTaskCommand().getValue().isBlank()) {
            responseObserver.onError(SiLAErrors.generateValidationError(
                    "Executable",
                    "The parameter to the executable was not set. Please specify a path to an "
                    + "valid executable."));
            return;
        }
        this.executionManager.addCommand(req, responseObserver);
    }

    @Override
    public void startTaskInfo(
            @NonNull final SiLAFramework.CommandExecutionUUID request,
            @NonNull final StreamObserver<SiLAFramework.ExecutionInfo> responseObserver) {
        this.executionManager.get(request).addStateObserver(responseObserver);
    }

    @Override
    public void startTaskResult(
            @NonNull final SiLAFramework.CommandExecutionUUID request,
            @NonNull final StreamObserver<TaskLaunchControllerOuterClass.StartTask_Responses> responseObserver) {
        /* Throw an error istead of returning the result value.
           This recommended by the SiLA2 Standard Part A at "Command Responses", "Best Practice" */
        if (exitValue != expRetVal) {
            responseObserver.onError(SiLAErrors.generateDefinedExecutionError(
                    "ExitValueNotExpected",
                    "The executable task returned with the value " + exitValue + " instead of the "
                    + "expected value " + expRetVal + ", which indicates that the run was not "
                    + "successfull."));
        } else {
            this.executionManager.get(request).sendResult(responseObserver);
            responseObserver.onCompleted();
        }
    }
}
