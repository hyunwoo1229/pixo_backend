package com.pixo.pixo_website.repository.admin;

import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.domain.admin.PhotoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByCategory(PhotoCategory category);
    List<Photo> findByCategoryIn(List<PhotoCategory> categories);
    Optional<Photo> findFirstByCategory(PhotoCategory category);
    List<Photo> findByCategoryOrderBySequenceAsc(PhotoCategory category);
    // 해당 카테고리의 sequence 최댓값을 가져옴 (사진이 하나도 없으면 null 반환)
    @Query("SELECT MAX(p.sequence) FROM Photo p WHERE p.category = :category")
    Integer findMaxSequenceByCategory(@Param("category") PhotoCategory category);

    List<Photo> findByCategoryOrderBySequenceDesc(PhotoCategory category);

    Optional<Photo> findFirstByCategoryOrderBySequenceDesc(PhotoCategory category);

}
