package com.aistudy.dao;

import com.aistudy.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AILogDAO {

    public static void logRequest(String featureType, String requestText, String responseText) {
        String sql = "INSERT INTO ai_logs (feature_type, request_text, response_text) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, featureType);
            ps.setString(2, requestText);
            ps.setString(3, responseText);
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to log AI request.");
        }
    }
}
