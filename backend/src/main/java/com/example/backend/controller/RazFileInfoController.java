package com.example.backend.controller;

import com.example.backend.entity.RazFileInfo;
import com.example.backend.service.RazFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class RazFileInfoController {
    @Autowired
    private RazFileInfoService service;

    @GetMapping
    public List<RazFileInfo> getAllFiles() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RazFileInfo> getFileById(@PathVariable Long id) {
        RazFileInfo file = service.findById(id);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public RazFileInfo createFile(@RequestBody RazFileInfo fileInfo) {
        return service.save(fileInfo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RazFileInfo> updateFile(@PathVariable Long id, @RequestBody RazFileInfo fileInfo) {
        try {
            // 检查文件是否存在
            RazFileInfo existingFile = service.findById(id);
            if (existingFile == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 更新需要修改的字段
            existingFile.setRazStartTime(fileInfo.getRazStartTime());
            existingFile.setRazEndTime(fileInfo.getRazEndTime());
            
            // 保存更新
            RazFileInfo updatedFile = service.save(existingFile);
            return ResponseEntity.ok(updatedFile);
        } catch (Exception e) {
            System.out.println("Update error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/children")
    public ResponseEntity<Map<String, Object>> getChildFiles(
            @RequestParam String level,
            @RequestParam String parentFileName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Sort sort = Sort.by(
            Sort.Order.asc("razStartTime"),
            Sort.Order.asc("razChildPngFileName")
        );
        Page<RazFileInfo> pageResult = service.findByLevelAndParentFileName(
            level, parentFileName, PageRequest.of(page, size, sort));
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        return ResponseEntity.ok(response);
    }
}