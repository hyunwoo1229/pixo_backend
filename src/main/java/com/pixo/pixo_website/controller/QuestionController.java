package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.dto.QuestionRequestDto;
import com.pixo.pixo_website.dto.QuestionResponseDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.security.CumstomUserDetails;
import com.pixo.pixo_website.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor

public class QuestionController {
    private final QuestionService questionService;

    //문의 작성
    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody QuestionRequestDto dto,
                                            @AuthenticationPrincipal CumstomUserDetails userDetails) {
        Member member = userDetails.getMember();
        questionService.createQuestion(dto, member);
        return ResponseEntity.ok(new SuccessResponse("문의가 등록되었습니다."));
    }

    //내 문의 목록 조회
    @GetMapping("/me")
    public ResponseEntity<List<QuestionResponseDto>> getMyQuestions(@AuthenticationPrincipal CumstomUserDetails userDetails) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(questionService.getMyQuestions(member));
    }

    //전체 문의 목록 조회
    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    //문의 수정
    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateQuestion(@PathVariable Long id,
                                                          @RequestBody QuestionRequestDto dto,
                                                          @AuthenticationPrincipal CumstomUserDetails userDetails) {
        questionService.updateQuestion(id, dto, userDetails.getMember());
        return ResponseEntity.ok(new SuccessResponse("문의가 수정되었습니다."));
    }

    //문의 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteQuestion(@PathVariable Long id,
                                            @AuthenticationPrincipal CumstomUserDetails userDetails) {
        questionService.deleteQuestion(id, userDetails.getMember());
        return ResponseEntity.ok(new SuccessResponse("문의가 삭제되었습니다."));
    }

    //문의 검색
    @GetMapping("/search")
    public ResponseEntity<List<QuestionResponseDto>> searchQuestions(
            @RequestParam String type,
            @RequestParam String keyword) {
        if ("content".equals(type)) {
            return ResponseEntity.ok(questionService.searchByContent(keyword));
        }
        return ResponseEntity.ok(questionService.searchByTitle(keyword));
    }

}
