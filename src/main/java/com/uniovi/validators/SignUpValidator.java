package com.uniovi.validators;

import com.uniovi.dto.PlayerDto;
import com.uniovi.services.PlayerService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SignUpValidator implements Validator {
    private PlayerService playerService;

    public SignUpValidator(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PlayerDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PlayerDto user = (PlayerDto) target;

        if (!EmailValidator.getInstance().isValid(user.getEmail())) {
            errors.rejectValue("email", null,
                    "El email no es válido");
        }

        if(playerService.getUserByEmail(user.getEmail()).isPresent()){
            errors.rejectValue("email", null,
                    "Ya hay una cuenta registrada con este email");
        }

        if (playerService.getUserByUsername(user.getUsername()).isPresent()) {
            errors.rejectValue("username", null,
                    "Ya existe una cuenta con este nombre de usuario");
        }

        if (user.getPassword() == null
            || !user.getPassword().equals(user.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", null,
                    "Las contraseñas no coinciden");
        }
    }
}
