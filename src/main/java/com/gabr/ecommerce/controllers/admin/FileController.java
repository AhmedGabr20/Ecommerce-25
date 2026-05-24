package com.gabr.ecommerce.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FileController {

    @Value("${app.base-url}")
    String baseUrl ;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath =
                Paths.get("uploads/products");
        Files.createDirectories(uploadPath);

        Path filePath =
                uploadPath.resolve(fileName);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return baseUrl +"/uploads/products/" + fileName;
    }
}
