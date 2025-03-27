package com.example.backend.service;

import com.example.backend.entity.RazParentFileInfo;
import com.example.backend.repository.RazParentFileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RazParentFileInfoService {
    @Autowired
    private RazParentFileInfoRepository repository;

    public List<RazParentFileInfo> findAll() {
        return repository.findAll();
    }

    public RazParentFileInfo findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public RazParentFileInfo save(RazParentFileInfo fileInfo) {
        LocalDateTime now = LocalDateTime.now();
        if (fileInfo.getId() == null) {
            fileInfo.setCreateTime(now);
        }
        fileInfo.setUpdateTime(now);
        return repository.save(fileInfo);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<RazParentFileInfo> findByLevel(String level, Pageable pageable) {
        if (level == null || level.isEmpty()) {
            return repository.findAll(pageable);
        }
        return repository.findByRazLevelStartingWithOrderByRazLevelAscRazParentFileNameAsc(level, pageable);
    }

    public Page<RazParentFileInfo> findByLevelAndKeyword(String level, String keyword, Pageable pageable) {
        if (level == null || level.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                return repository.findAll(pageable);
            }
            return repository.findByRazParentFileNameContainingIgnoreCaseOrderByRazLevelAscRazParentFileNameAsc(keyword, pageable);
        }
        
        if (keyword == null || keyword.isEmpty()) {
            return repository.findByRazLevelStartingWithOrderByRazLevelAscRazParentFileNameAsc(level, pageable);
        }
        
        return repository.findByRazLevelStartingWithAndRazParentFileNameContainingIgnoreCaseOrderByRazLevelAscRazParentFileNameAsc(
            level, keyword, pageable);
    }
}