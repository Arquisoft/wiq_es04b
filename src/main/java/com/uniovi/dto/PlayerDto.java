package com.uniovi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerDto {

    @Schema(description = "Username of the player", example = "student1")
    private String username;

    @Schema(description = "Email of the player", example = "student1@email.com")
    private String email;

    @Schema(description = "Password of the player", example = "password")
    private String password;

    @Schema(hidden = true)
    private String passwordConfirm;

    @Schema(description = "Roles of the player", example = "[\"ROLE_USER\"]")
    private String[] roles;
}
