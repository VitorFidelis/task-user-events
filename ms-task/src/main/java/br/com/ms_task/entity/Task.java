package br.com.ms_task.entity;

import br.com.ms_task.exception.TaskAlreadyCanceledException;
import br.com.ms_task.exception.TaskAlreadyCompletedException;
import br.com.ms_task.exception.TaskAlreadyInProgressException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

/**
 * Entidade que representa uma Tarefa.
 */

@Data // Gera Getters e Setters, @ToString(), @EqualsAndHashCode(), Construtor
@NoArgsConstructor // Gera contrutor vazio
@Builder
@AllArgsConstructor // Gera contrutor com todos os atributos
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Ativa o modo de inclusão explícita. O Lombok só usará os campos que você marcar com @EqualsAndHashCode.Include
@Entity //Informa que é uma entidade JPA
@Table(name = "tasks") // Informa que é uma tabela e o nome dessa tabela
public class Task {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Atributos para Auditoria

    @Column(name = "create_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createAt;

    @Column(name = "update_at", nullable = false, updatable = true)
    @UpdateTimestamp
    private Instant updateAt;

    @Column(name = "delete_at", nullable = true, updatable = true)
    private Instant deletedAt;

    private Task(
            final String title,
            final String description,
            final Status status
    ) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    /**
     * Cria uma nova tarefa com status OPEN.
     */
    public static Task newTask(
            final String title,
            final String description
    ) {
        return new Task(title, description, Status.OPEN);
    }

    /**
     * Cancela a tarefa, se possível.
     */
    public void cancel() {
        if (this.getStatus().equals(Status.CANCELED)) {
            throw new TaskAlreadyCanceledException(this.getId());
        }
        if (this.getStatus().equals(Status.COMPLETED)) {
            throw new TaskAlreadyCompletedException(this.getId());
        }
        this.setStatus(Status.CANCELED);
        this.setDeletedAt(Instant.now());
    }

    /**
     * Coloca a tarefa em progresso, se possível.
     */
    public void execute() {
        if (!this.getStatus().equals(Status.OPEN)) {
            throw new TaskAlreadyInProgressException(this.getId());
        }
        this.setStatus(Status.IN_PROGRESS);
    }

    /**
     * Marca a tarefa como completa, se possível.
     */
    public void complete() {
        if (!this.getStatus().equals(Status.IN_PROGRESS)) {
            throw new TaskAlreadyCompletedException(this.getId());
        }
        this.setStatus(Status.COMPLETED);
    }

    /**
     * Atualiza título e descrição da tarefa.
     */
    public void updateTask(
            final String title,
            final String description
    ) {
        this.setTitle(title);
        this.setDescription(description);
    }
}
