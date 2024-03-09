package com.uniovi.repositories;

import com.uniovi.entities.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.List;
public interface QuestionRepository extends CrudRepository<Question, Long> {
    Question findByStatement(String statement);
    List<Question> findAll();

    @Query("SELECT q FROM Question q WHERE q.language = ?1")
    Page<Question> findByLanguage(Pageable pageable, String language);
}
