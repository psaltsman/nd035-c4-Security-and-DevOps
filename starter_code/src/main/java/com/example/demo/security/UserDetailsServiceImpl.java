package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.rmi.server.ExportException;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    public static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: " + username);

        User user = null;

        try {

            user = userRepository.findByUsername(username);

        } catch (Exception ex) {

            log.error(ex.getClass().getName() + ": " + ex.getMessage());
        }

        if (user == null) {

            log.error("Username " + username + " NOT FOUND");

            throw new UsernameNotFoundException(username);
        }

        log.info("Username " + username + " found in DB");

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
