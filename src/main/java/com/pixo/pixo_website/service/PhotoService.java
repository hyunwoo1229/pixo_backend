package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.domain.admin.PhotoCategory;
import com.pixo.pixo_website.dto.admin.PhotoResponseDto;
import com.pixo.pixo_website.repository.admin.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public List<PhotoResponseDto> getPhotosByCategory(String category) {
        // 1) String -> Enum 변환(잘못된 값이면 400)
        final PhotoCategory cat;
        try {
            cat = PhotoCategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않는 카테고리: " + category);
        }

        // 2) 1차 조회
        List<Photo> list = photoRepository.findByCategory(cat);

        // 3) (선택) *_MAIN 비었으면 기본 카테고리로 폴백
        if (list.isEmpty() && category.endsWith("_MAIN")) {
            try {
                PhotoCategory base = PhotoCategory.valueOf(category.replace("_MAIN", ""));
                list = photoRepository.findByCategory(base);
            } catch (IllegalArgumentException ignored) { /* 무시 */ }
        }

        // 4) DTO 매핑 (만든 생성자 사용)
        return list.stream()
                .map(PhotoResponseDto::new) // PhotoResponseDto(Photo p)
                .collect(Collectors.toList());
    }
}