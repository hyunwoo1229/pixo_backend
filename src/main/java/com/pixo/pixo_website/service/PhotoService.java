package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.domain.admin.PhotoCategory;
import com.pixo.pixo_website.dto.admin.CategoryDetailResponseDto;
import com.pixo.pixo_website.dto.admin.PhotoResponseDto;
import com.pixo.pixo_website.repository.admin.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        // [수정] Asc -> Desc (큰 숫자가 먼저 나옴 = 최신순/지정순서)
        List<Photo> list = photoRepository.findByCategoryOrderBySequenceDesc(cat);

        // 3) (선택) *_MAIN 비었으면 기본 카테고리로 폴백
        if (list.isEmpty() && category.endsWith("_MAIN")) {
            try {
                PhotoCategory base = PhotoCategory.valueOf(category.replace("_MAIN", ""));
                // [수정] 폴백 데이터도 내림차순 정렬
                list = photoRepository.findByCategoryOrderBySequenceDesc(base);
            } catch (IllegalArgumentException ignored) { /* 무시 */ }
        }

        // 4) DTO 매핑
        return list.stream()
                .map(PhotoResponseDto::new)
                .collect(Collectors.toList());
    }

    public Map<String, PhotoResponseDto> getHomePhotos() {
        Map<String, PhotoResponseDto> homePhotos = new HashMap<>();
        List<PhotoCategory> mainCategories = List.of(
                PhotoCategory.REPRESENTATIVE, PhotoCategory.LANDSCAPE_MAIN,
                PhotoCategory.PRODUCT_MAIN, PhotoCategory.FOOD_MAIN, PhotoCategory.WEDDING_MAIN,
                PhotoCategory.FASHION_MAIN, PhotoCategory.CAR_MAIN, PhotoCategory.DRONE_LANDSCAPE_MAIN
        );

        List<Photo> photos = photoRepository.findByCategoryIn(mainCategories);
        Map<PhotoCategory, Photo> photoMap = photos.stream()
                .collect(Collectors.toMap(Photo::getCategory, p -> p, (p1, p2) -> p1));

        mainCategories.forEach(cat -> {
            Optional<Photo> photoOpt = Optional.ofNullable(photoMap.get(cat));

            if (photoOpt.isEmpty() && cat.name().endsWith("_MAIN")) {
                try {
                    PhotoCategory baseCat = PhotoCategory.valueOf(cat.name().replace("_MAIN", ""));
                    photoOpt = photoRepository.findFirstByCategoryOrderBySequenceDesc(baseCat);
                } catch (IllegalArgumentException ignored) {}
            }

            photoOpt.ifPresent(p -> homePhotos.put(cat.name(), new PhotoResponseDto(p)));
        });

        return homePhotos;
    }

    public CategoryDetailResponseDto getCategoryDetailPhotos(String categoryId) {
        try {
            PhotoCategory baseCategory = PhotoCategory.valueOf(categoryId);
            PhotoCategory mainCategory = PhotoCategory.valueOf(categoryId + "_MAIN");

            List<Photo> mainPhotoEntities = photoRepository.findByCategoryOrderBySequenceDesc(mainCategory);
            List<Photo> generalPhotoEntities = photoRepository.findByCategoryOrderBySequenceDesc(baseCategory);

            List<PhotoResponseDto> mainPhotosDto = mainPhotoEntities.stream()
                    .map(PhotoResponseDto::new)
                    .collect(Collectors.toList());

            List<PhotoResponseDto> generalPhotosDto = generalPhotoEntities.stream()
                    .map(PhotoResponseDto::new)
                    .collect(Collectors.toList());

            return new CategoryDetailResponseDto(mainPhotosDto, generalPhotosDto);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않는 카테고리: " + categoryId);
        }
    }
}