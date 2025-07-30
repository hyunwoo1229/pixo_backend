package com.pixo.pixo_website.repository.admin;

import com.pixo.pixo_website.domain.admin.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
