package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Reservation;
import com.pixo.pixo_website.dto.ReservationRequestDto;
import com.pixo.pixo_website.repository.MemberRepository;
import com.pixo.pixo_website.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

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

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByMember(Long memberId) {
        return reservationRepository.findByMemberId(memberId);
    }
}
