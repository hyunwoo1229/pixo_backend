package com.pixo.pixo_website.service.admin;


import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AdminReservationService {
    private final ReservationRepository reservationRepository;

    // 전체 예약 조회
    public List<ReservationResponseDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }

    // 이름으로 검색
    public List<ReservationResponseDto> searchByMemberName(String name) {
        return reservationRepository.findByMemberNameContainingIgnoreCase(name).stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }

    // 예약번호로 검색
    public List<ReservationResponseDto> searchByReservationCode(String code) {
        return reservationRepository.findByReservationCodeContainingIgnoreCase(code).stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
}
