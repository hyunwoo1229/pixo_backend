package com.pixo.pixo_website.controller.admin;

import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.dto.admin.PhotoRequestDto;
import com.pixo.pixo_website.service.admin.AdminPhotoService;
import com.pixo.pixo_website.service.admin.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/photo")
@RequiredArgsConstructor

public class AdminPhotoController {
    private final AdminPhotoService adminPhotoService;
    /*
    //사진 업로드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(@RequestPart("category") String category,
                                         @RequestPart("imageFile") MultipartFile imageFile) {
        PhotoRequestDto dto = new PhotoRequestDto(category, imageFile);
        adminPhotoService.uploadPhoto(dto);
        return ResponseEntity.ok(new SuccessResponse("사진이 업로드 되었습니다."));
    }
     */

    // 프론트엔드로부터 파일 업로드 허가 요청을 받음
    @PostMapping("/generate-signed-url")
    public ResponseEntity<Map<String, String>> generateSignedUrl(@RequestBody Map<String, String> payload) {
        String fileName = payload.get("fileName");
        String contentType = payload.get("contentType");
        return ResponseEntity.ok(adminPhotoService.generateSignedUrl(fileName, contentType));
    }

    // 프론트엔드로부터 GCS 업로드 완료 후 DB 저장 요청을 받음
    @PostMapping("/save-metadata")
    public ResponseEntity<Void> saveMetadata(@RequestBody PhotoRequestDto dto) {
        adminPhotoService.savePhotoMetadata(dto);
        return ResponseEntity.ok().build();
    }

    //사진 삭제
    @DeleteMapping("{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long photoId) {
        adminPhotoService.deletePhoto(photoId);
        return ResponseEntity.ok(new SuccessResponse("사진이 삭제 되었습니다."));
    }

    @PostMapping("/order")
    public void changeOrder(@RequestBody List<Long> photoIds) {
        adminPhotoService.updatePhotoOrder(photoIds);
    }

}
