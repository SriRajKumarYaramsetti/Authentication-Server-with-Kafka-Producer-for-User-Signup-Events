package com.SriRaj.UserService.Dtos;


import com.SriRaj.UserService.Models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidateResponseDto {
    private String name;
    private String email;
    private Long user_id;
    private List<Role> roles=new ArrayList<>();
}
