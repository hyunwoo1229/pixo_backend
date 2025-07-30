package com.pixo.pixo_website.service.admin;

import com.pixo.pixo_website.dto.MemberRequestDto;
import com.pixo.pixo_website.dto.admin.AdminMemberRequestDto;
import com.pixo.pixo_website.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AdminMemberService {
    private final MemberRepository memberRepository;

    public List<AdminMemberRequestDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(member -> {
                    AdminMemberRequestDto dto = new AdminMemberRequestDto();
                    dto.setLoginId(member.getLoginId());
                    dto.setName(member.getName());
                    dto.setPhoneNumber(member.getPhoneNumber());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
