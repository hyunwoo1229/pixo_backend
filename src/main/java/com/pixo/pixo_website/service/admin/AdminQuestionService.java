package com.pixo.pixo_website.service.admin;

import com.pixo.pixo_website.domain.Question;
import com.pixo.pixo_website.domain.admin.Answer;
import com.pixo.pixo_website.dto.QuestionRequestDto;
import com.pixo.pixo_website.dto.admin.AnswerRequestDto;
import com.pixo.pixo_website.repository.QuestionRepository;
import com.pixo.pixo_website.repository.admin.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class AdminQuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public void updateQuestion(Long questionId, QuestionRequestDto dto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionRepository.delete(question);
    }

    public void writeAnswer(Long questionId, AnswerRequestDto dto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Answer answer = new Answer();
        answer.setContent(dto.getContent());
        answer.setQuestion(question);
        answerRepository.save(answer);

        question.setAnswered(true);
        questionRepository.save(question);
    }

    public void updateAnswer(Long answerId, AnswerRequestDto dto) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        answer.setContent(dto.getContent());
        answer.setUpdatedAt(LocalDateTime.now());
        answerRepository.save(answer);
    }

    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Question question = answer.getQuestion();
        question.setAnswered(false);
        questionRepository.save(question);
        answerRepository.delete(answer);
    }
}
