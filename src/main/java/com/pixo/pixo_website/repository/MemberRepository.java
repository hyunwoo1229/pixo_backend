package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByRefreshToken(String refreshToken);
    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);
}
