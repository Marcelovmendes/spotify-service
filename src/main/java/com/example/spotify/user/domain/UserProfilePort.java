package com.example.spotify.user.domain;

import com.example.spotify.user.domain.entity.UserEntity;
import se.michaelthelin.spotify.model_objects.specification.User;


public interface UserProfilePort {
    User getCurrentUsersProfileSync(String token);
    UserEntity getCurrentUsersProfileAsync(String token);
}
