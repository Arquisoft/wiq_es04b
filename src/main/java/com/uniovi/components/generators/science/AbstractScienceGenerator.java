package com.uniovi.components.generators.science;

import com.uniovi.components.generators.AbstractQuestionGenerator;
import com.uniovi.entities.Category;
import com.uniovi.services.CategoryService;

public abstract class AbstractScienceGenerator extends AbstractQuestionGenerator {

    protected AbstractScienceGenerator(CategoryService categoryService) {
        super(categoryService);
    }

    @Override
    public Category getCategory() {
        return categoryService.getCategoryByName("Science");
    }
}
