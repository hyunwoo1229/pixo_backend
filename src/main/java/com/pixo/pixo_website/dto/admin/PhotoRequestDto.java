package com.pixo.pixo_website.dto.admin;

import com.pixo.pixo_website.domain.admin.PhotoCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter

public class PhotoRequestDto {
    private PhotoCategory category;
    private MultipartFile imageFile;

    public PhotoRequestDto(String category, MultipartFile imageFile) {
        this.category = PhotoCategory.valueOf(category);
        this.imageFile = imageFile;
    }
}
