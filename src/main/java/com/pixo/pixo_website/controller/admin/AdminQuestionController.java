package com.pixo.pixo_website.controller.admin;

import com.pixo.pixo_website.dto.QuestionRequestDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.dto.admin.AnswerRequestDto;
import com.pixo.pixo_website.service.admin.AdminQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final AdminQuestionService adminQuestionService;

    // 문의글 수정
    @PatchMapping("/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId,
                                            @RequestBody QuestionRequestDto dto) {
        adminQuestionService.updateQuestion(questionId, dto);
        return ResponseEntity.ok(new SuccessResponse("문의글이 수정되었습니다."));
    }

    // 문의글 삭제
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        adminQuestionService.deleteQuestion(questionId);
        return ResponseEntity.ok(new SuccessResponse("문의글이 삭제되었습니다."));
    }

    // 답변 등록
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> writeAnswer(@PathVariable Long questionId,
                                         @RequestBody AnswerRequestDto dto) {
        adminQuestionService.writeAnswer(questionId, dto);
        return ResponseEntity.ok(new SuccessResponse("답변이 등록되었습니다."));
    }

    // 답변 수정
    @PatchMapping("/answer/{answerId}")
    public ResponseEntity<?> updateAnswer(@PathVariable Long answerId,
                                          @RequestBody AnswerRequestDto dto) {
        adminQuestionService.updateAnswer(answerId, dto);
        return ResponseEntity.ok(new SuccessResponse("답변이 수정되었습니다."));
    }

    // 답변 삭제
    @DeleteMapping("/answer/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId) {
        adminQuestionService.deleteAnswer(answerId);
        return ResponseEntity.ok(new SuccessResponse("답변이 삭제되었습니다."));
    }

}