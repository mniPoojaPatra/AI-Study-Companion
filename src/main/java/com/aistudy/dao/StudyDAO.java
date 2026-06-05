package com.aistudy.dao;

import com.aistudy.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudyDAO {

    public void saveSummary(int userId, String content, String summary) {
        String sql = "INSERT INTO study_history (user_id, content, summary) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.setString(3, summary);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMCQ(com.aistudy.model.MCQ mcq) {
        String sql = "INSERT INTO mcqs (user_id, question, option_a, option_b, option_c, option_d, answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mcq.getUserId());
            ps.setString(2, mcq.getQuestion());
            ps.setString(3, mcq.getOptionA());
            ps.setString(4, mcq.getOptionB());
            ps.setString(5, mcq.getOptionC());
            ps.setString(6, mcq.getOptionD());
            ps.setString(7, mcq.getAnswer());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveFlashcard(com.aistudy.model.Flashcard flashcard) {
        String sql = "INSERT INTO flashcards (user_id, question, answer) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flashcard.getUserId());
            ps.setString(2, flashcard.getQuestion());
            ps.setString(3, flashcard.getAnswer());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Whitelist of allowed table names for the getCount query
    private static final Set<String> ALLOWED_TABLES = Set.of("study_history", "mcqs", "flashcards");

    public int getCount(int userId, String tableName) {
        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Map<String, String>> getRecentSummaries(int userId) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT content, summary, created_at FROM study_history WHERE user_id = ? ORDER BY created_at DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    String content = rs.getString("content");
                    if (content.length() > 50) {
                        content = content.substring(0, 50) + "...";
                    }
                    map.put("content", content);
                    map.put("summary", rs.getString("summary"));
                    map.put("created_at", rs.getString("created_at"));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
