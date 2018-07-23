package com.wichisoft.firebasechat.model;

public class User {

    private String userId;
    private String displayName;
    private String email;
    private String image;
    private Double latitude;
    private Double longitude;

    public User() {
    }

    public User(String userId, String displayName, String email, Double latitude, Double longitude) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }

    public void setImage(String image) {
        this.image = image;
    }
}