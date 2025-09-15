package com.pixo.pixo_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PixoWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(PixoWebsiteApplication.class, args);
	}

}
