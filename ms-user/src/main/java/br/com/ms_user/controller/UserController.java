package br.com.ms_user.controller;

import br.com.ms_user.mapper.UserMapper;
import br.com.ms_user.services.UserServices;
import com.fiap.user_service.user.api.ApiApi;
import com.fiap.user_service.user.dto.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements ApiApi {

    @NonNull
    private final UserServices userServices;

    @Override
    public ResponseEntity<CreateUserResponseDTO> createUser(
            final CreateUserRequestDTO createUserRequestDTO
    ) {
        final var createdUser = this.userServices.createUser(createUserRequestDTO);

        final URI uri = ServletUriComponentsBuilder // Criar URI baseada no caminho atual (/usuarios) + ID do objeto
                .fromCurrentRequest() // Ex: http://localhost:8080/usuarios
                .path("/api/v1/users/{id}") // Adiciona /id
                .buildAndExpand(createdUser.getId()) // Substitui {id} pelo ID do objeto
                .toUri();// Resultado: http://localhost:8080/api/v1/users/1

        var createdUserResponse = UserMapper.toCreateUserResponseDTO(createdUser);

        return ResponseEntity.created(uri).body(createdUserResponse);
    }

    @Override
    public ResponseEntity<DeactivateUserResponseDTO> deactivateUserById(
            final Long userId
    ) {
        var deactivatedUser = this.userServices.deactivate(userId);

        log.info("The user id: {} deactivated successfully", userId);

        var deactivatedUserResponse = UserMapper.toDeactivateUserResponseDTO(deactivatedUser);

        return ResponseEntity.ok(deactivatedUserResponse);
    }

    @Override
    public ResponseEntity<PaginatedResultDTO> getAllUsers(
            final Integer page,
            final Integer size,
            final String sort,
            final UserStatusDTO status
    ) {
        log.info("""
                find all users successfully: page {}, size {}, sort {}, status {}
                """, page, size, sort, status);
        return ResponseEntity.ok(this.userServices.findAllUsers(page, size, sort, status));
    }

    @Override
    public ResponseEntity<UserDTO> getUserById(
            final Long userId
    ) {
        return ResponseEntity.ok(this.userServices.findByIdUser(userId));
    }

    @Override
    public ResponseEntity<ReactivateUserResponseDTO> reactivateUserById(
            final Long userId
    ) {
        var reactivatedUser = this.userServices.reactivate(userId);

        var reactivatedUserResponse = UserMapper.toReactivateUserResponseDTO(reactivatedUser);

        return ResponseEntity.ok(reactivatedUserResponse);
    }

    @Override
    public ResponseEntity<UpdateUserResponseDTO> updateUserById(
            final Long userId,
            final UpdateUserRequestDTO dto
    ) {
        var updatedUser = this.userServices.updateUser(userId, dto);
        var updatedUserResponse = UserMapper.toUpdateUserResponseDTO(updatedUser);
        return ResponseEntity.ok(updatedUserResponse);
    }
}
