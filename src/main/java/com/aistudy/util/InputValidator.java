package com.aistudy.util;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Centralized input validation and sanitization utility.
 * All validation rules are defined here so servlets stay DRY.
 */
public class InputValidator {

    // --- Constants ---
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 72; // BCrypt limit
    public static final int MAX_BIO_LENGTH = 500;
    public static final int MAX_CONTENT_LENGTH = 50_000; // 50k chars for AI content
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB

    // --- Patterns ---
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-'.]{1,100}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_LETTER = Pattern.compile("[a-zA-Z]");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile("[0-9]");

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    // --- Validation Methods ---

    public static boolean isValidName(String name) {
        return name != null && !name.isBlank() && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && !email.isBlank()
                && email.trim().length() <= MAX_EMAIL_LENGTH
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }
        return PASSWORD_LETTER.matcher(password).find() && PASSWORD_DIGIT.matcher(password).find();
    }

    public static boolean isWithinLength(String text, int maxLength) {
        return text == null || text.length() <= maxLength;
    }

    public static boolean isValidImageFilename(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        String lower = fileName.toLowerCase().trim();
        return ALLOWED_IMAGE_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    /**
     * Trims and caps text to the given maximum length.
     * Returns null if input is null.
     */
    public static String trimAndCap(String text, int maxLength) {
        if (text == null) return null;
        String trimmed = text.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }

    /**
     * HTML-encodes a string to prevent XSS.
     * Replaces &, <, >, ", and ' with their HTML entities.
     */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
