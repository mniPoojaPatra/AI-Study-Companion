package com.aistudy.controller;

import com.aistudy.dao.UserDAO;
import com.aistudy.model.User;
import com.aistudy.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // --- Input Validation ---
        if (email == null || email.isBlank() || email.trim().length() > InputValidator.MAX_EMAIL_LENGTH) {
            request.setAttribute("error", "Please enter a valid email address.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }

        if (password == null || password.isEmpty() || password.length() > InputValidator.MAX_PASSWORD_LENGTH) {
            request.setAttribute("error", "Please enter a valid password.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.loginUser(email.trim(), password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
}
