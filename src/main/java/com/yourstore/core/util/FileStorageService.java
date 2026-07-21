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

    @Value("${app.upload.dir}")
    private String storageRoot;

    private Path rootLocation;

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

    public String saveFile(MultipartFile file, String subDir) throws IOException {
        Path targetDir = rootLocation.resolve(subDir).normalize();
        Files.createDirectories(targetDir);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        Path targetPath = targetDir.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + subDir + "/" + filename;
    }

    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        try {
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

    public Path getRootLocation() {
        return rootLocation;
    }
}