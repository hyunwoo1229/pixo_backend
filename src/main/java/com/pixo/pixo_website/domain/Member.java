package com.pixo.pixo_website.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



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

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
