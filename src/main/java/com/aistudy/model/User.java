package com.aistudy.model;

import java.sql.Date;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String profileImage;
    private String bio;
    private Date lastLoginDate;
    private int streakCount;

    // Constructors
    public User() {}

    public User(int id, String name, String email, String password, String profileImage, String bio, Date lastLoginDate, int streakCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.bio = bio;
        this.lastLoginDate = lastLoginDate;
        this.streakCount = streakCount;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Date getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(Date lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }
}
