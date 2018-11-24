package com.revenat.jcart.admin.security;

import com.revenat.jcart.entities.User;
import com.revenat.jcart.security.JCartSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JCartSecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        User user = securityService.findUserByEmail(userName);
        if (user == null) {
            throw new UsernameNotFoundException("Email " + userName + " not found.");
        }
        return new AuthenticatedUser(user);
    }
}
