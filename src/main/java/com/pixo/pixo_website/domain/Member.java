package com.pixo.pixo_website.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;
    private String provider;

    @Column(length = 512)
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // 기본값: 일반 사용자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    private LocalDateTime deletedAt;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public void withdraw() {
        this.status = MemberStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        this.password = null;
        this.refreshToken = null;
        this.phoneNumber = null;
    }

    public String getMaskedLoginId() {
        if (this.loginId.length() > 4) {
            return this.loginId.substring(0, this.loginId.length() - 2) + "****";
        }
        return this.loginId;
    }
}
