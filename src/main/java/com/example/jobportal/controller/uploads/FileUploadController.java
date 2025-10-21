package com.example.jobportal.controller.uploads;

import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.uploads.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadSingle(
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = fileUploadService.uploadFile(file);
            return ResponseEntity.ok(ApiResponse.ok("Upload successful", result));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/multiple")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> uploadMultiple(
            @RequestParam("files") List<MultipartFile> files) {
        try {
            List<Map<String, Object>> result = fileUploadService.uploadMultiple(files);
            return ResponseEntity.ok(ApiResponse.ok("All files uploaded successfully", result));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Upload failed: " + e.getMessage()));
        }
    }
}