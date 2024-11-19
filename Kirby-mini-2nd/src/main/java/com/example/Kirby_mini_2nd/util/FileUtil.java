package com.example.Kirby_mini_2nd.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FileUtil {
    private static final String UPLOAD_DIR = "C:/MiniProject/Kirby-mini_2nd/Kirby-mini-2nd/src/main/resources/static/images/";
    private static final String IMAGE_PATH = "http://localhost:8090/images/";

    // 파일(이미지) 저장  메서드
    public static String SaveFileImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            File destination = new File(UPLOAD_DIR + fileName);
            file.transferTo(destination);
            
            // 파일 경로와 이름
            // 저장시 url 만들어주기.
            return IMAGE_PATH + fileName;
        } catch (Exception e) {
            return ("에러발생 : " + e.getMessage());
        }

    }
}
