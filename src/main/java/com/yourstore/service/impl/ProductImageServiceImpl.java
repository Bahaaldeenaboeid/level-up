package com.yourstore.service.impl;

import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.core.util.FileStorageService;
import com.yourstore.entity.Product;
import com.yourstore.repository.ProductRepository;
import com.yourstore.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductImageServiceImpl(ProductRepository productRepository,
                                   FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public String uploadMainImage(MultipartFile file, Long productId) {
        Product product = getProduct(productId);

        // Delete old main image if exists
        if (product.getMainImage() != null) {
            fileStorageService.deleteFile(product.getMainImage());
        }

        try {
            String subDir = "products/" + productId;
            String imageUrl = fileStorageService.saveFile(file, subDir);
            product.setMainImage(imageUrl);
            productRepository.save(product);
            return imageUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload main image: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> uploadThumbnails(List<MultipartFile> files, Long productId) {
        Product product = getProduct(productId);
        List<String> thumbnailUrls = new ArrayList<>();

        // Delete old thumbnails if exist
        if (product.getThumbnails() != null && !product.getThumbnails().isEmpty()) {
            for (String url : product.getThumbnails()) {
                fileStorageService.deleteFile(url);
            }
        }

        try {
            String subDir = "products/" + productId;
            for (MultipartFile file : files) {
                String url = fileStorageService.saveFile(file, subDir);
                thumbnailUrls.add(url);
            }
            product.setThumbnails(thumbnailUrls);
            productRepository.save(product);
            return thumbnailUrls;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload thumbnails: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMainImage(Long productId) {
        Product product = getProduct(productId);
        if (product.getMainImage() != null) {
            fileStorageService.deleteFile(product.getMainImage());
            product.setMainImage(null);
            productRepository.save(product);
        }
    }

    @Override
    public void deleteThumbnail(Long productId, int index) {
        Product product = getProduct(productId);
        List<String> thumbnails = product.getThumbnails();

        if (thumbnails != null && index >= 0 && index < thumbnails.size()) {
            String url = thumbnails.get(index);
            fileStorageService.deleteFile(url);
            thumbnails.remove(index);
            product.setThumbnails(thumbnails);
            productRepository.save(product);
        }
    }

    @Override
    public void deleteAllThumbnails(Long productId) {
        Product product = getProduct(productId);
        if (product.getThumbnails() != null && !product.getThumbnails().isEmpty()) {
            for (String url : product.getThumbnails()) {
                fileStorageService.deleteFile(url);
            }
            product.setThumbnails(new ArrayList<>());
            productRepository.save(product);
        }
    }

    @Override
    public void updateMainImage(Long productId, MultipartFile file) {
        uploadMainImage(file, productId);
    }

    @Override
    public void updateThumbnail(Long productId, int index, MultipartFile file) {
        Product product = getProduct(productId);

        // Delete existing thumbnail at index
        List<String> thumbnails = product.getThumbnails();
        if (thumbnails != null && index >= 0 && index < thumbnails.size()) {
            fileStorageService.deleteFile(thumbnails.get(index));
            thumbnails.remove(index);
        }

        // Upload new thumbnail
        try {
            String subDir = "products/" + productId;
            String newUrl = fileStorageService.saveFile(file, subDir);

            if (thumbnails == null) {
                thumbnails = new ArrayList<>();
            }
            thumbnails.add(index, newUrl);
            product.setThumbnails(thumbnails);
            productRepository.save(product);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update thumbnail: " + e.getMessage(), e);
        }
    }

    @Override
    public String getMainImageUrl(Long productId) {
        Product product = getProduct(productId);
        return product.getMainImage();
    }

    @Override
    public List<String> getThumbnailUrls(Long productId) {
        Product product = getProduct(productId);
        return product.getThumbnails() != null ? product.getThumbnails() : new ArrayList<>();
    }

    // ===== PRIVATE HELPER =====

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }
}