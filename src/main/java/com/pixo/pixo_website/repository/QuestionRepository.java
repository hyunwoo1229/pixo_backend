package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByMember(Member member);
}
