package com.SriRaj.UserService.Dtos;


import com.SriRaj.UserService.Models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignUpRequestDto {
    private String name;
    private String email;
    private String password;
    private List<Role> roles;
}
