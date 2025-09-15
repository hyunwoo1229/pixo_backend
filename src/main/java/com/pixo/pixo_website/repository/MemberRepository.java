package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByRefreshToken(String refreshToken);
    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);
    Optional<Member> findByLoginIdAndNameAndPhoneNumber(String loginId, String name, String phoneNumber);
    Optional<Member> findByPhoneNumber(String phoneNumber);
    List<Member> findByStatusAndDeletedAtBefore(MemberStatus status, LocalDateTime dateTime);
}
