package ru.vovandiya.sfotpservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateOtpRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String operationId;
}