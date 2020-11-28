/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.service;

import com.mj.airport.dto.UserDto;
import com.mj.airport.model.User;
import com.mj.airport.repository.UserRepository;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author marko
 */
@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper mapper;

    //Create init user with username airport
    @PostConstruct
    private void createInitialUser() {
        if (userRepository.findByUsername("airport").isPresent()) return;
        log.info("creating initial user");
        UserDto user = new UserDto();
        user.setEmail("airport@airport.org");
        user.setFirstName("airport");
        user.setLastName("gate");
        user.setPassword("airport123");
        user.setUsername("airport");
        create(user);
    }

    public void create(UserDto dto) {
        User user = mapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

}
