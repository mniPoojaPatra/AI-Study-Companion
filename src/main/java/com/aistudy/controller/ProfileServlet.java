package com.aistudy.controller;

import com.aistudy.dao.UserDAO;
import com.aistudy.model.User;
import com.aistudy.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/profile")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 5,      // 5 MB
    maxRequestSize = 1024 * 1024 * 10   // 10 MB
)
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String name = request.getParameter("name");
        String bio = request.getParameter("bio");

        // --- Input Validation ---
        if (!InputValidator.isValidName(name)) {
            request.setAttribute("errorMessage", "Name must be 1-100 characters and contain only letters, spaces, hyphens, or apostrophes.");
            request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
            return;
        }

        // Cap bio length
        bio = InputValidator.trimAndCap(bio, InputValidator.MAX_BIO_LENGTH);

        // Handle Profile Image Upload
        Part filePart = request.getPart("profileImage");
        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            
            // Validate image file type
            if (!InputValidator.isValidImageFilename(fileName)) {
                request.setAttribute("errorMessage", "Invalid image format. Allowed: JPG, PNG, GIF, WEBP.");
                request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
                return;
            }

            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // Generate unique filename to avoid overwriting
            String uniqueFileName = user.getId() + "_" + System.currentTimeMillis() + "_" + fileName;
            filePart.write(uploadPath + File.separator + uniqueFileName);
            
            user.setProfileImage(uniqueFileName);
        }

        user.setName(InputValidator.trimAndCap(name, InputValidator.MAX_NAME_LENGTH));
        user.setBio(bio);

        boolean updated = userDAO.updateProfile(user);

        if (updated) {
            session.setAttribute("user", user); // Update session with new details
            request.setAttribute("successMessage", "Profile updated successfully!");
        } else {
            request.setAttribute("errorMessage", "Failed to update profile.");
        }

        request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
    }
}
