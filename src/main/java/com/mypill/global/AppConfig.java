package com.mypill.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {
    @Getter
    private static ApplicationContext context;
    @Getter
    private static String activeProfile;
    @Getter
    private static String siteName;
    @Getter
    private static String siteBaseUrl;
    @Getter
    public static double wholesalePriceRate;
    @Getter
    public static int cancelAvailableSeconds;
    @Getter
    public static int changePasswordCycleDays;
    private final Environment env;

    public AppConfig(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        AppConfig.context = context;
    }

    @Value("${spring.profiles.active:}")
    public void setActiveProfile() {
        activeProfile = env.getActiveProfiles()[1];
    }

    @Value("${custom.site.name}")
    public void setSiteName(String siteName) {
        AppConfig.siteName = siteName;
    }

    @Value("${custom.site.baseUrl}")
    public void setSiteBaseUrl(String siteBaseUrl) {
        AppConfig.siteBaseUrl = siteBaseUrl;
    }

    public static boolean isTest() {
        return activeProfile.equals("test");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Getter
    private static int maxOrderNameLength;

    @Value("${custom.order.maxOrderNameLength}")
    private void setMaxOrderName(int maxOrderNameLength) {
        AppConfig.maxOrderNameLength = maxOrderNameLength;
    }

    @Getter
    private static int maxAddressCount;

    @Value("${custom.address.maxAddressCount}")
    private void setMaxAddressCount(int maxAddressCount) {
        AppConfig.maxAddressCount = maxAddressCount;
    }

    @Getter
    private static String tossPaymentSecretKey;

    @Value("${custom.toss_payment.secretKey}")
    private void setTossPaymentSecretKey(String tossPaymentSecretKey) {
        AppConfig.tossPaymentSecretKey = tossPaymentSecretKey;
    }
}