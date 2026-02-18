package br.com.ms_task.mapper;

import br.com.ms_task.entity.Task;
import com.fiap.task_service.tasks.dto.CreateTaskRequestDTO;
import com.fiap.task_service.tasks.dto.TaskDTO;

/**
 * Classe utilitária para conversão entre entidade Task e TaskDTO.
 */

public final class TaskMapper {

    private TaskMapper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converte uma entidade Task para TaskDTO.
     * @param task entidade Task
     * @return TaskDTO ou null se task for null
     */
    public static TaskDTO toDto(final Task task) {
        if (task == null) return null;
        return new TaskDTO()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(TaskDTO.StatusEnum.fromValue(task.getStatus().name()));
    }

    /**
     * Converte um CreateTaskRequestDTO para entidade Task.
     * @param dto DTO de criação
     * @return Task ou null se dto for null
     */
    public static Task toEntity(final CreateTaskRequestDTO dto) {
        if (dto == null) return null;
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
    }
}
