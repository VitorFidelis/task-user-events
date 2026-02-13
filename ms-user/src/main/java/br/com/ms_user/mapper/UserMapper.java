package br.com.ms_user.mapper;

import br.com.ms_user.entity.User;
import com.fiap.user_service.user.dto.CreateUserRequestDTO;
import com.fiap.user_service.user.dto.UserDTO;

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
                .status(UserDTO.StatusEnum.fromValue(entity.getStatus().name()));
    }

    public static User toUserEntity(final CreateUserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }
}
