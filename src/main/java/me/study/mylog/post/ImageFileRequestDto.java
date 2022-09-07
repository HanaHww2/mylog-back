package me.study.mylog.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.post.domain.Post;
import me.study.mylog.upload.domain.ImageFile;

@Getter
@NoArgsConstructor
public class ImageFileRequestDto {

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
