package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "raz_file_info")
public class RazFileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "raz_level")
    private String razLevel;
    
    @Column(name = "raz_parent_file_name")
    private String razParentFileName;
    
    @Column(name = "raz_child_png_file_name")
    private String razChildPngFileName;
    
    @Column(name = "raz_start_time")
    private String razStartTime;
    
    @Column(name = "raz_end_time")
    private String razEndTime;
    
    @Column(name = "raz_file_dir")
    private String razFileDir;
    
    @Column(name = "raz_child_png_file_url", length = 512)
    private String razChildPngFileUrl;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
}