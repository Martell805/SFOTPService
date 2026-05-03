package ru.vovandiya.sfotpservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpConfigRequest {
    private int codeLength;
    private int ttlSeconds;
}