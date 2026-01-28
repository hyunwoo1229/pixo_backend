package com.pixo.pixo_website.domain;

import com.pixo.pixo_website.dto.ReservationRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Member member;

    private String shootType;
    private LocalDate date;
    private String time;
    private String location;
    private String notes;
    private String reservationCode;
    private String desiredShootDate;

    public static Reservation create(Member member, ReservationRequestDto dto) {
        Reservation reservation = new Reservation();
        reservation.member = member;
        reservation.shootType = dto.getShootType();
        reservation.date = dto.getDate();
        reservation.time = dto.getTime();
        reservation.location = dto.getLocation();
        reservation.notes = dto.getNotes();
        reservation.desiredShootDate = dto.getDesiredShootDate();
        reservation.reservationCode = generateCode();
        return reservation;
    }

    private static String generateCode() {
        String datePart = LocalDate.now().toString().replace("-", "");
        String randomPart = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
        return datePart + randomPart;
    }
}

