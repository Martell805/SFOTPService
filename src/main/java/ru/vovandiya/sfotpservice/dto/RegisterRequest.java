package ru.vovandiya.sfotpservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.vovandiya.sfotpservice.model.Role;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private Role role = Role.USER;
}