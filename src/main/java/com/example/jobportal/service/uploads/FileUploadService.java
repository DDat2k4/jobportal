package com.example.jobportal.service.uploads;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileUploadService {
    private final Cloudinary cloudinary;

    public FileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "jobportal/uploads"));
        Map<String, Object> response = new HashMap<>();
        response.put("url", uploadResult.get("secure_url"));
        response.put("format", uploadResult.get("format"));
        response.put("public_id", uploadResult.get("public_id"));
        response.put("bytes", uploadResult.get("bytes"));
        return response;
    }

    public List<Map<String, Object>> uploadMultiple(List<MultipartFile> files) throws IOException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (MultipartFile file : files) {
            resultList.add(uploadFile(file));
        }
        return resultList;
    }
}

