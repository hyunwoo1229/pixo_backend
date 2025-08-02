package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.domain.Reservation;
import com.pixo.pixo_website.dto.ReservationRequestDto;
import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor

public class ReservationController {
    private final ReservationService reservationService;

    //예약 생성
    @PostMapping("/{memberId}")
    public ResponseEntity<ReservationResponseDto> createReservation(@PathVariable Long memberId,
                                                                    @RequestBody ReservationRequestDto dto) {
        Reservation reservation = reservationService.createReservation(memberId, dto);
        return ResponseEntity.ok(new ReservationResponseDto(reservation));
    }

    //회원의 모든 예약 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsByMember(@PathVariable Long memberId) {
        List<ReservationResponseDto> reservations = reservationService.getReservationsByMember(memberId);
        return ResponseEntity.ok(reservations);
    }
}
