package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberId(Long memberId);
    List<Reservation> findByMemberNameContainingIgnoreCase(String name);
    List<Reservation> findByReservationCodeContainingIgnoreCase(String reservationCode);
}
