package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Reservation;
import com.pixo.pixo_website.dto.ReservationRequestDto;
import com.pixo.pixo_website.dto.ReservationResponseDto;
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

@Service
@RequiredArgsConstructor

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final RateLimiterService rateLimiterService;

    @Transactional
    public Reservation createReservation(String loginId, ReservationRequestDto dto) {

        String rateLimitKey = loginId + "_CREATE_RESERVATION";
        rateLimiterService.check(rateLimitKey, Duration.ofMinutes(10));

        // memberId 대신 loginId로 회원을 찾습니다.
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setShootType(dto.getShootType());
        reservation.setDate(dto.getDate());
        reservation.setTime(dto.getTime());
        reservation.setLocation(dto.getLocation());
        reservation.setNotes(dto.getNotes());
        reservation.setReservationCode(generateReservationCode());
        reservation.setDesiredShootDate(dto.getDesiredShootDate());

        Reservation saved = reservationRepository.save(reservation);


        // 이메일 내용 구성
        String subject = "[PIXO] 새 예약 알림";
        String body = String.format(
                "예약 코드: %s \n회원 이름: %s\n전화번호: %s\n\n촬영 종류: %s\n회의 날짜: %s\n회의 시간: %s\n희망 촬영 날짜: %s\n희망 촬영 장소: %s\n요청사항: %s",
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

    private String generateReservationCode() {
        // 예: 20250804AB12CD
        String datePart = LocalDate.now().toString().replace("-", ""); // YYYYMMDD
        String randomPart = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
        return datePart + randomPart;
    }

    @Transactional(readOnly = true)
    public List<String> getBookedTimes(LocalDate date) {
        return reservationRepository.findByDate(date).stream()
                .map(Reservation::getTime)
                .collect(Collectors.toList());
    }
}
