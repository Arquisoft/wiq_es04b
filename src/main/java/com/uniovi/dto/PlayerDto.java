package com.uniovi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {
    private String username;
    private String email;
    private String password;
    private String passwordConfirm;
    private String[] roles;
}
