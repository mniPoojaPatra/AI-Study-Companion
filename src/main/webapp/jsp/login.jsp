<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.aistudy.util.InputValidator" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - AI Study Companion</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/css/style.css?v=2" rel="stylesheet">
</head>
<body class="d-flex align-items-center min-vh-100">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="text-center mb-4">
                <h2 class="text-primary-accent fw-bold">AI Study Companion</h2>
                <p class="text-muted">Welcome back! Please login to your account.</p>
            </div>
            
            <div class="saas-card">
                <% String error = (String) request.getAttribute("error");
                   if(error != null) { %>
                    <div class="alert alert-danger" role="alert"><%= InputValidator.escapeHtml(error) %></div>
                <% } %>
                
                <% String registered = request.getParameter("registered");
                   if("true".equals(registered)) { %>
                    <div class="alert alert-success" role="alert">Registration successful. Please login.</div>
                <% } %>

                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="mb-3">
                        <label for="email" class="form-label">Email address</label>
                        <input type="email" class="form-control" id="email" name="email" placeholder="name@example.com" maxlength="100" required>
                    </div>
                    <div class="mb-4">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" placeholder="••••••••" maxlength="72" required>
                    </div>
                    <button type="submit" class="btn btn-primary-soft w-100">Log In</button>
                </form>
                
                <div class="text-center mt-4">
                    <span class="text-muted">Don't have an account?</span> 
                    <a href="${pageContext.request.contextPath}/register" class="text-primary-accent fw-medium text-decoration-none">Sign up</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
