package com.uniovi.components.generators.art;

import com.uniovi.components.generators.AbstractQuestionGenerator;
import com.uniovi.entities.Category;
import com.uniovi.services.CategoryService;

public abstract class AbstractArtGenerator extends AbstractQuestionGenerator {

    protected AbstractArtGenerator(CategoryService categoryService) {
        super(categoryService);
    }

    @Override
    public Category getCategory() {
        return categoryService.getCategoryByName("Art");
    }
}
