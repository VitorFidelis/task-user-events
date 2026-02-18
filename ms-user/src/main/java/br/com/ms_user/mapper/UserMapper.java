package br.com.ms_user.mapper;

import br.com.ms_user.entity.User;
import com.fiap.user_service.user.dto.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class UserMapper {

    private UserMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserDTO toUserDto(User entity) {
        return new UserDTO()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .status(UserStatusDTO.valueOf(entity.getStatus().name()))
                .createdAt(toOffset(entity.getCreatedAt()))
                .updatedAt(toOffset(entity.getUpdatedAt()))
                .activatedAt(toOffset(entity.getActivatedAt()))
                .deactivatedAt(toOffset(entity.getDeactivatedAt()));
    }

    public static User toUserEntity(final CreateUserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }

    private static OffsetDateTime toOffset(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    public static CreateUserResponseDTO toCreateUserResponseDTO(UserDTO userDTO) {
        return new CreateUserResponseDTO()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .status(UserStatusDTO.valueOf(userDTO.getStatus().name()))
                .createdAt(toOffset(userDTO.getCreatedAt().toInstant()));
    }

    public static UpdateUserResponseDTO toUpdateUserResponseDTO(UserDTO userDTO) {
        return new UpdateUserResponseDTO()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .status(UserStatusDTO.valueOf(userDTO.getStatus().name()))
                .updatedAt(toOffset(userDTO.getUpdatedAt().toInstant()));
    }

    public static DeactivateUserResponseDTO toDeactivateUserResponseDTO(UserDTO userDTO) {
        return new DeactivateUserResponseDTO()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .status(UserStatusDTO.valueOf(userDTO.getStatus().name()))
                .deactivatedAt(toOffset(userDTO.getDeactivatedAt().toInstant()));
    }

    public static ReactivateUserResponseDTO toReactivateUserResponseDTO(UserDTO userDTO) {
        return new ReactivateUserResponseDTO()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .status(UserStatusDTO.valueOf(userDTO.getStatus().name()))
                .activatedAt(toOffset(userDTO.getDeactivatedAt().toInstant()));
    }

}
