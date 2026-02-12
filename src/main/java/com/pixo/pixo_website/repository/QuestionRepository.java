package com.pixo.pixo_website.repository;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 내 문의 목록 조회 시에도 답변을 가져오도록 수정
    @Query("select q from Question q " +
            "join fetch q.member " +
            "left join fetch q.answer " +
            "where q.member = :member")
    List<Question> findByMember(@Param("member") Member member);

    // 제목 검색 시에도 답변을 가져오도록 수정
    @Query("select q from Question q " +
            "join fetch q.member " +
            "left join fetch q.answer " +
            "where lower(q.title) like lower(concat('%', :keyword, '%'))")
    List<Question> findByTitleWithAnswer(@Param("keyword") String keyword);

    // 내용 검색 시에도 답변을 가져오도록 수정
    @Query("select q from Question q " +
            "join fetch q.member " +
            "left join fetch q.answer " +
            "where lower(q.content) like lower(concat('%', :keyword, '%'))")
    List<Question> findByContentWithAnswer(@Param("keyword") String keyword);

    @Query("select q from Question q " +
            "join fetch q.member " +
            "left join fetch q.answer")
    List<Question> findAllWithMember();
}
