package com.pixo.pixo_website.dto.admin;

import com.pixo.pixo_website.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminMemberRequestDto {
    private String loginId;
    private String name;
    private String phoneNumber;
    private Long id;
    private Role role;
}
