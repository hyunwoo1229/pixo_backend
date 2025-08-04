package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Reservation;
import com.pixo.pixo_website.dto.ReservationRequestDto;
import com.pixo.pixo_website.dto.ReservationResponseDto;
import com.pixo.pixo_website.repository.MemberRepository;
import com.pixo.pixo_website.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MailService mailService;

    public Reservation createReservation(Long memberId, ReservationRequestDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setShootType(dto.getShootType());
        reservation.setDate(dto.getDate());
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setNotes(dto.getNotes());
        reservation.setReservationCode(generateReservationCode());

        Reservation saved = reservationRepository.save(reservation);

        // 이메일 내용 구성
        String subject = "[PIXO] 새 예약 알림";
        String body = String.format(
                "예약 코드: %s \n회원 이름: %s\n전화번호: %s\n\n촬영 종류: %s\n날짜: %s\n시간: %04d ~ %04d\n요청사항: %s",
                reservation.getReservationCode(),
                member.getName(),
                member.getPhoneNumber(),
                reservation.getShootType(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getNotes()
        );

        // 이메일 전송
        mailService.sendReservationNotification("hynoo20011229@gmail.com", subject, body);

        return saved;
    }

    public List<ReservationResponseDto> getReservationsByMember(Long memberId) {
        return reservationRepository.findByMemberId(memberId).stream()
                .map(ReservationResponseDto::new)
                .toList();
    }

    private String generateReservationCode() {
        // 예: 20250804AB12CD
        String datePart = LocalDate.now().toString().replace("-", ""); // YYYYMMDD
        String randomPart = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
        return datePart + randomPart;
    }
}
