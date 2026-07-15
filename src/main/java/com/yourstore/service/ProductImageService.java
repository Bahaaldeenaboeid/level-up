package com.yourstore.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    String uploadMainImage(MultipartFile file, Long productId);

    List<String> uploadThumbnails(List<MultipartFile> files, Long productId);

    void deleteMainImage(Long productId);

    void deleteThumbnail(Long productId, int index);

    void deleteAllThumbnails(Long productId);

    void updateMainImage(Long productId, MultipartFile file);

    void updateThumbnail(Long productId, int index, MultipartFile file);

    String getMainImageUrl(Long productId);

    List<String> getThumbnailUrls(Long productId);
}