package com.manish.NewsApplication.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepo repo;

    @Autowired
    public UserServiceImpl(UserRepo repo) {
        this.repo = repo;
    }

    @Override
    public boolean registerUser(User user) {
        if (user!=null)
        {
            repo.save(user);
            return true;
        }

        return false;
    }

    @Override
    public List<User> getAllUser() {
        return repo.findAll();
    }

    @Override
    public User findByEmail(String email) {
          return repo.findByEmail(email);
    }
}
