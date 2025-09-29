package com.pixo.pixo_website.service.admin;

import com.google.cloud.storage.*;
import com.google.cloud.storage.Acl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.cloud.storage.HttpMethod;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class StorageService {

    @Value("${cloud.storage.bucket-name}")
    private String bucketName;

    private final Storage storage;

    public StorageService(Storage storage) {
        this.storage = storage;
    }

    public String generateSignedUrlForUpload(String originalFileName, String contentType) {
        String ext = originalFileName.contains(".") ? originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase() : "";
        String uniqueFileName = "photos/" + UUID.randomUUID() + ext;

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, uniqueFileName))
                .setContentType(contentType)
                .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                .build();

        return storage.signUrl(blobInfo, 15L, TimeUnit.MINUTES, Storage.SignUrlOption.httpMethod(HttpMethod.PUT), Storage.SignUrlOption.withV4Signature()).toString();
    }
}
