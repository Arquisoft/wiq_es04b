package com.uniovi.components.generators.geography;

import com.uniovi.components.generators.AbstractQuestionGenerator;
import com.uniovi.entities.Category;
import com.uniovi.services.CategoryService;

public abstract class AbstractGeographyGenerator extends AbstractQuestionGenerator {

    protected AbstractGeographyGenerator(CategoryService categoryService) {
        super(categoryService);
    }

    @Override
    public Category getCategory() {
        return categoryService.getCategoryByName("Geography");
    }
}
