package com.manish.NewsApplication.preference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferenceImpl implements PreferenceService {

    @Autowired
    private PreferenceRepo  preferenceRepo;
    @Override
    public UserPreferences savePreferences(UserPreferences preferences) {

          return preferenceRepo.save(preferences);
    }

    @Override
    public Optional<UserPreferences> findByID(Long id) {
        return preferenceRepo.findById(id);
    }
}
