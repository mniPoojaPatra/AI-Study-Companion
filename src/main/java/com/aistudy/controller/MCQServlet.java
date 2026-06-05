package com.aistudy.controller;

import com.aistudy.dao.AILogDAO;
import com.aistudy.dao.StudyDAO;
import com.aistudy.model.MCQ;
import com.aistudy.model.User;
import com.aistudy.util.GeminiAPI;
import com.aistudy.util.InputValidator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/mcq")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class MCQServlet extends HttpServlet {
    private StudyDAO studyDAO;

    @Override
    public void init() {
        studyDAO = new StudyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/mcq.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String content = request.getParameter("content");
        
        try {
            Part filePart = request.getPart("fileUpload");
            if (filePart != null && filePart.getSize() > 0) {
                String extracted = FileExtractor.extractText(filePart);
                if (extracted != null) {
                    content = extracted;
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error reading file: " + e.getMessage());
            request.getRequestDispatcher("/jsp/mcq.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (content != null && !content.trim().isEmpty()) {
            // --- Input Restriction: cap content length ---
            content = InputValidator.trimAndCap(content, InputValidator.MAX_CONTENT_LENGTH);

            String prompt = "Generate 5 multiple choice questions from this content.\n" +
                            "Return the output ONLY as a JSON array of objects with the exact keys: " +
                            "[\n  {\n    \"question\": \"...\",\n    \"option_a\": \"...\",\n    \"option_b\": \"...\",\n    \"option_c\": \"...\",\n    \"option_d\": \"...\",\n    \"answer\": \"...\"\n  }\n]\n" +
                            "Do not include any markdown formatting like ```json.\n\nContent:\n" + content;
            
            try {
                String aiResponseRaw = GeminiAPI.generateContent(prompt);
                AILogDAO.logRequest("MCQ", prompt, aiResponseRaw);

                // Clean the response if Gemini wraps it in markdown despite instructions
                String aiResponse = aiResponseRaw.trim();
                if (aiResponse.startsWith("```json")) {
                    aiResponse = aiResponse.substring(7);
                }
                if (aiResponse.startsWith("```")) {
                    aiResponse = aiResponse.substring(3);
                }
                if (aiResponse.endsWith("```")) {
                    aiResponse = aiResponse.substring(0, aiResponse.length() - 3);
                }
                aiResponse = aiResponse.trim();

                JsonArray jsonArray = JsonParser.parseString(aiResponse).getAsJsonArray();
                List<MCQ> mcqs = new ArrayList<>();

                for (JsonElement element : jsonArray) {
                    JsonObject obj = element.getAsJsonObject();
                    MCQ mcq = new MCQ();
                    mcq.setUserId(user.getId());
                    mcq.setQuestion(obj.get("question").getAsString());
                    mcq.setOptionA(obj.get("option_a").getAsString());
                    mcq.setOptionB(obj.get("option_b").getAsString());
                    mcq.setOptionC(obj.get("option_c").getAsString());
                    mcq.setOptionD(obj.get("option_d").getAsString());
                    mcq.setAnswer(obj.get("answer").getAsString());
                    
                    studyDAO.saveMCQ(mcq);
                    mcqs.add(mcq);
                }

                request.setAttribute("mcqs", mcqs);
                request.setAttribute("originalContent", content);

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Failed to generate MCQs: " + e.getMessage());
                AILogDAO.logRequest("MCQ_Error", prompt, e.getMessage());
            }
        } else {
            request.setAttribute("error", "Please provide some content to generate MCQs.");
        }
        
        request.getRequestDispatcher("/jsp/mcq.jsp").forward(request, response);
    }
}
