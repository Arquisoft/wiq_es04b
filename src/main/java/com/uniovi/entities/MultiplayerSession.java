package com.uniovi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter // getters para todas las propiedades
@Setter // setters para todas las propiedades
@Entity
public class MultiplayerSession  {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private int  multiplayerCode;
    @ManyToMany
    private Set<Player> players =new HashSet<>();

}
