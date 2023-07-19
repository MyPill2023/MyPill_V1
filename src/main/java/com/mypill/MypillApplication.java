package com.mypill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan
@EnableScheduling
@EnableConfigurationProperties
public class MypillApplication {
    public static void main(String[] args) {
        SpringApplication.run(MypillApplication.class, args);
    }

}
