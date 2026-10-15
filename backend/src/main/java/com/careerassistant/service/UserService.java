package com.careerassistant.service;

import com.careerassistant.dto.LoginRequest;
import com.careerassistant.dto.LoginResponse;
import com.careerassistant.dto.RegisterRequest;
import com.careerassistant.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest loginRequest);
}
