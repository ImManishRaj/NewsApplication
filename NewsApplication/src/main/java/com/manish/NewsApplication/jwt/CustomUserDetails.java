package com.manish.NewsApplication.jwt;

import com.manish.NewsApplication.user.User;
import com.manish.NewsApplication.user.UserRepo;
import com.manish.NewsApplication.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetails implements UserDetailsService {

        @Autowired
        private UserRepo repo;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            User user = repo.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        }
}
