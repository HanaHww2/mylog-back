package me.study.mylog.upload.domain;

import lombok.*;
import me.study.mylog.post.entity.Post;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String originFilename;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @Column
    private Boolean isThumbnail = false;

    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;


    // 연관관계 편의 메소드
    public void makeRelImgFileToPost() {
        List<ImageFile> imageFileList = post.getImageFileList();
        if (imageFileList.contains(this))  return;

        imageFileList.add(this);
    }
}
