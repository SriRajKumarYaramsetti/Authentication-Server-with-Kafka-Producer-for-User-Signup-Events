package com.SriRaj.UserService.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter
public class Session extends BaseModel{
    private String token;
    private Date expiringAt;


    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)

    private User user;


    @Enumerated(EnumType.ORDINAL)
    private SessionStatus sessionStatus=SessionStatus.ACTIVE;
}
