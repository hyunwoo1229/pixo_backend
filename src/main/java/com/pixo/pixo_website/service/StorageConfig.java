package com.pixo.pixo_website.service;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    @Bean
    public Storage storage() {
        // GOOGLE_APPLICATION_CREDENTIALS 환경 변수 및 권한을 사용하여 인증하고 클라이언트를 생성
        return StorageOptions.getDefaultInstance().getService();
    }
}
