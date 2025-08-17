package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.domain.Reservation;
import com.pixo.pixo_website.dto.ReservationRequestDto;
import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor

public class ReservationController {
    private final ReservationService reservationService;

    //예약 생성
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(Authentication authentication,
                                                                    @RequestBody ReservationRequestDto dto) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자의 loginId
        Reservation reservation = reservationService.createReservation(loginId, dto);
        return ResponseEntity.ok(new ReservationResponseDto(reservation));
    }

    // ▼▼▼▼▼ 회원의 모든 예약 조회 API 수정 ▼▼▼▼▼
    @GetMapping("/my") // URL을 '/my'로 변경
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build(); // 인증 정보가 없으면 401 반환
        }
        String loginId = authentication.getName(); // 현재 로그인한 사용자의 loginId
        List<ReservationResponseDto> reservations = reservationService.getReservationsByLoginId(loginId);
        return ResponseEntity.ok(reservations);
    }
}
