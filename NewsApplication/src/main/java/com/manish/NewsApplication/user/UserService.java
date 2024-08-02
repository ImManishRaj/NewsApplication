package com.manish.NewsApplication.user;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface UserService {

   boolean registerUser(@RequestBody User user);

   List<User> getAllUser();

   User findByEmail(String email);
}
