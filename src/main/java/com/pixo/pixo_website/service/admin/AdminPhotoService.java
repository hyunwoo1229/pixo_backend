package com.pixo.pixo_website.service.admin;

import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.dto.admin.PhotoRequestDto;
import com.pixo.pixo_website.dto.admin.PhotoResponseDto;
import com.pixo.pixo_website.repository.admin.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AdminPhotoService {
    private final PhotoRepository photoRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public void uploadPhoto(PhotoRequestDto dto) {
        MultipartFile file = dto.getImageFile();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + extension;

        File targetFile = new File(uploadDir + savedName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }

        Photo photo = new Photo();
        photo.setCategory(dto.getCategory());
        photo.setImageUrl("/uploads/" + savedName);
        photo.setOriginalFileName(originalFilename);
        photo.setSavedFileName(savedName);
        photoRepository.save(photo);
    }

    public void deletePhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사진이 존재하지 않습니다."));

        //실제 파일 경로
        Path filePath = Paths.get(uploadDir + photo.getSavedFileName());

        //파일 삭제
        try{
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }

        //db에서 삭제
        photoRepository.delete(photo);
    }

    public List<PhotoResponseDto> getAllPhotos() {
        return photoRepository.findAll().stream()
                .map(PhotoResponseDto::new)
                .toList();
    }

}
