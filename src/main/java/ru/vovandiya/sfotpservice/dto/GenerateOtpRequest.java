package ru.vovandiya.sfotpservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.vovandiya.sfotpservice.enums.OtpChannel;

@Getter
@Setter
public class GenerateOtpRequest {
    @NotBlank
    private String operationId;

    @NotNull
    private OtpChannel channel;

    private String destination;
}