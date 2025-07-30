package com.pixo.pixo_website.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class QuestionRequestDto {
    private String title;
    private String content;

    public QuestionRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
