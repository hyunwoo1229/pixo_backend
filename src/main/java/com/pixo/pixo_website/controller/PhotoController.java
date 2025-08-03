package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.dto.admin.PhotoResponseDto;
import com.pixo.pixo_website.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor

public class PhotoController {

    private final PhotoService photoService;

    //카테고리 별 사진 조회
    @GetMapping("/category")
    public ResponseEntity<List<PhotoResponseDto>> getPhotosByCategory(@RequestParam("category") String category) {
        return ResponseEntity.ok(photoService.getPhotosByCategory(category));
    }
}
