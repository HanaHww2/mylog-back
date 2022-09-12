package me.study.mylog.upload.utils;

import me.study.mylog.upload.dto.ImageFileResponseDto;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ImageHandler {

    /*
    * 이미지 파일 저장 및 dto 반환
    * */
    List<Optional<ImageFileResponseDto>> saveFiles(List<MultipartFile> multipartFiles);
    
    /*
    * 사욯하는 시스템(e.g. local, s3)에 파일 저장
    * */
    String saveInSystem(MultipartFile file, String fileName) throws FileUploadException;
    
}
