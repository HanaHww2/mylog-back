package me.study.mylog.upload.controller;

import me.study.mylog.common.properties.LocalFileProperties;
import me.study.mylog.upload.dto.ImageFileResponseDto;
import me.study.mylog.upload.service.ImageFileWriteService;
import me.study.mylog.upload.utils.ImageHandler;
import me.study.mylog.upload.utils.S3ImageHandler;
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

//@RequiredArgsConstructor
@RestController
public class ImageFileController {
    private final ImageFileWriteService imageFileWriteService;
    private final ImageHandler imgHandler;

    public ImageFileController(ImageFileWriteService imageFileWriteService, S3ImageHandler imgHandler) {
        this.imageFileWriteService = imageFileWriteService;
        this.imgHandler = imgHandler;
        //LocalImageHandler
    }

    /*
    * 이미지 업로드
    * 파일 시스템에 먼저 업로드한다.
    * (깃허브 위키의 이미지 업로드되는 전략을 표면적으로 따라간다.
    * -> 깃허브 위키에서는 에디터에 이미지를 올리면, 바로 이미지 업로드가 진행되고 이미지의 url이 생성된다.)
    * DB에 저장하는 것은 글이 등록되는 시점이다.
    * 차후, 스케쥴링 혹은 배치 작업을 통해서, 디비에 기록되지 않은 uri를 가진 이미지나 첨부파일은 삭제 처리해보고자 한다.
    * 이 로직은 차후 상황에 따라 변경 가능할 것이다.
    * */
    // TODO 로컬 파일 시스템 활용과 S3 활용에서 응답 데이터 구조 차이가 있음 -> 수정 중, 결과 확인 필요
    @PostMapping("/api/images/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("imgFiles") List<MultipartFile> multipartFiles) {

        List<Optional<ImageFileResponseDto>> dtoList = imgHandler.saveFiles(multipartFiles);

        // TODO S3 업로드 실패시 로컬에 저장하는 보완 코드를 작성하고 싶은데 어떻게 하면 좋을지 고민이다.
        // 서비스 레이어 (핸들러)에서 처리하려니, 객체 간 종속을 갖게 되는 모습이 그리 좋지 않아 보인다.
        // 스트림에서 익셉션을 잘 핸들링해서 보완할 수 없을까... 오류가 발생하면 전환을 시키는 정도로 괜찮을지 오류난 파일만 재시도를 하도록 하는 게 좋을지...고민고민
//        List<Optional<ImageFileResponseDto>> failedList = dtoList.stream()
//                .filter(dto -> dto.isEmpty())
//                .collect(Collectors.toList());
//        
//        if (failedList.size() > 0) {
//            LocalImageHandler localImageHandler = new LocalImageHandler();
//            localImageHandler.saveFiles(failedList);
//        }

        return new ResponseEntity<>(dtoList, HttpStatus.CREATED);
    }


    @GetMapping("/api/images/local/{filename}")
    public ResponseEntity<Resource> showImage(@PathVariable String filename) {

        Resource resource = new FileSystemResource(LocalFileProperties.UPLOAD_PATH + filename);
        if (!resource.exists()) return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);

        HttpHeaders header = new HttpHeaders();
        try {
            Path filePath = Paths.get(LocalFileProperties.UPLOAD_PATH + filename);
            header.add("Content-type", Files.probeContentType(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .headers(header)
                .body(resource);
    }
}
