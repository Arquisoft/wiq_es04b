package com.uniovi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;
    private String description;

    @OneToMany(mappedBy = "category")
    private Set<Question> questions = new HashSet<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
