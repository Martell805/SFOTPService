package ru.vovandiya.sfotpservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vovandiya.sfotpservice.enums.OtpStatus;
import ru.vovandiya.sfotpservice.model.OtpCode;

import java.util.List;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    List<OtpCode> findByUserIdAndStatus(Long userId, OtpStatus status);

    List<OtpCode> findByStatus(OtpStatus status);

    Optional<OtpCode> findByCodeAndOperationIdAndStatus(
            String code, String operationId, OtpStatus status);

    List<OtpCode> findByUserId(Long userId);
}