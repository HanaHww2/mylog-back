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
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(file.getInputStream().available());
            amazonS3.putObject(bucketName, fileName, file.getInputStream(), objMeta);
            url = amazonS3.getUrl(bucketName, fileName).toString();

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
}
