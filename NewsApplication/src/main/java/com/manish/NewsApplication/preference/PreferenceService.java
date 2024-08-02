package com.manish.NewsApplication.preference;

import java.util.Optional;

public interface PreferenceService {

    UserPreferences savePreferences(UserPreferences preferences);

    Optional<UserPreferences> findByID(Long id);



}
