package com.uniovi.repositories;

import com.uniovi.entities.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
}
