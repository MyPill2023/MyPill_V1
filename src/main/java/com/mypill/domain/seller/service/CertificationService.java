package com.mypill.domain.seller.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.domain.seller.properties.CertificateProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final CertificateProperties certificateProperties;

    public boolean businessNumberCertificated(String number) {
        String requestUrl = "http://apis.data.go.kr/1130000/MllBs_1Service/getMllBsBiznoInfo_1";
        String pageNo = "1";
        String numOfRows = "30";
        String resultType = "json";
        String urlWithQuery = requestUrl + "?serviceKey=" + certificateProperties.getBusinessRegisterNumber() +
                "&pageNo=" + pageNo +
                "&numOfRows=" + numOfRows +
                "&resultType=" + resultType +
                "&brno=" + number;
        try {
            StringBuilder sb = getDataFromAPI(urlWithQuery);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(sb.toString());
            String totalCount = jsonData.path("totalCount").toString();
            if (totalCount == null) {
                return false;
            }
            return totalCount.equals("1");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean nutrientBusinessNumberCertificated(String number) {
        String url1 = "http://openapi.foodsafetykorea.go.kr/api/";
        String serviceNumber = "I1290";
        String resultType = "json";
        String startIndex = "1";
        String endIndex = "1";
        String urlWithQuery = url1 + "/" + certificateProperties.getNutrientBusinessRegisterNumber()
                + "/" + serviceNumber
                + "/" + resultType
                + "/" + startIndex
                + "/" + endIndex
                + "/LCNS_NO=" + number;
        try {
            StringBuilder sb = getDataFromAPI(urlWithQuery);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(sb.toString());
            String resultCode = jsonData.get("I1290").get("RESULT").get("CODE").asText();
            return resultCode.equals("INFO-000");
        } catch (Exception e) {
            return false;
        }
    }

    private StringBuilder getDataFromAPI(String urlWithQuery) throws IOException {
        URL url = new URL(urlWithQuery);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb;
    }
}
