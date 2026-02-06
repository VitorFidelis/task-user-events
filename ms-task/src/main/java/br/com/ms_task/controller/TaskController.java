package br.com.ms_task.controller;

import br.com.ms_task.service.TaskService;
import com.fiap.task_service.tasks.api.ApiApi;
import com.fiap.task_service.tasks.dto.CreateTaskRequestDTO;
import com.fiap.task_service.tasks.dto.PaginatedResultDTO;
import com.fiap.task_service.tasks.dto.TaskDTO;
import com.fiap.task_service.tasks.dto.UpdateTaskRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor // Gera automaticamente um construtor com um parâmetro para cada campo final não inicializado e campos marcados com @NonNull
@RestController
@Slf4j
public class TaskController implements ApiApi {

    private final TaskService taskService;

    /**
     * Cancela uma tarefa pelo ID.
     */
    @Override
    public ResponseEntity<Void> cancelTask(final Long taskId) {
        log.info("Cancelando tarefa com id {}", taskId);
        this.taskService.cancelTask(taskId);
        return ResponseEntity.ok().build();
    }

    /**
     * Marca uma tarefa como completa.
     */
    @Override
    public ResponseEntity<Void> completeTask(final Long taskId) {
        log.info("Completando tarefa com id {}", taskId);
        this.taskService.completeTask(taskId);
        return ResponseEntity.ok().build();
    }

    /**
     * Cria uma nova tarefa.
     */
    @Override
    public ResponseEntity<TaskDTO> createTask(@Valid final CreateTaskRequestDTO body) {
        final var createdTask = this.taskService.createNewTask(body);
        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{taskId}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        log.info("Tarefa criada com id {}", createdTask.getId());
        return ResponseEntity.created(uri).body(createdTask);
    }

    /**
     * Executa uma tarefa pelo ID.
     */
    @Override
    public ResponseEntity<Void> executeTask(final Long taskId) {
        log.info("Executando tarefa com id {}", taskId);
        this.taskService.executeTask(taskId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retorna tarefas paginadas e filtradas.
     */
    @Override
    public ResponseEntity<PaginatedResultDTO> getAllTasks(
            @Valid final Integer page,
            @Valid final Integer size,
            @Valid final String sort,
            @Valid final String status
    ) {
        final var paginatedResult = this.taskService.getAllTasks(page, size, sort, status);
        return ResponseEntity.ok(paginatedResult);
    }

    /**
     * Busca uma tarefa pelo ID.
     */
    @Override
    public ResponseEntity<TaskDTO> getTaskById(final Long taskId) {
        final var getTaskById = this.taskService.getTaskById(taskId);
        return ResponseEntity.ok(getTaskById);
    }

    /**
     * Atualiza uma tarefa pelo ID.
     */
    @Override
    public ResponseEntity<TaskDTO> updateTaskById(
            final Long taskId,
            @Valid final UpdateTaskRequestDTO body
    ) {
        log.info("Atualizando tarefa com id {}", taskId);
        final var updatedTask = this.taskService.updateTask(taskId, body);
        return ResponseEntity.ok(updatedTask);
    }
}
