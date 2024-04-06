package com.uniovi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionDto {

    @Schema(description = "The statement of the question")
    private String statement;

    @Schema(description = "The options of the question")
    private List<AnswerDto> options;

    @Schema(description = "The correct answer of the question", hidden = true)
    private AnswerDto correctAnswer;

    @Schema(description = "The category of the question")
    private CategoryDto category;

    @Schema(description = "The language of the question")
    private String language;
}
