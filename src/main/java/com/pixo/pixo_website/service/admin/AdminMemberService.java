package com.pixo.pixo_website.service.admin;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Role;
import com.pixo.pixo_website.dto.admin.AdminMemberRequestDto;
import com.pixo.pixo_website.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    dto.setId(member.getId());
                    dto.setLoginId(member.getLoginId());
                    dto.setName(member.getName());
                    dto.setPhoneNumber(member.getPhoneNumber());
                    dto.setRole(member.getRole());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMemberRole(Long memberId, Role newRole) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.setRole(newRole);
    }
}
