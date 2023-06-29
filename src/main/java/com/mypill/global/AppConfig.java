package com.mypill.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Getter
    private static ApplicationContext context;
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

    @Autowired
    public void setContext(ApplicationContext context) {
        AppConfig.context = context;
    }
//
//    @Value("${custom.rebate.wholesalePriceRate}")
//    public void setWholesalePriceRate(double value) {
//        wholesalePriceRate = value;
//    }
//
//    @Value("${custom.order.cancelAvailableSeconds}")
//    public void setCancelAvailableSeconds(String value) {
//        cancelAvailableSeconds = Integer.valueOf(value);
//    }
//
//    @Value("${spring.profiles.active:}")
//    public void setActiveProfile(String value) {
//        activeProfile = value;
//    }
//
//    @Value("${custom.site.name}")
//    public void setSiteName(String siteName) {
//        AppConfig.siteName = siteName;
//    }
//
//    @Value("${custom.site.baseUrl}")
//    public void setSiteBaseUrl(String siteBaseUrl) {
//        AppConfig.siteBaseUrl = siteBaseUrl;
//    }
//
//    @Value("${custom.member.changePasswordCycleDays}")
//    public void setChangePasswordCycleDays(int changePasswordCycleDays) {
//        AppConfig.changePasswordCycleDays = changePasswordCycleDays;
//    }

    public static boolean isNotProd() {
        return isProd() == false;
    }

    public static boolean isProd() {
        return activeProfile.equals("prod");
    }

    public static boolean isNotDev() {
        return isLocal() == false;
    }

    public static boolean isLocal() {
        return activeProfile.equals("local");
    }

    public static boolean isNotTest() {
        return isLocal() == false;
    }

    public static boolean isTest() {
        return activeProfile.equals("test");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Getter
    private static int startMaxLength;

    @Value("${custom.survey.startMaxLength}")
    public void setStartMaxLength(int startMaxLength) {
        AppConfig.startMaxLength = startMaxLength;
    }

    @Getter
    private static int startMinLength;

    @Value("${custom.survey.startMinLength}")
    public void setStartMinLength(int startMinLength) {
        AppConfig.startMinLength = startMinLength;
    }

    @Getter
    private static int completeMaxLength;

    @Value("${custom.survey.completeMaxLength}")
    public void setCompleteMaxLength(int completeMaxLength) {
        AppConfig.completeMaxLength = completeMaxLength;
    }

    @Getter
    private static int completeMinLength;

    @Value("${custom.survey.completeMinLength}")
    public void setCompleteMinLength(int completeMinLength) {
        AppConfig.completeMinLength = completeMinLength;
    }


}