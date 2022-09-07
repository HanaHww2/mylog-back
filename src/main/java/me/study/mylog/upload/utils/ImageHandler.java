package me.study.mylog.upload.utils;

import me.study.mylog.upload.dto.ImageFileResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ImageHandler {
    public static final String UPLOAD_PATH = "C:\\myLogUpload\\images\\";

    public ImageHandler() {
        checkFileDir();
    }

    private void checkFileDir() {
        File fileDir  = new File(UPLOAD_PATH);
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
                    boolean result = saveInFileSystem(multipartFiles.get(idx), dto.getFilename());
                    if (!result) dto = null;
                    return Optional.ofNullable(dto);
                   // return (result)? Optional.ofNullable(dto) : Optional.ofNullable(null); 왜 이 구문으로 리턴하려고 하면 오류가 나는지 모르겟다.
                })
                .collect(Collectors.toList());

        return dtoList;
    }

    // 파일 삭제하기

    public static boolean saveInFileSystem(MultipartFile file, String physicalFileName) {
        try {
            file.transferTo(new File(UPLOAD_PATH + physicalFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
