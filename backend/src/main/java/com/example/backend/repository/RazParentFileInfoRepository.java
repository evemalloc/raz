package com.example.backend.repository;

import com.example.backend.entity.RazParentFileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RazParentFileInfoRepository extends JpaRepository<RazParentFileInfo, Long> {
    Page<RazParentFileInfo> findByRazLevelStartingWithOrderByRazLevelAscRazParentFileNameAsc(
        String level, Pageable pageable);
    
    Page<RazParentFileInfo> findByRazParentFileNameContainingIgnoreCaseOrderByRazLevelAscRazParentFileNameAsc(
        String keyword, Pageable pageable);
    
    Page<RazParentFileInfo> findByRazLevelStartingWithAndRazParentFileNameContainingIgnoreCaseOrderByRazLevelAscRazParentFileNameAsc(
        String level, String keyword, Pageable pageable);
}