package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByLoginId(String loginId);
    Optional<MemberEntity> findByRefreshToken(String refreshToken);
}
