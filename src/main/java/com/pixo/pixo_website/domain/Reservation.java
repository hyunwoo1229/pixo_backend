package com.pixo.pixo_website.domain;

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
    private Integer startTime;
    private Integer endTime;
    private String notes;
    private String reservationCode;
}
