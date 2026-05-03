package ru.vovandiya.sfotpservice.service.notification;

public interface NotificationService {
    void send(String destination, String code);
}