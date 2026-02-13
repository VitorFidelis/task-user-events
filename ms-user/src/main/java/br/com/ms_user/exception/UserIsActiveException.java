package br.com.ms_user.exception;

public class UserIsActiveException extends RuntimeException {

  public UserIsActiveException(Long id) {
    super("The user with id " + id + " is already active");
  }
}
