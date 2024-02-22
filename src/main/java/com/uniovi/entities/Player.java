package com.uniovi.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter // getters para todas las propiedades
@Setter // setters para todas las propiedades
@NoArgsConstructor(access = AccessLevel.PROTECTED) // constructor sin argumentos
@Entity
public class Player {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    @NotEmpty
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @NotEmpty
    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name="players_roles",
            joinColumns={@JoinColumn(name="PLAYER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="NAME")})
    private Set<Role> roles = new HashSet<>();

    // Transient: no se almacena en la base de datos
    @Transient
    private String passwordConfirm;

    public Player(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
