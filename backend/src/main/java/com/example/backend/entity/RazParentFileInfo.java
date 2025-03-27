package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "raz_parent_file_info")
public class RazParentFileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "raz_level")
    private String razLevel;
    
    @Column(name = "raz_parent_file_name")
    private String razParentFileName;
    
    @Column(name = "raz_file_dir")
    private String razFileDir;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "cover_picture_url")
    private String coverPictureUrl;
    
    @Column(name = "mp3_url", length = 512)
    private String mp3Url;
    
    // 添加 getter 和 setter
    public String getMp3Url() {
        return mp3Url;
    }
    
    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }
}