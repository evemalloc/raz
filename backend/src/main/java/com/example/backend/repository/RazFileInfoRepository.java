package com.example.backend.repository;

import com.example.backend.entity.RazFileInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RazFileInfoRepository extends JpaRepository<RazFileInfo, Long> {
    Page<RazFileInfo> findByRazLevelAndRazParentFileNameOrderByRazStartTimeAscRazChildPngFileNameAsc(
        String razLevel, 
        String razParentFileName, 
        Pageable pageable
    );
}