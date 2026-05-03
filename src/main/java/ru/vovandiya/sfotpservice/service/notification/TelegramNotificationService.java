package ru.vovandiya.sfotpservice.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service("telegramNotification")
@Slf4j
public class TelegramNotificationService implements NotificationService {

    private final String telegramApiUrl;
    private final String chatId;

    public TelegramNotificationService(
        @Value("${telegram.api-url:https://api.telegram.org}") String apiUrl,
        @Value("${telegram.bot-token}") String botToken,
        @Value("${telegram.chat-id}") String chatId) {
        this.telegramApiUrl = apiUrl + "/bot" + botToken + "/sendMessage";
        this.chatId = chatId;
    }

    @Override
    public void send(String destination, String code) {
        var message = destination + ", your confirmation code is: " + code;
        var url = telegramApiUrl + "?chat_id=" + chatId + "&text=" + urlEncode(message);
        sendRequest(url);
    }

    private void sendRequest(String url) {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("Telegram API error. Status: {}", response.statusCode());
            } else {
                log.info("OTP sent via Telegram");
            }
        } catch (InterruptedException e) {
            log.error("Telegram request interrupted", e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("Failed to send Telegram message", e);
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}