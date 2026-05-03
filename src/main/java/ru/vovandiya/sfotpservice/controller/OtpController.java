package ru.vovandiya.sfotpservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vovandiya.sfotpservice.dto.GenerateOtpRequest;
import ru.vovandiya.sfotpservice.dto.ValidateOtpRequest;
import ru.vovandiya.sfotpservice.service.OtpService;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Slf4j
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> generate(@AuthenticationPrincipal UserDetails userDetails,
                                         @Valid @RequestBody GenerateOtpRequest request) {
        log.info("POST /api/otp/generate user={} channel={}", userDetails.getUsername(), request.getChannel());
        otpService.generate(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Boolean>> validate(@AuthenticationPrincipal UserDetails userDetails,
                                                         @Valid @RequestBody ValidateOtpRequest request) {
        log.info("POST /api/otp/validate user={} operation={}", userDetails.getUsername(), request.getOperationId());
        var valid = otpService.validate(userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}