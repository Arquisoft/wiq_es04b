package com.uniovi.components.generators.movie;

import com.uniovi.components.generators.AbstractQuestionGenerator;
import com.uniovi.entities.Category;
import com.uniovi.services.CategoryService;

public abstract class AbstractMovieGenerator extends AbstractQuestionGenerator {

    protected AbstractMovieGenerator(CategoryService categoryService) {
        super(categoryService);
    }

    @Override
    public Category getCategory() {
        return categoryService.getCategoryByName("Movie");
    }
}
