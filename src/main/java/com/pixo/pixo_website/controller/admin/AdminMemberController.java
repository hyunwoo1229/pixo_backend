package com.pixo.pixo_website.controller.admin;


import com.pixo.pixo_website.dto.MemberRequestDto;
import com.pixo.pixo_website.dto.admin.AdminMemberRequestDto;
import com.pixo.pixo_website.service.admin.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    @GetMapping
    public ResponseEntity<List<AdminMemberRequestDto>> getAllMembers() {
        return ResponseEntity.ok(adminMemberService.getAllMembers());
    }
}
