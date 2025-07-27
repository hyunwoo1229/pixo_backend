package com.pixo.pixo_website.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MemberDto {
    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;
    private String provider;

}
