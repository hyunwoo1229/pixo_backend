package com.pixo.pixo_website.controller.admin;

import com.pixo.pixo_website.domain.Role;
import com.pixo.pixo_website.dto.admin.AdminMemberRequestDto;
import com.pixo.pixo_website.service.admin.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{memberId}/role")
    public ResponseEntity<String> updateMemberRole(
            @PathVariable Long memberId,
            @RequestParam Role role
    ) {
        adminMemberService.updateMemberRole(memberId, role);
        return ResponseEntity.ok("권한이 변경되었습니다.");
    }
}
