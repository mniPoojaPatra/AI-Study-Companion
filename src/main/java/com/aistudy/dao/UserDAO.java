package com.aistudy.dao;

import com.aistudy.model.User;
import com.aistudy.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, last_login_date, streak_count) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.setInt(5, 1); // Initial streak

            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setProfileImage(rs.getString("profile_image"));
                    user.setBio(rs.getString("bio"));
                    user.setLastLoginDate(rs.getDate("last_login_date"));
                    user.setStreakCount(rs.getInt("streak_count"));

                    updateStreak(user, conn);
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateStreak(User user, Connection conn) {
        try {
            LocalDate today = LocalDate.now();
            Date lastLogin = user.getLastLoginDate();
            
            if (lastLogin != null) {
                LocalDate lastDate = lastLogin.toLocalDate();
                
                if (lastDate.equals(today.minusDays(1))) {
                    // Consecutive day
                    user.setStreakCount(user.getStreakCount() + 1);
                } else if (lastDate.isBefore(today.minusDays(1))) {
                    // Missed a day, reset streak
                    user.setStreakCount(1);
                }
                // If lastDate.equals(today), do nothing to streak
            } else {
                user.setStreakCount(1);
            }
            
            user.setLastLoginDate(Date.valueOf(today));
            
            String sql = "UPDATE users SET last_login_date = ?, streak_count = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, user.getLastLoginDate());
                ps.setInt(2, user.getStreakCount());
                ps.setInt(3, user.getId());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET name = ?, profile_image = ?, bio = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getName());
            ps.setString(2, user.getProfileImage());
            ps.setString(3, user.getBio());
            ps.setInt(4, user.getId());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
