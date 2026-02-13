package br.com.ms_user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("The user with id " + id + " not found.");
    }
}
