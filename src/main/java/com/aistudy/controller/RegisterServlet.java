package com.aistudy.controller;

import com.aistudy.dao.UserDAO;
import com.aistudy.model.User;
import com.aistudy.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // --- Input Validation ---
        if (!InputValidator.isValidName(name)) {
            request.setAttribute("error", "Name must be 1-100 characters and contain only letters, spaces, hyphens, or apostrophes.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (!InputValidator.isValidEmail(email)) {
            request.setAttribute("error", "Please enter a valid email address (max 100 characters).");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (!InputValidator.isValidPassword(password)) {
            request.setAttribute("error", "Password must be 8-72 characters and contain at least one letter and one digit.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setName(InputValidator.trimAndCap(name, InputValidator.MAX_NAME_LENGTH));
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(password);

        if (userDAO.registerUser(user)) {
            response.sendRedirect(request.getContextPath() + "/login?registered=true");
        } else {
            request.setAttribute("error", "Registration failed. Email might already exist.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
    }
}
