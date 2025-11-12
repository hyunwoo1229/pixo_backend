package com.pixo.pixo_website.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponseDto {
    private List<PhotoResponseDto> mainPhotos;
    private List<PhotoResponseDto> generalPhotos;
}