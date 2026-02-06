package br.com.ms_task.handler;

import br.com.ms_task.exception.TaskAlreadyCanceledException;
import br.com.ms_task.exception.TaskAlreadyCompletedException;
import br.com.ms_task.exception.TaskAlreadyInProgressException;
import br.com.ms_task.exception.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Handler global para exceções da API de tarefas.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Trata exceções de operações inválidas em tarefas.
     */
    @ExceptionHandler({
            TaskAlreadyCanceledException.class,
            TaskAlreadyCompletedException.class,
            TaskAlreadyInProgressException.class
    })
    public ProblemDetail handleTaskBusinessException(final RuntimeException exception) {
        final var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Operation invalid");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    /**
     * Trata exceção de tarefa não encontrada.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleTaskNotFoundException(final TaskNotFoundException exception) {
        final var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Task not found");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    /**
     * Trata exceções internas do servidor.
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ProblemDetail handleInternalServerError(final HttpServerErrorException exception) {
        final var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal server error");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    /**
     * Trata exceções genéricas não mapeadas.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(final Exception exception) {
        final var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Unexpected error");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}
