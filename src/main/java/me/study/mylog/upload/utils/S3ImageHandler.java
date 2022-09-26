package me.study.mylog.upload.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.common.exception.CustomFileUploadException;
import me.study.mylog.common.exception.DuplicatedResoureException;
import me.study.mylog.upload.dto.ImageFileResponseDto;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageHandler implements ImageHandler{

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;
    private final String baseDirName = "upload/";

    @Override
    public List<Optional<ImageFileResponseDto>> saveFiles(List<MultipartFile> multipartFiles) {

            List<Optional<ImageFileResponseDto>> fileResponseDtoList = multipartFiles.stream()
                    .map(file -> {
                        ImageFileResponseDto dto = new ImageFileResponseDto(file.getOriginalFilename());
                        try {
                            String fileName = dto.getFilename();
                            String url = this.saveInSystem(file, fileName);
                            dto.assignImageUrl(url);
                        } catch (FileUploadException ex) {
                            log.info(ex.getMessage());
                            dto = null;
                        }
                        return Optional.ofNullable(dto);
                    })
                    .collect(Collectors.toList());

        return fileResponseDtoList;
    }

    public String saveInSystem(MultipartFile file, String fileName) throws FileUploadException {
        String url ="";
        try {
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.setContentLength(file.getInputStream().available());
            amazonS3.putObject(bucketName, baseDirName + fileName, file.getInputStream(), objMetadata);
            url = amazonS3.getUrl(bucketName, baseDirName + fileName).toString();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new FileUploadException("FAIL_TO_UPLOAD_FILE"); //
        }
        return url;
    }

    // 파일 삭제 로직
    public void deleteFromSystem(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
    }

    /*
     * TODO 여러 사이트들을 돌아보며 확인한 결과 보통 cdn 서버를 두고
     * 이미지 자료를 반환한다.
     * 나의 경우, s3 를 사용하고 있으며 프론트는 엔진엑스를 웹 서버로 활용해 배포할 예정이다.
     * s3의 url이 바로 노출되는 것이 조금 망설여지는 지점이지만, (포폴 수준에서는 이렇게 진행하는 경우가 꽤 있는 거 같고)
     * 백단에서 한 번 더 이미지 조회를 수행하는 것도 아닌 것 같다. 조금 더 확인해볼 필요성을 느낀다.
     * */
}
