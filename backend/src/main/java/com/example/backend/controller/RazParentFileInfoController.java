package com.example.backend.controller;

import com.example.backend.entity.RazParentFileInfo;
import com.example.backend.service.RazParentFileInfoService;
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
@RequestMapping("/api/parent-files")
public class RazParentFileInfoController {
    @Autowired
    private RazParentFileInfoService service;

    @GetMapping
    public List<RazParentFileInfo> getAllFiles() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RazParentFileInfo> getFileById(@PathVariable Long id) {
        RazParentFileInfo file = service.findById(id);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public RazParentFileInfo createFile(@RequestBody RazParentFileInfo fileInfo) {
        return service.save(fileInfo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RazParentFileInfo> updateFile(@PathVariable Long id, @RequestBody RazParentFileInfo fileInfo) {
        if (!id.equals(fileInfo.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.save(fileInfo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getFilesByLevel(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Sort sort = Sort.by(
            Sort.Order.asc("razLevel"),
            Sort.Order.asc("razParentFileName")
        );
        Page<RazParentFileInfo> pageResult = service.findByLevelAndKeyword(level, keyword, PageRequest.of(page, size, sort));
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        return ResponseEntity.ok(response);
    }
}