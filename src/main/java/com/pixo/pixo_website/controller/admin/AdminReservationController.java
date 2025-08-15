package com.pixo.pixo_website.controller.admin;

import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.service.admin.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservation")
@RequiredArgsConstructor

public class AdminReservationController {
    private final AdminReservationService adminReservationService;

    // 전체 예약 조회
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getAllReservations() {
        return ResponseEntity.ok(adminReservationService.getAllReservations());
    }

    // 이름으로 검색
    @GetMapping("/search/name")
    public ResponseEntity<List<ReservationResponseDto>> searchByMemberName(@RequestParam String name) {
        return ResponseEntity.ok(adminReservationService.searchByMemberName(name));
    }

    // 예약번호로 검색
    @GetMapping("/search/code")
    public ResponseEntity<List<ReservationResponseDto>> searchByReservationCode(@RequestParam String code) {
        return ResponseEntity.ok(adminReservationService.searchByReservationCode(code));
    }

}
