CREATE DATABASE IF NOT EXISTS ai_study_companion;
USE ai_study_companion;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    profile_image VARCHAR(255) DEFAULT 'default-avatar.png',
    bio TEXT,
    last_login_date DATE,
    streak_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS study_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    content TEXT,
    summary TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mcqs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    question TEXT,
    option_a TEXT,
    option_b TEXT,
    option_c TEXT,
    option_d TEXT,
    answer VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS flashcards (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    question TEXT,
    answer TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ai_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    feature_type VARCHAR(50),
    request_text TEXT,
    response_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
