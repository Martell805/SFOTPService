package ru.vovandiya.sfotpservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vovandiya.sfotpservice.dto.GenerateOtpRequest;
import ru.vovandiya.sfotpservice.dto.ValidateOtpRequest;
import ru.vovandiya.sfotpservice.enums.OtpStatus;
import ru.vovandiya.sfotpservice.model.OtpCode;
import ru.vovandiya.sfotpservice.model.OtpConfig;
import ru.vovandiya.sfotpservice.repository.OtpCodeRepository;
import ru.vovandiya.sfotpservice.repository.OtpConfigRepository;
import ru.vovandiya.sfotpservice.repository.UserRepository;
import ru.vovandiya.sfotpservice.service.notification.EmailNotificationService;
import ru.vovandiya.sfotpservice.service.notification.FileNotificationService;
import ru.vovandiya.sfotpservice.service.notification.SmsNotificationService;
import ru.vovandiya.sfotpservice.service.notification.TelegramNotificationService;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final OtpConfigRepository otpConfigRepository;
    private final UserRepository userRepository;

    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final TelegramNotificationService telegramService;
    private final FileNotificationService fileService;

    public void generate(String userEmail, GenerateOtpRequest request) {
        var config = otpConfigRepository.findById(1L)
                .orElseGet(() -> {
                    var def = new OtpConfig();
                    return otpConfigRepository.save(def);
                });

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var code = generateCode(config.getCodeLength());
        var otp  = new OtpCode();
        otp.setCode(code);
        otp.setOperationId(request.getOperationId());
        otp.setUser(user);
        otp.setExpiresAt(LocalDateTime.now().plusSeconds(config.getTtlSeconds()));
        otpCodeRepository.save(otp);

        log.info("Generated OTP for user {} operation {}", userEmail, request.getOperationId());

        var destination = request.getDestination();
        switch (request.getChannel()) {
            case EMAIL    -> emailService.send(destination, code);
            case SMS      -> smsService.send(destination, code);
            case TELEGRAM -> telegramService.send(destination, code);
            case FILE     -> fileService.send(destination, code);
        }
    }

    public boolean validate(String userEmail, ValidateOtpRequest request) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var otp = otpCodeRepository
                .findByCodeAndOperationIdAndStatus(
                        request.getCode(), request.getOperationId(), OtpStatus.ACTIVE)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (otp == null) {
            log.warn("Invalid OTP attempt for user {} operation {}", userEmail, request.getOperationId());
            return false;
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setStatus(OtpStatus.EXPIRED);
            otpCodeRepository.save(otp);
            log.info("OTP expired for user {} operation {}", userEmail, request.getOperationId());
            return false;
        }

        otp.setStatus(OtpStatus.USED);
        otpCodeRepository.save(otp);
        log.info("OTP validated for user {} operation {}", userEmail, request.getOperationId());
        return true;
    }

    @Scheduled(fixedDelayString = "${otp.expiry-check-interval-ms:60000}")
    public void expireOutdatedCodes() {
        var expired = otpCodeRepository.findByStatus(OtpStatus.ACTIVE).stream()
                .filter(o -> o.getExpiresAt().isBefore(LocalDateTime.now()))
                .peek(o -> o.setStatus(OtpStatus.EXPIRED))
                .toList();
        otpCodeRepository.saveAll(expired);
        if (!expired.isEmpty()) {
            log.info("Marked {} OTP codes as EXPIRED", expired.size());
        }
    }

    private String generateCode(int length) {
        var random = new SecureRandom();
        var sb = new StringBuilder();
        for (var i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}