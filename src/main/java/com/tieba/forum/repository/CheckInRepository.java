package com.tieba.forum.repository;

import com.tieba.forum.entity.CheckIn;
import com.tieba.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    Optional<CheckIn> findByUserIdAndCheckInDate(Long userId, LocalDate date);
    List<CheckIn> findByUserIdOrderByCheckInDateDesc(Long userId);
    Optional<CheckIn> findTopByUserIdOrderByCheckInDateDesc(Long userId);
}
