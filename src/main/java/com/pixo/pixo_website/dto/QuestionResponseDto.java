package com.pixo.pixo_website.dto;

import com.pixo.pixo_website.domain.Question;
import lombok.Getter;

@Getter
public class QuestionResponseDto {
    private String title;
    private String content;
    private boolean answered;

    public QuestionResponseDto(Question question) {
        this.title = question.getTitle();
        this.content = question.getContent();
        this.answered = question.getAnswered();
    }
}
