package com.pixo.pixo_website.dto;

import com.pixo.pixo_website.domain.Reservation;
import lombok.Getter;

import java.time.LocalDate;

@Getter

public class ReservationResponseDto {
    private Long id;
    private String shootType;
    private LocalDate date;
    private Integer startTime;
    private Integer endTime;
    private String notes;
    private String memberName;

    public ReservationResponseDto(Reservation reservation) {
        this.id = reservation.getId();
        this.shootType = reservation.getShootType();
        this.date = reservation.getDate();
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.notes = reservation.getNotes();
        this.memberName = reservation.getMember().getName(); // 필요 시 name만 추출
    }
}
