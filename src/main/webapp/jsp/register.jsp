<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.aistudy.util.InputValidator" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - AI Study Companion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2" rel="stylesheet">
</head>
<body class="d-flex align-items-center min-vh-100">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="text-center mb-4">
                <h2 class="text-primary-accent fw-bold">Create Account</h2>
                <p class="text-muted">Start your AI-powered learning journey.</p>
            </div>
            
            <div class="saas-card">
                <% String error = (String) request.getAttribute("error");
                   if(error != null) { %>
                    <div class="alert alert-danger" role="alert"><%= InputValidator.escapeHtml(error) %></div>
                <% } %>

                <form action="${pageContext.request.contextPath}/register" method="post">
                    <div class="mb-3">
                        <label for="name" class="form-label">Full Name</label>
                        <input type="text" class="form-control" id="name" name="name" placeholder="John Doe"
                               maxlength="100" pattern="[a-zA-Z\s\-'\.]{1,100}" title="Only letters, spaces, hyphens, and apostrophes" required>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email address</label>
                        <input type="email" class="form-control" id="email" name="email" placeholder="name@example.com"
                               maxlength="100" required>
                    </div>
                    <div class="mb-4">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" placeholder="••••••••"
                               minlength="8" maxlength="72" title="8-72 characters, at least one letter and one digit" required>
                        <div class="form-text">Min 8 characters, must include a letter and a digit.</div>
                    </div>
                    <button type="submit" class="btn btn-primary-soft w-100">Sign Up</button>
                </form>
                
                <div class="text-center mt-4">
                    <span class="text-muted">Already have an account?</span> 
                    <a href="${pageContext.request.contextPath}/login" class="text-primary-accent fw-medium text-decoration-none">Log in</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
