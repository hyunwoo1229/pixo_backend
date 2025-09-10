package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberId(Long memberId);
    List<Reservation> findByMemberNameContainingIgnoreCase(String name);
    List<Reservation> findByReservationCodeContainingIgnoreCase(String reservationCode);
    List<Reservation> findByDate(LocalDate date);
}
