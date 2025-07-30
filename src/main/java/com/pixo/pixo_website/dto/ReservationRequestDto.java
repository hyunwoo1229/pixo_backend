package com.pixo.pixo_website.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class ReservationRequestDto {
    private String shootType;
    private LocalDate date;
    private Integer startTime;
    private Integer endTime;
    private String notes;
}
