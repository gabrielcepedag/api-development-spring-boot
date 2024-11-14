package com.darvybm.project.apidevelopment.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
}