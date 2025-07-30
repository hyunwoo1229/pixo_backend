package com.pixo.pixo_website.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminMemberRequestDto {
    private String loginId;
    private String name;
    private String phoneNumber;
}
