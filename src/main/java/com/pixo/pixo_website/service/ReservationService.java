package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.BlockedTime;
import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Reservation;
import com.pixo.pixo_website.dto.ReservationRequestDto;
import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.repository.BlockedTimeRepository;
import com.pixo.pixo_website.repository.MemberRepository;
import com.pixo.pixo_website.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final RateLimiterService rateLimiterService;
    private final BlockedTimeRepository blockedTimeRepository;

    @Transactional
    public Reservation createReservation(String loginId, ReservationRequestDto dto) {

        String rateLimitKey = loginId + "_CREATE_RESERVATION";
        rateLimiterService.check(rateLimitKey, Duration.ofMinutes(10));

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        Reservation reservation = Reservation.create(member, dto);
        Reservation saved = reservationRepository.save(reservation);

        // 이메일 내용 구성
        String subject = "[PIXO] 새 예약 알림";
        String body = String.format(
                "예약 코드: %s \n\n회원 이름: %s\n\n전화번호: %s\n\n\n촬영 종류: %s\n\n회의 날짜: %s\n\n회의 시간: %s\n\n희망 촬영 날짜: %s\n\n희망 촬영 장소: %s\n\n요청사항: %s",
                saved.getReservationCode(),
                member.getName(),
                member.getPhoneNumber(),
                saved.getShootType(),
                saved.getDate(),
                saved.getTime(),
                saved.getDesiredShootDate(),
                saved.getLocation(),
                saved.getNotes()
        );

        // 이메일 전송
        mailService.sendReservationNotification("hynoo20011229@gmail.com", subject, body);

        return saved;
    }


    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getReservationsByLoginId(String loginId) {
        // 1. loginId로 Member 정보를 먼저 조회합니다.
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        // 2. 조회된 Member의 ID를 사용하여 예약을 조회합니다.
        return reservationRepository.findByMemberId(member.getId()).stream()
                .map(ReservationResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getBookedTimes(LocalDate date) {

        // 1. 해당 날짜에 실제 예약된 시간 목록 조회
        List<String> reservedTimes = reservationRepository.findByDate(date).stream()
                .map(Reservation::getTime)
                .toList();

        // 2. 해당 날짜에 관리자가 막은 시간 목록 조회
        List<String> adminBlockedTimes = blockedTimeRepository.findByBlockedDate(date).stream()
                .map(BlockedTime::getTimeSlot)
                .toList();

        // 3. 두 목록을 합치고 중복을 제거하여 반환
        List<String> combinedList = Stream.concat(reservedTimes.stream(), adminBlockedTimes.stream())
                .distinct()
                .collect(Collectors.toList());
        return combinedList;
    }
}
