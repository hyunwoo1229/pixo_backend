package com.pixo.pixo_website.controller.admin;

import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.dto.admin.PhotoUploadRequestDto;
import com.pixo.pixo_website.service.admin.AdminPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/photo")
@RequiredArgsConstructor

public class AdminPhotoController {
    private final AdminPhotoService adminPhotoService;

    //사진 업로드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(@RequestPart("category") String category,
                                         @RequestPart("imageFile") MultipartFile imageFile) {
        PhotoUploadRequestDto dto = new PhotoUploadRequestDto(category, imageFile);
        adminPhotoService.uploadPhoto(dto);
        return ResponseEntity.ok(new SuccessResponse("사진이 업로드 되었습니다."));
    }

    //사진 삭제
    @DeleteMapping("{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long photoId) {
        adminPhotoService.deletePhoto(photoId);
        return ResponseEntity.ok(new SuccessResponse("사진이 삭제 되었습니다."));
    }

    //전체 사진 조회
    @GetMapping("/all")
    public ResponseEntity<List<Photo>> getAllPhotos() {
        return ResponseEntity.ok(adminPhotoService.getAllPhotos());
    }

}
