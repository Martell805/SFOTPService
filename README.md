# SFOTPService

Сервис генерации и валидации OTP-кодов с JWT-аутентификацией. Поддерживает отправку кодов по Email, SMS, Telegram и сохранение в файл.

---

## Стек

- Java 21, Spring Boot, Spring Security, Spring Data JPA
- PostgreSQL 17
- JWT (jjwt)
- Gradle

---

## Запуск

### 1. Поднять инфраструктуру

```bash
docker-compose up -d
```

Запустятся четыре контейнера:

| Контейнер | Назначение | Адрес |
|-----------|------------|-------|
| `postgres` | База данных | `localhost:5432` |
| `maildev` | Эмулятор email | `localhost:1080` (UI), `localhost:1025` (SMTP) |
| `smppsim` | Эмулятор SMS (SMPP) | `localhost:2775`, `localhost:8989` (UI) |
| `wiremock` | Мок Telegram API | `localhost:8089` |

### 2. Запустить приложение

```bash
./gradlew bootRun
```

Приложение запустится на `http://localhost:8080`.

---

## API

### Аутентификация

#### Регистрация пользователя

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "user123",
  "role": "USER"
}
```

#### Регистрация администратора

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123",
  "role": "ADMIN"
}
```

> Второй администратор зарегистрирован быть не может — вернёт ошибку.

#### Логин

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "user123"
}
```

Ответ:

```json
{
  "token": "eyJ..."
}
```

Все последующие запросы требуют заголовок:

```
Authorization: Bearer <token>
```

---

### API администратора

> Доступно только пользователям с ролью `ADMIN`.

#### Обновить конфигурацию OTP-кодов

```http
PUT /api/admin/otp-config
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "codeLength": 6,
  "ttlSeconds": 120
}
```

#### Получить список всех пользователей (без администраторов)

```http
GET /api/admin/users
Authorization: Bearer <admin_token>
```

#### Удалить пользователя и его OTP-коды

```http
DELETE /api/admin/users/{id}
Authorization: Bearer <admin_token>
```

---

### API пользователя

> Доступно только пользователям с ролью `USER`.

#### Генерация OTP-кода

```http
POST /api/otp/generate
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "operationId": "payment-001",
  "channel": "EMAIL",
  "destination": "user@example.com"
}
```

Поле `channel` принимает одно из значений: `EMAIL`, `SMS`, `TELEGRAM`, `FILE`.

#### Валидация OTP-кода

```http
POST /api/otp/validate
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "code": "123456",
  "operationId": "payment-001"
}
```

Ответ:

```json
{ "valid": true }
```

---

## Проверка каналов доставки

### Email

После отправки запроса с `"channel": "EMAIL"` открыть в браузере:

```
http://localhost:1080
```

Письмо с OTP-кодом появится в интерфейсе MailDev в реальном времени.

### SMS

После отправки запроса с `"channel": "SMS"` выполнить команду:

```bash
docker exec smppsim tail -f /smppsim/sme_decoded.capture | grep "short_message"
```

В выводе появится строка вида:

```
short_message=Your code: 123456
```

Также доступен веб-интерфейс SMPPSim со статистикой PDU:

```
http://localhost:8989
```

### Telegram

После отправки запроса с `"channel": "TELEGRAM"` открыть:

```
http://localhost:8089/__admin/requests
```

В JSON-ответе будут все входящие запросы к моку Telegram API. Очистить историю запросов перед тестом:

```bash
curl -X DELETE http://localhost:8089/__admin/requests
```

### Файл

После отправки запроса с `"channel": "FILE"` код сохранится в файл `otp_codes.txt` в корне проекта в формате:

```
2026-05-03T15:00:00 | user@example.com | 123456
```

---

## Сценарии тестирования

| Сценарий | Ожидаемый результат |
|----------|---------------------|
| Регистрация второго администратора | `400 Bad Request` |
| Запрос к `/api/admin/**` с токеном пользователя | `403 Forbidden` |
| Валидация верного кода | `{ "valid": true }` |
| Повторная валидация использованного кода | `{ "valid": false }` |
| Валидация после истечения TTL | `{ "valid": false }` |

---

## Настройка

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: myuser
    password: mypassword

jwt:
  secret: "your-256-bit-secret-key-here-min-32-chars!!"
  expiration: 86400000

telegram:
  bot-token: "fake-token"
  chat-id: "123456789"
  api-url: "http://localhost:8089"

otp:
  expiry-check-interval-ms: 60000
```

### email.properties

```properties
email.username=test@example.com
email.password=
email.from=test@example.com
mail.smtp.host=localhost
mail.smtp.port=1025
mail.smtp.auth=false
mail.smtp.starttls.enable=false
```

### sms.properties

```properties
smpp.host=localhost
smpp.port=2775
smpp.system_id=bitsense
smpp.password=pass123
smpp.system_type=OTP
smpp.source_addr=OTPService
```