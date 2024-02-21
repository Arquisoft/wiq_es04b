package com.uniovi.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
public class AppUser {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter @Setter @NonNull
    private String nickname;

    @Getter @Setter @NonNull
    private String name;

    @Getter @Setter @NonNull
    private String surname;

    @Column(unique = true, nullable = false)
    @Getter @Setter @NonNull
    private String email;

    @Getter @Setter @NonNull
    private String password;

    @Transient
    @Getter @Setter
    private String passwordConfirm;

    protected AppUser() {}

    public AppUser(String nickname, String name, String surname, String email, String password) {
        this.nickname = nickname;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }
}
