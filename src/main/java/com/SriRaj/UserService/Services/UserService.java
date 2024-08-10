package com.SriRaj.UserService.Services;

import com.SriRaj.UserService.Dtos.UserResponseDto;
import com.SriRaj.UserService.Dtos.ValidateResponseDto;
import com.SriRaj.UserService.Models.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    public UserResponseDto createUser(String name, String email, String password, List<Role> roles);

    public ResponseEntity<UserResponseDto> userLogin(String email, String password);

    public ResponseEntity<Void> userLogOut(String Token);

    public ResponseEntity<ValidateResponseDto> validateToken(String token);
}
