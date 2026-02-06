package br.com.ms_task.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(
            final Long id
    ) {
        super("Task id " + id + " not found");
    }
}
