package com.mypill.domain.seller.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@RequiredArgsConstructor
@Component
@ConfigurationProperties(prefix = "custom.api")
public class CertificateProperties {
    private String businessRegisterNumber;
    private String nutrientBusinessRegisterNumber;
}
