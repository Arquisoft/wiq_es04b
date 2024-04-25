package com.uniovi.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.components.generators.QuestionGeneratorV2;
import com.uniovi.entities.Question;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class QuestionGeneratorTestController {

    private final String jsonFilePath = "src/main/resources/static/JSON/QuestionTemplates.json";

    @RequestMapping("/test")
    public void test() throws IOException {
        // Crear un objeto File con la ruta del archivo JSON
        File jsonFile = new File(jsonFilePath);

        // Crear un ObjectMapper de Jackson
        ObjectMapper objectMapper = new ObjectMapper();

        // Leer el archivo JSON y convertirlo en un JsonNode
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        QuestionGeneratorV2 qgen = new QuestionGeneratorV2(jsonNode);
        List<Question> q = qgen.getQuestions(Question.SPANISH);
        for(Question question : q){
            System.out.println(question);
        }
    }
}
