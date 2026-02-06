package br.com.ms_task.service;

import br.com.ms_task.entity.Task;
import br.com.ms_task.mapper.TaskMapper;
import br.com.ms_task.exception.TaskNotFoundException;
import br.com.ms_task.repository.TaskRepository;
import br.com.ms_task.repository.TaskSpecification;
import com.fiap.task_service.tasks.dto.CreateTaskRequestDTO;
import com.fiap.task_service.tasks.dto.PaginatedResultDTO;
import com.fiap.task_service.tasks.dto.TaskDTO;
import com.fiap.task_service.tasks.dto.UpdateTaskRequestDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j // Cria um objeto Logger estático para a classe
@Transactional //  é usada para gerenciar transações de banco de dados de forma declarativa no Spring
@Service
@RequiredArgsConstructor // Gera um construtor com parâmetros para todos os campos declarados como final e para aqueles anotados com @NonNull que não possuem valor inicial.
public class TaskService {

    @NonNull
    private final TaskRepository taskRepository;

    /**
     * Cria uma nova tarefa.
     */
    public TaskDTO createNewTask(final CreateTaskRequestDTO infoTask) {
        if (infoTask == null || infoTask.getTitle() == null) {
            throw new IllegalArgumentException("Título da tarefa não pode ser nulo");
        }
        final var toCreateTask = Task.newTask(
                infoTask.getTitle(),
                infoTask.getDescription()
        );
        final var createdTask = this.taskRepository.save(toCreateTask);
        log.info("Tarefa criada com id {}", createdTask.getId());
        return TaskMapper.toDto(createdTask);
    }

    /**
     * Atualiza uma tarefa existente.
     */
    public TaskDTO updateTask(final Long taskId, final UpdateTaskRequestDTO infoTask) {
        if (taskId == null || infoTask == null) {
            throw new IllegalArgumentException("Parâmetros inválidos para atualização");
        }
        final var toUpdate = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        toUpdate.updateTask(infoTask.getTitle(), infoTask.getDescription());
        final var updatedTask = this.taskRepository.save(toUpdate);
        log.info("Tarefa atualizada com id {}", updatedTask.getId());
        return TaskMapper.toDto(updatedTask);
    }

    /**
     * Cancela uma tarefa.
     */
    public void cancelTask(final Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser nulo");
        }
        final var toCancel = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        toCancel.cancel();
        log.info("Tarefa cancelada com id {}", taskId);
        // Não é necessário chamar save() explicitamente se estiver dentro de uma transação
    }

    /**
     * Executa uma tarefa.
     */
    public void executeTask(final Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser nulo");
        }
        final var toExecute = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        toExecute.execute();
        log.info("Tarefa executada com id {}", taskId);
    }

    /**
     * Completa uma tarefa.
     */
    public void completeTask(final Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser nulo");
        }
        final var toComplete = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        toComplete.complete();
        log.info("Tarefa completada com id {}", taskId);
    }

    /**
     * Busca uma tarefa pelo ID.
     */
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(final Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser nulo");
        }
        final var toGet = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        return TaskMapper.toDto(toGet);
    }

    /**
     * Busca tarefas paginadas e filtradas.
     */
    @Transactional(readOnly = true)
    public PaginatedResultDTO getAllTasks(
            final Integer page,
            final Integer size,
            final String sort,
            final String status
    ) {
        if (page == null || size == null || sort == null || !sort.contains(",")) {
            throw new IllegalArgumentException("Parâmetros de paginação ou ordenação inválidos");
        }
        final var sortParams = sort.split(",");
        final var sortField = sortParams[0];
        final var sortDirection = sortParams[1];
        final var pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortField)
        );
        final var specification = TaskSpecification.create(status);
        final var tasks = taskRepository.findAll(specification, pageable);
        final var totalElements = tasks.getTotalElements();
        final var totalPages = tasks.getTotalPages();
        final var taskDTOs = tasks.getContent().stream()
                .map(TaskMapper::toDto)
                .toList();

        return new PaginatedResultDTO()
                .page(Long.valueOf(page))
                .size(Long.valueOf(size))
                .totalElements(totalElements)
                .totalPages(Long.valueOf(totalPages))
                .content(taskDTOs);
    }
}
