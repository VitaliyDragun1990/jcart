package com.revenat.jcart.security;

import com.revenat.jcart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade to all Security Related Methods
 */
@Service
@Transactional
public class SecurityService {

    @Autowired
    private UserRepository userRepository;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
