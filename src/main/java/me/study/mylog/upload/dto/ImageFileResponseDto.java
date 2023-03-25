package me.study.mylog.upload.dto;

import lombok.*;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ImageFileResponseDto {
    private String originFilename;
    private String filename;
    private String url;

    public ImageFileResponseDto(String originFilename) {
        this.originFilename = originFilename;
        int idxForExtension = originFilename.lastIndexOf('.');
        // datetime 등 을 활용해 조금 더 난수화 할 필요
        this.filename = UUID.randomUUID() + originFilename.substring(idxForExtension);
    }

    public void assignImageUrl(String url) {
        if (Optional.ofNullable(this.url).isPresent()) return;
        this.url = url;
        return;
    }

}
