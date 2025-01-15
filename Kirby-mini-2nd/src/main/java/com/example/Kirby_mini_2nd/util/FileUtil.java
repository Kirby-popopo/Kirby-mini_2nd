package com.example.Kirby_mini_2nd.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FileUtil {
   // private static final String UPLOAD_DIR = "C:/education/Kirby-mini_2nd/Kirby-mini-2nd/src/main/resources/static/";
    private static final String UPLOAD_DIR = "C:/Users/mysk4/Documents/GitHub/Kirby-mini_2nd/Kirby-mini-2nd/src/main/resources/static/";
    private static final String IMAGE_PATH = "http://localhost:8090/images/";
    private static final String VIDEO_PATH = "http://localhost:8090/media/";
    private static final String DEFAULT_IMAGE = "http://localhost:8090/images/default.jpg";

    // 파일(이미지) 저장  메서드
    //public static String UpdateImage(MultipartFile file) {
    public static String UpdateImage(MultipartFile file, String fileName) {
        if (file == null){
            // 업데이트시 파일을 선택하지 않았다면 기본 이미지나 원래 설정한 이미지 그대로 사용.
            // 결국 파일 네임을 그대로 돌려주면 됨.
            return fileName;
        }

        try {
            String newFileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            File destination = new File(UPLOAD_DIR + "/images/" + newFileName);
            file.transferTo(destination);

            // 파일 경로와 이름
            // 저장시 url 만들어주기.
            return IMAGE_PATH + newFileName;
        } catch (Exception e) {
            return ("에러발생 : " + e.getMessage());
        }
    }
    // 파일(이미지/비디오) 저장  메서드
    public static String SaveMedia(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
            File destination;

            //파일 타입 확인
            String fileType;
            String mediaType=file.getContentType();
            if(mediaType !=null&& mediaType.startsWith("image")){
                destination = new File(UPLOAD_DIR + "images/" + fileName);
                fileType=IMAGE_PATH;
            }
            else if(mediaType !=null&& mediaType.startsWith("video")){
                destination = new File(UPLOAD_DIR + "media/" + fileName);
                fileType=VIDEO_PATH;
            }
            else{
                throw new IllegalArgumentException( "지원하지 않는 파일 형식 입니다.");
            }
            // 성공 시 MediaResponse 반환
            file.transferTo(destination);//파일저장
            return  fileType + fileName;

        } catch (Exception e) {
            return ("에러발생 : " + e.getMessage());
        }

    }
}
