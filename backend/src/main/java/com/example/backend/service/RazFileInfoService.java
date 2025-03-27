package com.example.backend.service;

import com.example.backend.entity.RazFileInfo;
import com.example.backend.repository.RazFileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RazFileInfoService {
    @Autowired
    private RazFileInfoRepository repository;

    public List<RazFileInfo> findAll() {
        return repository.findAll();
    }

    public RazFileInfo findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public RazFileInfo save(RazFileInfo fileInfo) {
        if (fileInfo.getId() == null) {
            fileInfo.setCreateTime(LocalDateTime.now());
        }
        fileInfo.setUpdateTime(LocalDateTime.now());
        return repository.save(fileInfo);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<RazFileInfo> findByLevelAndParentFileName(String level, String parentFileName, Pageable pageable) {
        return repository.findByRazLevelAndRazParentFileNameOrderByRazStartTimeAscRazChildPngFileNameAsc(
            level, parentFileName, pageable);
    }
}