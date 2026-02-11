package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberId(Long memberId);
    List<Reservation> findByMemberNameContainingIgnoreCase(String name);
    List<Reservation> findByReservationCodeContainingIgnoreCase(String reservationCode);
    List<Reservation> findByDate(LocalDate date);

    @Query("select r from Reservation r join fetch r.member")
    List<Reservation> findAllWithMember();
}
