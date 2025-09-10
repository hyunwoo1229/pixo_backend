package com.pixo.pixo_website.controller.admin;

import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.dto.admin.BlockTimeRequestDto;
import com.pixo.pixo_website.service.admin.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/blocked-times")
    public ResponseEntity<List<String>> getBlockedTimes(@RequestParam LocalDate date) {
        return ResponseEntity.ok(adminReservationService.getAdminBlockedTimes(date));
    }

    @PostMapping("/block-time")
    public ResponseEntity<SuccessResponse> blockTime(@RequestBody BlockTimeRequestDto dto) {
        adminReservationService.blockTime(dto.getDate(), dto.getTimeSlot());
        return ResponseEntity.ok(new SuccessResponse("해당 시간이 예약 불가 처리되었습니다."));
    }

    @DeleteMapping("/block-time")
    public ResponseEntity<SuccessResponse> unblockTime(@RequestBody BlockTimeRequestDto dto) {
        adminReservationService.unblockTime(dto.getDate(), dto.getTimeSlot());
        return ResponseEntity.ok(new SuccessResponse("해당 시간이 다시 예약 가능 처리되었습니다."));
    }
}
