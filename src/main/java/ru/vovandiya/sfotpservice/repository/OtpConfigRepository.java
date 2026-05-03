package ru.vovandiya.sfotpservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vovandiya.sfotpservice.model.OtpConfig;

public interface OtpConfigRepository extends JpaRepository<OtpConfig, Long> {}