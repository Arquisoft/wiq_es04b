package com.uniovi.components.generators.history;

import com.uniovi.components.generators.AbstractQuestionGenerator;
import com.uniovi.entities.Category;
import com.uniovi.services.CategoryService;

public abstract class AbstractHistoryGenerator extends AbstractQuestionGenerator {

    protected AbstractHistoryGenerator(CategoryService categoryService) {
        super(categoryService);
    }

    @Override
    public Category getCategory() {
        return categoryService.getCategoryByName("History");
    }
}
