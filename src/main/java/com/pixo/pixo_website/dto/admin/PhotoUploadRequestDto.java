package com.pixo.pixo_website.dto.admin;

import com.pixo.pixo_website.domain.admin.PhotoCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter

public class PhotoUploadRequestDto {
    private PhotoCategory category;
    private MultipartFile imageFile;

    public PhotoUploadRequestDto(String category, MultipartFile imageFile) {
        this.category = PhotoCategory.valueOf(category);
        this.imageFile = imageFile;
    }
}
