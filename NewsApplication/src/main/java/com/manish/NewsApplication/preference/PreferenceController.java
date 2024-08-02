package com.manish.NewsApplication.preference;

import com.manish.NewsApplication.user.User;
import com.manish.NewsApplication.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user/v1/pref")
public class PreferenceController {

    @Autowired
    private PreferenceImpl preference;

    @Autowired
    private UserService userService;



    @PostMapping("/addPreferences/{email}")
    public ResponseEntity<?> registerPreferences(@PathVariable String email,
                                                      @RequestBody CategoriesRequest categoriesRequest)
    {

        System.out.println("Received categories: " + categoriesRequest.getCategories());
        User user=userService.findByEmail(email);

        if (user==null)
        {
            return ResponseEntity.notFound().build();
        }

        UserPreferences userPreferences=user.getUserPreference();
        if (userPreferences==null)
        {
                userPreferences =new UserPreferences();
                userPreferences.setUser(user);
        }

        userPreferences.setCategoriesOfInterest(categoriesRequest.getCategories());
        preference.savePreferences(userPreferences);

        return ResponseEntity.ok(userPreferences.getCategoriesOfInterest().toString());
    }


    @GetMapping("/getUserPref/{email}")
    public ResponseEntity<?> getUserPref(@PathVariable String email) {
        try {
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            UserPreferences userPreferences = user.getUserPreference();

            if (userPreferences == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(userPreferences);
        } catch (Exception e) {
            // Log the exception

            // Return a server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving user preferences.");
        }
    }


}
