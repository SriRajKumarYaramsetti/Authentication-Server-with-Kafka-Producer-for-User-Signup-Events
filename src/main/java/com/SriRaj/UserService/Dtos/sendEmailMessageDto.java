package com.SriRaj.UserService.Dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class sendEmailMessageDto {
    private String from;
    private String to;
    private String subject;
    private String body;
}
