package com.pixo.pixo_website.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BlockTimeRequestDto {
    private LocalDate date;
    private String timeSlot;
}
