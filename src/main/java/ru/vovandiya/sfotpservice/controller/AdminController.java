package ru.vovandiya.sfotpservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vovandiya.sfotpservice.dto.OtpConfigRequest;
import ru.vovandiya.sfotpservice.model.OtpConfig;
import ru.vovandiya.sfotpservice.model.User;
import ru.vovandiya.sfotpservice.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/otp-config")
    public ResponseEntity<OtpConfig> updateConfig(@Valid @RequestBody OtpConfigRequest request) {
        log.info("PUT /api/admin/otp-config length={} ttl={}", request.getCodeLength(), request.getTtlSeconds());
        return ResponseEntity.ok(adminService.updateConfig(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("GET /api/admin/users");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/admin/users/{}", id);
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}