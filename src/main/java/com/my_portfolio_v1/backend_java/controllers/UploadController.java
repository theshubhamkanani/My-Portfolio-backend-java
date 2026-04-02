package com.my_portfolio_v1.backend_java.controllers;

import com.my_portfolio_v1.backend_java.dtos.FileUploadResponse;
import com.my_portfolio_v1.backend_java.services.ProfileImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final ProfileImageStorageService profileImageStorageService;

    @PostMapping("/api/v1/admin/uploads/profile-image")
    public ResponseEntity<FileUploadResponse> uploadProfileImage(
            @RequestParam("file") MultipartFile file
    ) {
        String fileUrl = profileImageStorageService.storeProfileImage(file);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                FileUploadResponse.builder()
                        .fileName(fileName)
                        .fileUrl(fileUrl)
                        .build()
        );
    }
}
