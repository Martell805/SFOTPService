package ru.vovandiya.sfotpservice.service;

public interface NotificationService {
    void send(String destination, String code);
}