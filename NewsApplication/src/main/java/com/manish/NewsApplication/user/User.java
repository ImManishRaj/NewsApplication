package com.manish.NewsApplication.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.manish.NewsApplication.preference.UserPreferences;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Phone number is mandatory")
    private String phone;

    @NotNull(message = "Password is mandatory")
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private UserPreferences userPreference;
}
