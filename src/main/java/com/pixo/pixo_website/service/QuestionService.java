package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Question;
import com.pixo.pixo_website.dto.QuestionRequestDto;
import com.pixo.pixo_website.repository.MemberRepository;
import com.pixo.pixo_website.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor

public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    public void createQuestion(QuestionRequestDto dto, Member member) {
        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setMember(member);
        questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getMyQuestions(Member member) {
        return questionRepository.findByMember(member);
    }

    @Transactional
    public void updateQuestion(Long id, QuestionRequestDto dto, Member member) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문의글이 존재하지 않습니다."));
        if (!question.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 문의글만 수정할 수 있습니다.");
        }

        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long id, Member member) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문의글이 존재하지 않습니다."));

        if (!question.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 문의글만 삭제할 수 있습니다.");
        }

        questionRepository.delete(question);
    }
}
