package me.study.mylog.upload.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.post.entity.Post;
import me.study.mylog.upload.domain.ImageFile;

@Getter
@NoArgsConstructor
public class ImageFileRequest {

    private String originFilename;
    private String filename;
    private String filePath;
    private Boolean isThumbnail;

    public ImageFile toEntity(Post post) {

        return ImageFile.builder()
                .filename(filename)
                .filePath(filePath)
                .originFilename(originFilename)
                .post(post)
                .build();
    }
}
