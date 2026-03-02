package com.tieba.forum.repository;

import com.tieba.forum.entity.Captcha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CaptchaRepository extends JpaRepository<Captcha, Long> {
    Optional<Captcha> findByCaptchaId(String captchaId);
    Optional<Captcha> findByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
