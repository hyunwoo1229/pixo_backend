package com.pixo.pixo_website.dto;

import com.pixo.pixo_website.domain.Question;
import com.pixo.pixo_website.dto.admin.AnswerResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QuestionResponseDto {
    private Long id;
    private String title;
    private String content;
    private String memberName;
    private LocalDateTime createdAt;
    private boolean answered;
    private AnswerResponseDto answer;

    public QuestionResponseDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.answered = question.getAnswered();
        this.createdAt = question.getCreatedAt();

        if (question.getMember() != null) {
            this.memberName = question.getMember().getName();
        } else {
            this.memberName = "작성자 없음";
        }

        if (question.getAnswer() != null) {
            this.answer = new AnswerResponseDto(question.getAnswer());
        }
    }

    public static QuestionResponseDto fromEntity(Question question) {
        QuestionResponseDto dto = new QuestionResponseDto();
        dto.id = question.getId();
        dto.title = question.getTitle();
        dto.content = question.getContent();

        if (question.getMember() != null) {
            dto.memberName = question.getMember().getName();
        }

        dto.createdAt = question.getCreatedAt();
        dto.answered = question.getAnswered();
        return dto;
    }
}