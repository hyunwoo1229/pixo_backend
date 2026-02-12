package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.BlockedTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BlockedTimeRepository extends JpaRepository<BlockedTime, Long> {
    @Query("select b.timeSlot from BlockedTime b where b.blockedDate = :blockedDate")
    List<String> findTimeSlotsByBlockedDate(@Param("blockedDate") LocalDate blockedDate);
    Optional<BlockedTime> findByBlockedDateAndTimeSlot(LocalDate blockedDate, String timeSlot);

    @Transactional
    void deleteByBlockedDateAndTimeSlot(LocalDate blockedDate, String timeSlot);
}
