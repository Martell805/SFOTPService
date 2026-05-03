package ru.vovandiya.sfotpservice.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service("fileNotification")
@Slf4j
public class FileNotificationService implements NotificationService {

    private static final String FILE_PATH = "otp_codes.txt";

    @Override
    public void send(String destination, String code) {
        try (var writer = new FileWriter(FILE_PATH, true)) {
            writer.write(LocalDateTime.now() + " | " + destination + " | " + code + "\n");
            log.info("OTP saved to file for {}", destination);
        } catch (IOException e) {
            log.error("Failed to write OTP to file", e);
            throw new RuntimeException("Failed to save OTP to file", e);
        }
    }
}