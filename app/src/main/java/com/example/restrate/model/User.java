package com.example.restrate.model;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String imageURL;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setFromModel(FirebaseUser user) {
        this.setEmail(user.getEmail());
        this.setId(user.getUid());
        this.setFullName(user.getDisplayName());
        this.setImageURL(user.getPhotoUrl().toString());
    }
}
