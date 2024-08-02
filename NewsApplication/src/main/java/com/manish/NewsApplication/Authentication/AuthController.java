package com.manish.NewsApplication.Authentication;

import com.manish.NewsApplication.jwt.JwtUtils;
import com.manish.NewsApplication.user.User;
import com.manish.NewsApplication.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {


    @Autowired
    private JwtUtils jwtUtils;


    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLogin login) {
        User user = userService.findByEmail(login.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            String jwtToken = jwtUtils.generateToken(user);

            // Prepare response with JWT token and other details
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("employeeName", user.getName());
            response.put("ID", user.getId());
            response.put("Email",user.getEmail());
            response.put("Phone",user.getPhone());
            response.put("jwt_token", jwtToken); // Include JWT token in response

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
