<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI Study Companion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2" rel="stylesheet">
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100 text-center">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <h1 class="display-4 fw-bold text-primary-accent mb-3">AI Study Companion</h1>
            <p class="lead text-muted mb-5">Your smart learning assistant. Upload notes, generate summaries, create MCQs, and generate flashcards instantly.</p>
            
            <div class="d-flex gap-3 justify-content-center">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary-soft btn-lg px-5">Login</a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-secondary btn-lg px-5 border-2 fw-medium" style="border-radius: 8px;">Register</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
