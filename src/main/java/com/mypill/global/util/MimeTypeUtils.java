package com.mypill.global.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.apache.tika.Tika;

public class MimeTypeUtils {

    public static boolean isImage(String mimeType) {
        return checkMimeType(mimeType, "image/");
    }

    public static boolean checkMimeType(String mimeType, String checkType) {
        return mimeType != null && mimeType.startsWith(checkType);
    }

    public static String getMimeType(MultipartFile file) {
        Tika tika = new Tika();
        String mimeType;
        try {
            mimeType = tika.detect(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mimeType;
    }

    public static String extractFileExtension(String mimeType) {
        return mimeType.split("/")[1];
    }
}