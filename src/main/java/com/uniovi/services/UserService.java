package com.uniovi.services;

import com.uniovi.entities.AppUser;
import com.uniovi.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class UserService {
    private final AppUserRepository userRepository;

    @Autowired
    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addNewUser(String email, String name, String lastName) {
       userRepository.save(new AppUser(name, name, email, lastName, "1234"));
    }

    public List<AppUser> getUsers() {
        List<AppUser> list = new ArrayList<>();
        userRepository.findAll().forEach(list::add);
        return list;
    }
}
