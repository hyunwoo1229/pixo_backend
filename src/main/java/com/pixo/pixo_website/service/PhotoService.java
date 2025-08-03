package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.admin.PhotoCategory;
import com.pixo.pixo_website.dto.admin.PhotoResponseDto;
import com.pixo.pixo_website.repository.admin.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class PhotoService {
    private final PhotoRepository photoRepository;

    public List<PhotoResponseDto> getPhotosByCategory(String category) {
        PhotoCategory cat = PhotoCategory.valueOf(category.toUpperCase());
        return photoRepository.findByCategory(cat).stream()
                .map(PhotoResponseDto::new)
                .toList();
    }
}
