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
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void deleteAnswer(Long answerId) {
       /* Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Question question = answer.getQuestion();
        question.setAnswer(null);
        question.setAnswered(false);
        questionRepository.save(question);
        answerRepository.delete(answer);

        */
        // 1. 삭제할 답변을 찾습니다.
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다."));

        // 2. 답변을 통해 부모인 질문을 찾습니다.
        Question question = answer.getQuestion();

        // 3. 질문의 답변 상태를 변경합니다.
        question.setAnswered(false);

        // 4. 질문과 답변의 연결을 끊습니다.
        // 이 코드가 실행되고 트랜잭션이 끝나면, orphanRemoval=true 설정 때문에
        // JPA가 자동으로 Answer 데이터를 DELETE 합니다.
        question.setAnswer(null);
    }
}
