package com.pixo.pixo_website.dto.admin;

import com.pixo.pixo_website.domain.admin.PhotoCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhotoRequestDto {
    private String category;
    private String originalFileName;
    private String savedFileName;
}
