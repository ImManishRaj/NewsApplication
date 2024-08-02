package com.manish.NewsApplication.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;

@RestController
@RequestMapping("/user/v1")
public class UserController {

private final UserService userService;

private static final Logger logger= LogManager.getLogger(UserController.class);



   @Autowired
   public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/registration")
    public ResponseEntity<String> saveUser(@RequestBody User user)
    {


        String HashedPassword= passwordEncoder.encode(user.getPassword());
        user.setPassword(HashedPassword);

        System.out.println("Password Hashed");

        logger.info("Password has been Hashed");
        if (userService.registerUser(user))
        {
            return ResponseEntity.ok().body("Registration completed");
        }


        return ResponseEntity.badRequest().body("Registration is unsuccessful");
    }


    @GetMapping("/get-all-user")
    public List<User> getAll()
    {
        return  userService.getAllUser();
    }


    @GetMapping("/findByEmail/{email}")
    public User findUser(@PathVariable String email)
    {
        return userService.findByEmail(email);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user )
    {

        if (user==null)
        {
            return ResponseEntity.badRequest().body("Invalid details");
        }
        String email=user.getEmail();
        String password= user.getPassword();

        if (email.isEmpty() || password.isEmpty())
        {

            return ResponseEntity.badRequest().body("Both username and password is required");
        }

        User SavedUser=userService.findByEmail(email);

        String storedPassword= SavedUser.getPassword();

        if (!passwordEncoder.matches(password,storedPassword))
        {
           logger.info("Password Mismatch");
            return ResponseEntity.badRequest().body("Password is incorrect");
        }
        logger.info("Login successful");
        return ResponseEntity.ok().body("Login Successful");
    }
}
