package me.study.mylog.upload.controller;

import lombok.RequiredArgsConstructor;
import me.study.mylog.upload.dto.ImageFileResponseDto;
import me.study.mylog.upload.service.ImageFileService;
import me.study.mylog.upload.utils.ImageHandler;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = {"http://localhost:3000"})
@RequiredArgsConstructor
@RestController
public class ImageFileController {
    private final ImageFileService imageFileService;
    private final ImageHandler imgHandler;

    /*
    * 이미지 업로드
    * 파일 시스템에 먼저 업로드한다.
    * (깃허브 위키의 이미지 업로드되는 전략을 표면적으로 따라간다.
    * -> 깃허브 위키에서는 에디터에 이미지를 올리면, 바로 이미지 업로드가 진행되고 이미지의 url이 생성된다.)
    * DB에 저장하는 것은 글이 등록되는 시점이다.
    * 차후, 스케쥴링 혹은 배치 작업을 통해서, 디비에 기록되지 않은 uri를 가진 이미지나 첨부파일은 삭제 처리해보고자 한다.
    * 이 로직은 차후 상황에 따라 변경 가능할 것이다.
    * */
    @PostMapping("/api/local/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("imgFiles") List<MultipartFile> multipartFiles) {

        List<Optional<ImageFileResponseDto>> dtoList = imgHandler.saveFiles(multipartFiles);
        if (dtoList.isEmpty()) return new ResponseEntity<>("fail to upload", HttpStatus.CONFLICT);

        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }


    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> showImage(@PathVariable String filename) {

        Resource resource = new FileSystemResource(imgHandler.UPLOAD_PATH + filename);
        if (!resource.exists()) return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);

        HttpHeaders header = new HttpHeaders();
        try {
            Path filePath = Paths.get(imgHandler.UPLOAD_PATH + filename);
            header.add("Content-type", Files.probeContentType(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .headers(header)
                .body(resource);
    }
}
