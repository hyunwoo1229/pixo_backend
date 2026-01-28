package com.pixo.pixo_website.domain;

import com.pixo.pixo_website.domain.admin.Answer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity

public class Question
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private Boolean answered = false;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Answer answer;


    public static Question create(String title, String content, Member member) {
        Question question = new Question();
        question.title = title;
        question.content = content;
        question.member = member;
        return question;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void validateOwner(Long memberId) {
        if (!this.member.getId().equals(memberId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void addAnswer(Answer answer) {
        this.answer = answer;
        this.answered = true;
    }

    public void removeAnswer() {
        this.answer = null;
        this.answered = false;
    }
}
