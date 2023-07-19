package com.mypill.domain.survey.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@RequiredArgsConstructor
@Component
@ConfigurationProperties(prefix = "custom.survey")
public class SurveyProperties {
    private int startMaxLength;
    private int startMinLength;
    private int completeMaxLength;
    private int completeMinLength;
}
