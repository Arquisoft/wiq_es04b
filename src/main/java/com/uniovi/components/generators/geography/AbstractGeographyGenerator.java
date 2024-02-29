package com.uniovi.components.generators.geography;

import com.uniovi.components.generators.AbstractQuestionGenerator;
import com.uniovi.entities.Category;

public abstract class AbstractGeographyGenerator extends AbstractQuestionGenerator {

    @Override
    public Category getCategory() {
        return new Category("Geography", "Questions about geography");
    }
}
