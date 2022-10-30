package me.study.mylog.upload.utils;

import me.study.mylog.common.exception.CustomFileUploadException;
import me.study.mylog.common.properties.LocalFileProperties;
import me.study.mylog.upload.dto.ImageFileResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class LocalImageHandler implements ImageHandler {


    public LocalImageHandler() {
        checkFileDir();
    }

    private void checkFileDir() {
        File fileDir  = new File(LocalFileProperties.UPLOAD_PATH);
        if(!fileDir.exists()) { // 폴더가 없을 경우 폴더 만들기
            fileDir.mkdirs();
        }
    }

    /*
    * 이미지 파일 저장 및 dto 반환
    * */
    public List<Optional<ImageFileResponseDto>> saveFiles(List<MultipartFile> multipartFiles) {

        List<Optional<ImageFileResponseDto>> dtoList = IntStream.range(0, multipartFiles.size())
                .mapToObj((idx) -> {
                    ImageFileResponseDto dto = new ImageFileResponseDto(multipartFiles.get(idx).getOriginalFilename());
                    String filename = dto.getFilename();
                    String dirLocation = this.saveInSystem(multipartFiles.get(idx), filename);

                    if (Optional.ofNullable(dirLocation).isPresent()) {
                        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/images/local/"+filename)
                                .build()
                                .toString();
                        dto.assignImageUrl(uri);
                    } else {
                        // 일부 업로드가 실패한 경우가 만약 생긴다면 예외 대신 null로 응답
                        dto = null;
                    }
                    return Optional.ofNullable(dto);
                   // return (result)? Optional.ofNullable(dto) : Optional.ofNullable(null); 왜 이 구문으로 리턴하려고 하면 오류가 나는지 모르겟다.
                })
                .collect(Collectors.toList());

        return dtoList;
    }

    public String saveInSystem(MultipartFile file, String physicalFileName) {
        String location = LocalFileProperties.UPLOAD_PATH + physicalFileName;
        try {
            file.transferTo(new File(location));
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomFileUploadException("FAIL_TO_UPLOAD_FILE", HttpStatus.BAD_REQUEST);
        }
        return location;
    }

    // 파일 삭제하기
}
