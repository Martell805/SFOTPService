package ru.vovandiya.sfotpservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "otp_config")
@Getter
@Setter
@NoArgsConstructor
public class OtpConfig {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private int codeLength = 6;

    @Column(nullable = false)
    private int ttlSeconds = 300;
}