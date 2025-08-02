package com.pixo.pixo_website.dto.admin;
import com.pixo.pixo_website.domain.admin.Photo;

import lombok.Getter;

@Getter

public class PhotoResponseDto {
    private String imageUrl;
    private String category;
    private String originalFileName;

    public PhotoResponseDto(Photo photo) {
        this.imageUrl = photo.getImageUrl();
        this.category = photo.getCategory().name(); // enum → String
        this.originalFileName = photo.getOriginalFileName();
    }
}

