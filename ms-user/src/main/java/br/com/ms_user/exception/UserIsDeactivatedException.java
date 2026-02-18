package br.com.ms_user.exception;

public class UserIsDeactivatedException extends RuntimeException {

    public UserIsDeactivatedException(Long id) {
        super("The user with id " + id + " is already deactivated");
    }
}
