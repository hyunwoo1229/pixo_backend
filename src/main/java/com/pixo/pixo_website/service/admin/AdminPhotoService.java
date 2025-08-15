package com.pixo.pixo_website.service.admin;

import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.dto.admin.PhotoRequestDto;
import com.pixo.pixo_website.repository.admin.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPhotoService {
    private final PhotoRepository photoRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Photo uploadPhoto(PhotoRequestDto dto) {
        MultipartFile file = dto.getImageFile();
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 없습니다.");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("upload");
        String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
        String savedName = UUID.randomUUID() + ext;

        try {
            // ✅ 경로 안전하게 생성 + 디렉터리 보장
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(base);
            Path target = base.resolve(savedName);

            file.transferTo(target.toFile());
            log.info("Saved file to {}", target);
        } catch (IOException e) {
            log.error("파일 저장 중 오류", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 중 오류", e);
        }

        Photo photo = new Photo();
        // 엔티티가 enum이면 이렇게, 문자열이면 그대로 저장
        // photo.setCategory(PhotoCategory.valueOf(dto.getCategory()));
        photo.setCategory(dto.getCategory());

        photo.setImageUrl("/uploads/" + savedName);
        photo.setOriginalFileName(original);
        photo.setSavedFileName(savedName);

        return photoRepository.save(photo);
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



}
