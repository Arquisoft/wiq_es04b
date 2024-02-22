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
    private String nickname;
    private String email;
    private String name;
    private String lastName;
    private String password;
    private String passwordConfirm;
    private String[] roles;
}
