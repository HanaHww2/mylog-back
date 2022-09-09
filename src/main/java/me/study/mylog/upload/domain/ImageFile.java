package me.study.mylog.upload.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.post.domain.Post;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
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
    private Boolean isThumbnail;

    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;

    @Builder
    public ImageFile(Long id, String originFilename, String filename, String filePath,
                     Boolean isThumbnail, Post post) {
        this.id = id;
        this.originFilename = originFilename;
        this.filename = filename;
        this.filePath = filePath;
        this.isThumbnail = isThumbnail!=null?isThumbnail:false;
        this.post = post;
    }

    // 연관관계 편의 메소드
    public void makeRelImgFileToPost() {
        List<ImageFile> imageFileList = post.getImageFileList();
        if (imageFileList.contains(this))  return;

        imageFileList.add(this);
    }
}
