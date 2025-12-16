package com.example.jobportal.controller.excel;

import com.example.jobportal.data.response.ApiResponse;
import com.example.jobportal.service.learningpath.LearningPathExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/learning-path")
@RequiredArgsConstructor
public class LearningpathImport {
    private final LearningPathExcelImportService learningPathExcelImportService;

    @PostMapping("/import-excel")
    @PreAuthorize("hasAuthority('LEARNING_PATH_IMPORT')")
    public ApiResponse<Void> importExcel(@RequestParam MultipartFile file) {
        learningPathExcelImportService.importAll(file);
        return ApiResponse.ok("Import success", null);
    }
}
