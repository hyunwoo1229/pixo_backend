package com.pixo.pixo_website.dto;

import com.pixo.pixo_website.domain.Reservation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationResponseDto {
    private Long id;
    private String shootType;
    private LocalDate date;
    private String time;
    private String location;
    private String notes;
    private String memberName;
    private String reservationCode;
    private String desiredShootDate;

    public ReservationResponseDto(Reservation reservation) {
        this.id = reservation.getId();
        this.shootType = reservation.getShootType();
        this.date = reservation.getDate();
        this.time = reservation.getTime();
        this.location = reservation.getLocation(); // 생성자에서 값을 설정합니다.
        this.notes = reservation.getNotes();
        this.memberName = reservation.getMember().getName();
        this.reservationCode = reservation.getReservationCode();
        this.desiredShootDate = reservation.getDesiredShootDate();
    }
}
