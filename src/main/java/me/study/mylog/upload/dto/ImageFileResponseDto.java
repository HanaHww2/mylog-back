package me.study.mylog.upload.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ImageFileResponseDto {
    private String originFilename;
    private String filename;

    public ImageFileResponseDto(String originFilename) {
        this.originFilename = originFilename;
        int idxForExtension = originFilename.lastIndexOf('.');
        // datetime 등 을 활용해 조금 더 난수화 할 예정
        this.filename = UUID.randomUUID().toString() + originFilename.substring(idxForExtension);
    }
}
