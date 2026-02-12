package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //List<Reservation> findByMemberId(Long memberId);
    //List<Reservation> findByMemberNameContainingIgnoreCase(String name);
    //List<Reservation> findByReservationCodeContainingIgnoreCase(String reservationCode);
    List<Reservation> findByDate(LocalDate date);

    @Query("select r from Reservation r join fetch r.member where r.member.id = :memberId")
    List<Reservation> findByMemberId(@Param("memberId") Long memberId);

    @Query("select r from Reservation r join fetch r.member where lower(r.member.name) like lower(concat('%', :name, '%'))")
    List<Reservation> findByMemberNameContainingIgnoreCase(@Param("name") String name);

    @Query("select r from Reservation r join fetch r.member where lower(r.reservationCode) like lower(concat('%', :code, '%'))")
    List<Reservation> findByReservationCodeContainingIgnoreCase(@Param("code") String code);

    @Query("select r from Reservation r join fetch r.member")
    List<Reservation> findAllWithMember();
}
