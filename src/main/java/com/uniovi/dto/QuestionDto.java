package com.uniovi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {

    private Long id;
    private String statement;
    private List<AnswerDto> options;
    private AnswerDto correctAnswer;
    private CategoryDto category;

}
