package br.com.ms_task.exception;

public class TaskAlreadyInProgressException extends RuntimeException {

    public TaskAlreadyInProgressException(
            final Long id
    ) {
        super("Task with id " + id + " is already in progress");
    }
}
