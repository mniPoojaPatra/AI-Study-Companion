package com.aistudy.controller;

import com.aistudy.dao.AILogDAO;
import com.aistudy.dao.StudyDAO;
import com.aistudy.model.User;
import com.aistudy.util.GeminiAPI;
import com.aistudy.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import com.aistudy.util.FileExtractor;

import java.io.IOException;

@WebServlet("/summary")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class SummaryServlet extends HttpServlet {
    private StudyDAO studyDAO;

    @Override
    public void init() {
        studyDAO = new StudyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/summary.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String content = request.getParameter("content");
        
        try {
            Part filePart = request.getPart("fileUpload");
            if (filePart != null && filePart.getSize() > 0) {
                String extracted = FileExtractor.extractText(filePart);
                if (extracted != null) {
                    content = extracted; // Override text area with file content
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error reading file: " + e.getMessage());
            request.getRequestDispatcher("/jsp/summary.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (content != null && !content.trim().isEmpty()) {
            // --- Input Restriction: cap content length ---
            content = InputValidator.trimAndCap(content, InputValidator.MAX_CONTENT_LENGTH);

            String prompt = "Summarize the following notes in simple points:\n\n" + content;
            
            try {
                String aiResponse = GeminiAPI.generateContent(prompt);
                
                AILogDAO.logRequest("Summary", prompt, aiResponse);
                studyDAO.saveSummary(user.getId(), content, aiResponse);
                
                request.setAttribute("summary", aiResponse);
                request.setAttribute("originalContent", content);
                
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Failed to generate summary: " + e.getMessage());
                AILogDAO.logRequest("Summary_Error", prompt, e.getMessage());
            }
        } else {
            request.setAttribute("error", "Please provide some content to summarize.");
        }
        
        request.getRequestDispatcher("/jsp/summary.jsp").forward(request, response);
    }
}
