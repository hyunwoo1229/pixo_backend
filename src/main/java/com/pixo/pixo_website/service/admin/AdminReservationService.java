package com.pixo.pixo_website.service.admin;


import com.pixo.pixo_website.domain.BlockedTime;
import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.repository.BlockedTimeRepository;
import com.pixo.pixo_website.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AdminReservationService {
    private final ReservationRepository reservationRepository;
    private final BlockedTimeRepository blockedTimeRepository;

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

    public List<String> getAdminBlockedTimes(LocalDate date) {
        return blockedTimeRepository.findByBlockedDate(date).stream()
                .map(BlockedTime::getTimeSlot)
                .collect(Collectors.toList());
    }

    @Transactional
    public void blockTime(LocalDate date, String timeSlot) {
        blockedTimeRepository.findByBlockedDateAndTimeSlot(date, timeSlot).ifPresentOrElse(
                (blockedTime) -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 막혀있는 시간입니다."); },
                () -> {
                    BlockedTime newBlock = new BlockedTime();
                    newBlock.setBlockedDate(date);
                    newBlock.setTimeSlot(timeSlot);
                    blockedTimeRepository.save(newBlock);
                }
        );
    }

    @Transactional
    public void unblockTime(LocalDate date, String timeSlot) {
        blockedTimeRepository.deleteByBlockedDateAndTimeSlot(date, timeSlot);
    }

}
