package com.pixo.pixo_website.dto.admin;
import com.pixo.pixo_website.domain.admin.Photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDto {
    private Long id;
    private String imageUrl;
    private String category;
    private String originalFileName;

    public PhotoResponseDto(Photo photo) {
        this.id = photo.getId();
        this.imageUrl = photo.getImageUrl();
        this.category = photo.getCategory().name(); // enum → String
        this.originalFileName = photo.getOriginalFileName();
    }
}

