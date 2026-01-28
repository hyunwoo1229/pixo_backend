package com.pixo.pixo_website.service.admin;

import com.google.cloud.storage.*;
import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.domain.admin.PhotoCategory;
import com.pixo.pixo_website.dto.admin.PhotoRequestDto;
import com.pixo.pixo_website.repository.admin.PhotoRepository;
import io.awspring.cloud.s3.S3Template;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

import com.google.cloud.storage.Acl.Role;
import org.springframework.http.MediaType;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPhotoService {
    private final PhotoRepository photoRepository;
    //private final Storage storage;
    //private final StorageService storageService;
    private final S3Template s3Template;


    //@Value("${cloud.storage.bucket-name}")
    //private String bucketName;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 1. 업로드용 Presigned URL 생성 (GCS의 generateSignedUrl 대체)
    @Transactional
    public Map<String, String> generateSignedUrl(String fileName, String contentType) {
        // 고유한 파일명 생성 (GCS 로직 유지)
        String objectKey = "photos/" + UUID.randomUUID() + "_" + fileName;

        // S3 PUT 전용 Presigned URL 생성 (유효시간 10분)
        URL presignedUrl = s3Template.createSignedPutURL(bucketName, objectKey, Duration.ofMinutes(10));

        Map<String, String> response = new HashMap<>();
        response.put("signedUrl", presignedUrl.toString());
        response.put("savedFileName", objectKey); // DB에 저장될 S3 Key

        return response;
    }

    // 2. DB 메타데이터 저장 (S3 URL 체계 적용)
    @Transactional
    public void savePhotoMetadata(PhotoRequestDto dto) {
        // AWS S3 표준 URL 구성
        String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, dto.getSavedFileName());

        Photo photo = new Photo();
        photo.setCategory(PhotoCategory.valueOf(dto.getCategory()));
        photo.setImageUrl(publicUrl);
        photo.setOriginalFileName(dto.getOriginalFileName());
        photo.setSavedFileName(dto.getSavedFileName());

        // 순서 정렬 로직 (기존 유지)
        Integer maxSeq = photoRepository.findMaxSequenceByCategory(photo.getCategory());
        photo.setSequence((maxSeq == null) ? 0 : maxSeq + 1);

        photoRepository.save(photo);
    }

    // 3. 사진 삭제 (S3 객체 삭제)
    @Transactional
    public void deletePhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        try {
            // GCS storage.delete 대체
            s3Template.deleteObject(bucketName, photo.getSavedFileName());
            log.info("S3 객체 삭제 성공: {}", photo.getSavedFileName());
        } catch (Exception e) {
            log.error("S3 삭제 실패", e);
            throw new RuntimeException("S3 파일 삭제 중 오류 발생");
        }

        photoRepository.delete(photo);
    }

    // 사진 순서 변경 로직
    @Transactional
    public void updatePhotoOrder(List<Long> photoIds) {
        int size = photoIds.size();

        for (int i = 0; i < size; i++) {
            Long id = photoIds.get(i);
            Photo photo = photoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사진을 찾을 수 없습니다."));

            // 순서를 역순 혹은 정순으로 설정 (기존 로직에 맞게 조정)
            photo.setSequence(size - 1 - i);

            photoRepository.save(photo);
        }
    }

    /*public Photo uploadPhoto(PhotoRequestDto dto) {
        MultipartFile file = dto.getImageFile();
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 없습니다.");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("upload");
        String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")).toLowerCase() : "";
        String objectName = "photos/" + UUID.randomUUID() + ext;

        String mimeType = Optional.ofNullable(file.getContentType())
                .filter(type -> !type.isEmpty())
                .orElseGet(() -> {
                    if (ext.equals(".png")) return MediaType.IMAGE_PNG_VALUE;
                    if (ext.equals(".jpg") || ext.equals(".jpeg")) return MediaType.IMAGE_JPEG_VALUE;
                    return "application/octet-stream";
                });

        try {
            //BlobInfo 빌더를 사용하여 setAcl 추가
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                    .setContentType(mimeType)
                    .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Role.READER)))
                    .build();

            // 2. Cloud Storage에 업로드 (MultipartFile의 바이트 데이터 사용)
            storage.create(blobInfo, file.getBytes());

            // 3. 이미지 URL 생성
            String publicUrl = String.format("https://storage.googleapis.com/%s/%s",
                    bucketName, objectName);

            log.info("Saved file to Cloud Storage at {}", publicUrl);

            Photo photo = new Photo();
            photo.setCategory(dto.getCategory());
            photo.setImageUrl(publicUrl);
            photo.setOriginalFileName(original);
            photo.setSavedFileName(objectName);

            return photoRepository.save(photo);
        } catch (IOException e) {
            log.error("파일 데이터 처리 중 오류", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류", e);
        } catch (StorageException e) {
            log.error("Cloud Storage 업로드 중 오류", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "클라우드 저장소 업로드 오류", e);
        }
    }

     */

    /*
    public Map<String, String> generateSignedUrl(String fileName, String contentType) {
        //  StorageService를 통해 Signed URL 생성
        String signedUrl = storageService.generateSignedUrlForUpload(fileName, contentType);

        //  GCS에 저장될 고유 파일명 파싱 (컨트롤러에 있던 로직)
        // URL의 '?' 이전까지가 파일 경로이며, bucketName과 그 뒤의 '/'를 제외한 부분이 파일명
        int start = signedUrl.indexOf(bucketName + "/") + bucketName.length() + 1;
        int end = signedUrl.indexOf("?");
        String savedFileName = signedUrl.substring(start, end);

        //  응답 데이터 가공 (컨트롤러에 있던 로직)
        Map<String, String> response = new HashMap<>();
        response.put("signedUrl", signedUrl);
        response.put("savedFileName", savedFileName);

        return response;
    }

    public void savePhotoMetadata(PhotoRequestDto dto) {
        String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, dto.getSavedFileName());

        Photo photo = new Photo();
        photo.setCategory(PhotoCategory.valueOf(dto.getCategory()));
        photo.setImageUrl(publicUrl);
        photo.setOriginalFileName(dto.getOriginalFileName());
        photo.setSavedFileName(dto.getSavedFileName());

        Integer maxSeq = photoRepository.findMaxSequenceByCategory(photo.getCategory());
        int nextSequence = (maxSeq == null) ? 0 : maxSeq + 1;
        photo.setSequence(nextSequence);

        photoRepository.save(photo);
    }

    public void deletePhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사진이 존재하지 않습니다."));

        // DB에 저장된 Cloud Storage 객체 이름(경로)을 가져옴
        String objectName = photo.getSavedFileName();

        try{
            //  Cloud Storage 객체 삭제
            boolean deleted = storage.delete(bucketName, objectName);

            if (deleted) {
                log.info("Cloud Storage에서 객체 {} 삭제 완료.", objectName);
            } else {
                log.warn("Cloud Storage에서 객체 {}를 찾을 수 없음.", objectName);
            }

        } catch (StorageException e) {
            log.error("Cloud Storage 파일 삭제 중 오류 발생", e);
            throw new RuntimeException("클라우드 파일 삭제 중 오류 발생", e);
        }

        // DB에서 삭제
        photoRepository.delete(photo);
    }

    public void updatePhotoOrder(List<Long> photoIds) {

        int size = photoIds.size();

        for (int i = 0; i < size; i++) {
            Long id = photoIds.get(i);
            Photo photo = photoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            photo.setSequence(size - 1 - i);

            photoRepository.save(photo);
        }
    }
*/

}

