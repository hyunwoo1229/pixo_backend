package com.pixo.pixo_website.repository.admin;

import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.domain.admin.PhotoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByCategory(PhotoCategory category);
    List<Photo> findByCategoryIn(List<PhotoCategory> categories);
    Optional<Photo> findFirstByCategory(PhotoCategory category);
}
