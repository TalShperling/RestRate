package com.example.restrate.model;

import com.google.firebase.auth.FirebaseUser;

public class User {
    // For future option when changing from firebase
    private FirebaseUser user;

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public String getUserId(){
        return user.getUid();
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }
}
