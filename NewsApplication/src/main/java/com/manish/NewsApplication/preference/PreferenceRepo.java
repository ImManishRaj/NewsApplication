package com.manish.NewsApplication.preference;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepo extends JpaRepository<UserPreferences,Long> {
}
