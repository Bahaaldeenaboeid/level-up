package com.yourstore.service;

import com.yourstore.entity.Category;
import java.util.List;

public interface CategoryService {

    Category createCategory(String name, String description);

    Category updateCategory(Long categoryId, String name, String description);

    void deleteCategory(Long categoryId);

    Category getCategoryById(Long categoryId);

    List<Category> getAllCategories();
}