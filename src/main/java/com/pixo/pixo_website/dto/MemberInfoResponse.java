package com.pixo.pixo_website.dto;

import com.pixo.pixo_website.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class MemberInfoResponse {

    private final String loginId;
    private final String name;

    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .loginId(member.getLoginId())
                .name(member.getName())
                .build();
    }
}
