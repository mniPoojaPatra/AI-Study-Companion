package com.aistudy.controller;

import com.aistudy.dao.StudyDAO;
import com.aistudy.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private StudyDAO studyDAO;

    @Override
    public void init() {
        studyDAO = new StudyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user != null) {
            request.setAttribute("summaryCount", studyDAO.getCount(user.getId(), "study_history"));
            request.setAttribute("mcqCount", studyDAO.getCount(user.getId(), "mcqs"));
            request.setAttribute("flashcardCount", studyDAO.getCount(user.getId(), "flashcards"));
            request.setAttribute("recentSummaries", studyDAO.getRecentSummaries(user.getId()));
        }

        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }
}
