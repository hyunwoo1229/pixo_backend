package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberId(Long memberId);
    // 이름으로 검색
    @Query("SELECT r FROM Reservation r WHERE LOWER(r.member.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Reservation> searchByMemberName(@Param("name") String name);

    // 예약번호로 검색
    List<Reservation> findByReservationCodeContainingIgnoreCase(String reservationCode);
}
