package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Question;
import com.pixo.pixo_website.dto.QuestionRequestDto;
import com.pixo.pixo_website.dto.QuestionResponseDto;
import com.pixo.pixo_website.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MailService mailService;

    public void createQuestion(QuestionRequestDto dto, Member member) {
        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setMember(member);
        questionRepository.save(question);

        // 이메일 전송
        String subject = "[PIXO] 새로운 문의가 등록되었습니다";
        String body = String.format(
                "회원 이름: %s\n제목: %s\n내용:\n%s",
                member.getName(),
                dto.getTitle(),
                dto.getContent()
        );

        mailService.sendReservationNotification("hynoo20011229@gmail.com", subject, body);
    }


    public List<QuestionResponseDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionResponseDto::new)
                .toList();
    }

    public List<QuestionResponseDto> getMyQuestions(Member member) {
         return questionRepository.findByMember(member).stream()
                .map(QuestionResponseDto::new)
                .toList();
    }

    @Transactional
    public void updateQuestion(Long questionId, QuestionRequestDto dto, Member member) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문의글이 존재하지 않습니다."));
        if (!question.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 문의글만 수정할 수 있습니다.");
        }

        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setUpdatedAt(LocalDateTime.now());
        questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId, Member member) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "문의글이 존재하지 않습니다."));

        if (!question.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 문의글만 삭제할 수 있습니다.");
        }

        questionRepository.delete(question);
    }

    public List<QuestionResponseDto> searchByTitle(String keyword) {
        return questionRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(QuestionResponseDto::new)
                .toList();
    }

    public List<QuestionResponseDto> searchByContent(String keyword) {
        return questionRepository.findByContentContainingIgnoreCase(keyword).stream()
                .map(QuestionResponseDto::new)
                .toList();
    }

}
