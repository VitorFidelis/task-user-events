package br.com.ms_user.services;

import br.com.ms_user.entity.User;
import br.com.ms_user.exception.UserNotFoundException;
import br.com.ms_user.mapper.UserMapper;
import br.com.ms_user.repository.UserRepository;
import br.com.ms_user.repository.UserSpecification;
import com.fiap.user_service.user.dto.CreateUserRequestDTO;
import com.fiap.user_service.user.dto.PaginatedResultDTO;
import com.fiap.user_service.user.dto.UpdateUserRequestDTO;
import com.fiap.user_service.user.dto.UserDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServices {

    @NonNull
    private final UserRepository userRepository;

    public UserDTO createUser(final CreateUserRequestDTO dto) {
        final var toCreateUser = User.newUser(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword()
        );
        final var createdTask = this.userRepository.save(toCreateUser);
        log.info("User created with id: {}", createdTask.getId());
        return UserMapper.toUserDto(createdTask);
    }

    public UserDTO updateUser(final Long id, final UpdateUserRequestDTO dto) {
        final var userById = this.userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id));
        userById.updateUser(dto.getUsername(),dto.getEmail());
        final var updatedUser = this.userRepository.save(userById);
        log.info("User updated with id: {}", updatedUser.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO findByIdUser(final Long id) {
        final var userbyId = this.userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id));
        log.info("user found by id {}", userbyId.getId());
        return UserMapper.toUserDto(userbyId);
    }

    @Transactional(readOnly = true)
    public PaginatedResultDTO findAllUsers(
            final Integer page,
            final Integer size,
            final String sort,
            final String status

    ) {
        if (page == null || size == null || sort == null || !sort.contains(",")) {
            throw new IllegalArgumentException("Invalid paging parameters");
        }

        // separa campo e direção
        final var sortParams = sort.split(",");
        final var sortField = sortParams[0];
        final var sortDirection = sortParams[1];

        final var pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortField)
        );

        // cria specification dinâmica
        final var specification = UserSpecification.create(status);

        final var pageResult = userRepository.findAll(specification, pageable);

        final var content = pageResult.getContent()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();

        return new PaginatedResultDTO()
                .page(page)
                .size(size)
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .content(content);

    }

    public void deactivate(final Long id) {
        final var toUserById = this.userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id));
        toUserById.deactivate();
        final var userDeactivated = this.userRepository.save(toUserById);
        log.info("user by id: {} successfully deactivated", userDeactivated.getId());
    }

    public void reactivate(final Long id) {
        final var toUserById = this.userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id));
        toUserById.reactivate();
        final var userReactivated = this.userRepository.save(toUserById);
        log.info("user by id: {} successfully deactivated", userReactivated.getId());
    }
}
