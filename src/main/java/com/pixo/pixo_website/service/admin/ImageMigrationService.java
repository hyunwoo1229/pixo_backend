package com.pixo.pixo_website.service.admin;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.pixo.pixo_website.domain.admin.Photo;
import com.pixo.pixo_website.domain.admin.PhotoCategory;
import com.pixo.pixo_website.repository.admin.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageMigrationService {

    private final PhotoRepository photoRepository;
    private final Storage storage;

    @Value("${cloud.storage.bucket-name}")
    private String bucketName;

    @Transactional
    public String compressNextBatch(String categoryName, int batchSize) {
        // ID가 낮은(옛날) 순서대로 가져옵니다.
        Pageable limit = PageRequest.of(0, batchSize, Sort.by("id").ascending());
        List<Photo> targets;
        long remainCount;

        // 카테고리 지정 여부에 따른 조회 분기
        if (categoryName != null && !categoryName.isEmpty()) {
            try {
                PhotoCategory category = PhotoCategory.valueOf(categoryName);
                targets = photoRepository.findByCategoryAndSavedFileNameNotLike(category, "%.webp", limit);
                remainCount = photoRepository.countByCategoryAndSavedFileNameNotLike(category, "%.webp");
            } catch (IllegalArgumentException e) {
                return "오류: 존재하지 않는 카테고리입니다 (" + categoryName + ")";
            }
        } else {
            targets = photoRepository.findBySavedFileNameNotLike("%.webp", limit);
            remainCount = photoRepository.countBySavedFileNameNotLike("%.webp");
        }

        if (targets.isEmpty()) {
            return "변환 완료! 해당 조건에 처리할 파일이 없습니다.";
        }

        int success = 0;
        int fail = 0;

        for (Photo photo : targets) {
            try {
                processSinglePhoto(photo);
                success++;
            } catch (Exception e) {
                log.error("변환 실패 ID: {}", photo.getId(), e);
                fail++;
            }
        }

        String targetName = (categoryName != null) ? categoryName : "전체";
        return String.format("[%s] 실행 결과: %d장 성공, %d장 실패 (남은 사진: 약 %d장)",
                targetName, success, fail, remainCount);
    }

    private void processSinglePhoto(Photo photo) throws Exception {
        String oldName = photo.getSavedFileName();

        // 1. 다운로드
        Blob blob = storage.get(BlobId.of(bucketName, oldName));
        if (blob == null) throw new RuntimeException("파일 없음: " + oldName);

        // 2. 압축 및 WebP 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(blob.getContent()))
                .size(1920, 1920) // FHD 제한
                .outputQuality(0.8) // 퀄리티 80%
                .outputFormat("webp")
                .toOutputStream(out);

        byte[] compressedBytes = out.toByteArray();
        String newName = oldName.substring(0, oldName.lastIndexOf(".")) + ".webp";

        // 3. 재업로드
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, newName)
                .setContentType("image/webp")
                .build();
        storage.create(blobInfo, compressedBytes);

        // 4. DB 업데이트
        String newUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, newName);
        photo.setSavedFileName(newName);
        photo.setImageUrl(newUrl);
        photoRepository.save(photo);

        // 5. 원본 삭제 (선택 사항: 용량이 부족하면 주석 해제하여 사용)
        // storage.delete(bucketName, oldName);
    }
}