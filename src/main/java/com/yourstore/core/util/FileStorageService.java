package com.yourstore.core.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads/}")
    private String storageRoot;

    private Path rootLocation;

    // ==========================================================================================
    // INITIALIZE STORAGE DIRECTORY
    // ==========================================================================================
    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageRoot).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocation.resolve("products"));
            Files.createDirectories(rootLocation.resolve("reviews"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory: " + rootLocation, e);
        }
    }

    // ==========================================================================================
    // SAVE FILE TO DISK
    // ==========================================================================================
    public String saveFile(MultipartFile file, String subDir) throws IOException {
        // 1. Create specific subdirectory (e.g., products/1)
        Path targetDir = rootLocation.resolve(subDir).normalize();
        Files.createDirectories(targetDir);

        // 2. Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // 3. Save file to disk
        Path targetPath = targetDir.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Return web URL
        return "/uploads/" + subDir + "/" + filename;
    }

    // ==========================================================================================
    // DELETE FILE FROM DISK
    // ==========================================================================================
    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        try {
            // Extract subdirectory and filename from URL
            // /uploads/products/1/image.jpg → subDir = "products/1", filename = "image.jpg"
            String path = imageUrl.replace("/uploads/", "");
            int lastSlash = path.lastIndexOf("/");
            if (lastSlash > 0) {
                String subDir = path.substring(0, lastSlash);
                String filename = path.substring(lastSlash + 1);
                Path filePath = rootLocation.resolve(subDir).resolve(filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + imageUrl + " - " + e.getMessage());
        }
    }

    // ==========================================================================================
    // CHECK IF FILE EXISTS
    // ==========================================================================================
    public boolean fileExists(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        try {
            String path = imageUrl.replace("/uploads/", "");
            int lastSlash = path.lastIndexOf("/");
            if (lastSlash > 0) {
                String subDir = path.substring(0, lastSlash);
                String filename = path.substring(lastSlash + 1);
                Path filePath = rootLocation.resolve(subDir).resolve(filename);
                return Files.exists(filePath);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================================================================
    // GET ROOT LOCATION
    // ==========================================================================================
    public Path getRootLocation() {
        return rootLocation;
    }
}