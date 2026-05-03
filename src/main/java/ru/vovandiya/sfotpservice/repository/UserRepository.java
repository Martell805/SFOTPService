package ru.vovandiya.sfotpservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vovandiya.sfotpservice.model.Role;
import ru.vovandiya.sfotpservice.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByRole(Role role);
}