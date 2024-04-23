package com.uniovi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

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

    public MultiplayerSession() {

    }

    public MultiplayerSession(String code, Player p) {
        this.multiplayerCode=Integer.parseInt(code);
        players.add(p);

    }

    public void addPlayer(Player p){
        players.add(p);
    }
}
