package com.uniovi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ApiKey {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String key = UUID.randomUUID().toString();

    @OneToOne
    private Player player;

    @OneToMany(mappedBy = "apiKey")
    private Set<RestApiAccessLog> accessLogs = new HashSet<>();
}
