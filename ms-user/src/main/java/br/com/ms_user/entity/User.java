package br.com.ms_user.entity;


import br.com.ms_user.exception.UserIsActiveException;
import br.com.ms_user.exception.UserIsDeactivatedException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.Email;
import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String username;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    // Atributos para Auditoria

    @CreationTimestamp
    @Column(nullable = false, name = "created_at",  updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at", updatable = true)
    private Instant updatedAt;

    @Column(nullable = true, name = "activated_at", updatable = true)
    private Instant activatedAt;

    @Column(nullable = true, name = "deactivated_at", updatable = true)
    private Instant deactivatedAt;

    private User(
            final String username,
            final String email,
            final String password,
            final Status status
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public static User newUser(
            final String username,
            final String email,
            final String password
    ) {
        return new User(username, email, password, Status.ACTIVE);
    }

    public void deactivate() {
        if (!this.getStatus().equals(Status.DEACTIVATED)) {
            this.setStatus(Status.DEACTIVATED);
            this.setDeactivatedAt(Instant.now());
        }else {
            throw new UserIsDeactivatedException(this.getId());
        }
    }

    public void reactivate() {
        if (!this.getStatus().equals(Status.ACTIVE)) {
            this.setStatus(Status.ACTIVE);
            this.setActivatedAt(Instant.now());
        }else {
            throw new UserIsActiveException(this.getId());
        }
    }

    public void updateUser(final String username, final String email) {
        this.setUsername(username);
        this.setEmail(email);
    }

}
