package com.example.Kirby_mini_2nd.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FileUtil {
    private static final String UPLOAD_DIR = "C:/MiniProject/Kirby-mini_2nd/Kirby-mini-2nd/src/main/resources/static/images/";
    private static final String IMAGE_PATH = "http://localhost:8090/images/";
    private static final String DEFAULT_IMAGE = "http://localhost:8090/images/default.jpg";

    // 파일(이미지) 저장  메서드
    public static String UpdateImage(MultipartFile file, String fileName) {
        if (file == null){
            // 업데이트시 파일을 선택하지 않았다면 기본 이미지나 원래 설정한 이미지 그대로 사용.
            // 결국 파일 네임을 그대로 돌려주면 됨.
            return fileName;
        }

        try {
            String newFileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            File destination = new File(UPLOAD_DIR + newFileName);
            file.transferTo(destination);
            
            // 파일 경로와 이름
            // 저장시 url 만들어주기.
            return IMAGE_PATH + newFileName;
        } catch (Exception e) {
            return ("에러발생 : " + e.getMessage());
        }

    }
}
