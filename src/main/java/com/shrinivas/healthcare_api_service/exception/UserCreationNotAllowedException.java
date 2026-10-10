package com.shrinivas.healthcare_api_service.exception;

public class UserCreationNotAllowedException extends RuntimeException {
    public UserCreationNotAllowedException(String userCreationNowAllowed) {
        super(userCreationNowAllowed);
    }
}
