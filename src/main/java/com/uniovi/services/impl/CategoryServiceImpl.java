package com.uniovi.services.impl;

import com.uniovi.entities.Category;
import com.uniovi.repositories.CategoryRepository;
import com.uniovi.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void addNewCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> l = new ArrayList<>();
        categoryRepository.findAll().forEach(l::add);
        return l;
    }

    @Override
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }
}
