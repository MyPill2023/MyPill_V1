package com.mypill;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M") // 작업 진행 중 중단 된 경우 Lock을 유지할 기본 시간
public class MypillApplication {
    public static void main(String[] args) {
        SpringApplication.run(MypillApplication.class, args);
    }

}
