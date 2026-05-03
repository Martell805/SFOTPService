package ru.vovandiya.sfotpservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vovandiya.sfotpservice.dto.OtpConfigRequest;
import ru.vovandiya.sfotpservice.model.OtpConfig;
import ru.vovandiya.sfotpservice.model.Role;
import ru.vovandiya.sfotpservice.model.User;
import ru.vovandiya.sfotpservice.repository.OtpCodeRepository;
import ru.vovandiya.sfotpservice.repository.OtpConfigRepository;
import ru.vovandiya.sfotpservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final OtpConfigRepository otpConfigRepository;
    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;

    public OtpConfig updateConfig(OtpConfigRequest request) {
        var config = otpConfigRepository.findById(1L).orElseGet(OtpConfig::new);
        config.setCodeLength(request.getCodeLength());
        config.setTtlSeconds(request.getTtlSeconds());
        var saved = otpConfigRepository.save(config);
        log.info("OTP config updated: length={} ttl={}", saved.getCodeLength(), saved.getTtlSeconds());
        return saved;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .toList();
    }

    public void deleteUser(Long userId) {
        otpCodeRepository.deleteAll(otpCodeRepository.findByUserId(userId));
        userRepository.deleteById(userId);
        log.info("Deleted user {} with all OTP codes", userId);
    }
}