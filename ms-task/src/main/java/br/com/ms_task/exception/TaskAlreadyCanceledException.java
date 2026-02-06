package br.com.ms_task.exception;

public class TaskAlreadyCanceledException extends RuntimeException {

    public TaskAlreadyCanceledException(
            final Long id
    ) {
        super("Task with id " + id + " is already canceled");
    }
}
