package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByMember(Member member);
    List<Question> findByTitleContainingIgnoreCase(String keyword);
    List<Question> findByContentContainingIgnoreCase(String keyword);

    @Query("select q from Question q " +
            "join fetch q.member " +          // 작성자 가져오기
            "left join fetch q.answer")
    List<Question> findAllWithMember();

}
