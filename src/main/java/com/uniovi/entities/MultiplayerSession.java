package com.uniovi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter // getters para todas las propiedades
@Setter // setters para todas las propiedades
@Entity
public class MultiplayerSession  {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String multiplayerCode;
    //@ManyToMany
    //private Set<Player> players =new HashSet<>();
    @ElementCollection
    @Column
    private Map<Player, Integer> playerScores = new HashMap<>();

    public MultiplayerSession() {}

    public MultiplayerSession(String code, Player p) {
        this.multiplayerCode=code;
        playerScores.put(p,-1);

    }

    public void addPlayer(Player p){
        playerScores.put(p,-1);
    }
}
