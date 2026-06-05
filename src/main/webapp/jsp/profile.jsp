<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.aistudy.model.User" %>
<%@ page import="com.aistudy.util.InputValidator" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - AI Study Companion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Quicksand:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2" rel="stylesheet">
    <style>
        .profile-avatar {
            width: 150px;
            height: 150px;
            object-fit: cover;
            border-radius: 50%;
            border: 4px solid var(--primary-color);
            box-shadow: 0 4px 15px rgba(167, 139, 250, 0.3);
        }
    </style>
</head>
<body>

<jsp:include page="navbar.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="saas-card text-center mb-4">
                <h3 class="fw-bold mb-4">My Profile</h3>

                <% String successMsg = (String) request.getAttribute("successMessage");
                   if(successMsg != null) { %>
                    <div class="alert alert-success" role="alert"><%= InputValidator.escapeHtml(successMsg) %></div>
                <% } %>
                <% String errorMsg = (String) request.getAttribute("errorMessage");
                   if(errorMsg != null) { %>
                    <div class="alert alert-danger" role="alert"><%= InputValidator.escapeHtml(errorMsg) %></div>
                <% } %>
                
                <% 
                    User user = (User) session.getAttribute("user");
                    String avatarPath = user.getProfileImage() != null && !user.getProfileImage().equals("default-avatar.png") 
                        ? request.getContextPath() + "/uploads/" + user.getProfileImage() 
                        : "https://ui-avatars.com/api/?name=" + user.getName().replace(" ", "+") + "&background=random";
                %>
                
                <img src="<%= InputValidator.escapeHtml(avatarPath) %>" alt="Profile Picture" class="profile-avatar mb-4">
                
                <form action="${pageContext.request.contextPath}/profile" method="post" enctype="multipart/form-data" class="text-start">
                    
                    <div class="mb-3">
                        <label for="profileImage" class="form-label">Update Profile Picture</label>
                        <input class="form-control" type="file" id="profileImage" name="profileImage" accept=".jpg,.jpeg,.png,.gif,.webp">
                    </div>

                    <div class="mb-3">
                        <label for="name" class="form-label">Full Name</label>
                        <input type="text" class="form-control" id="name" name="name" value="${sessionScope.user.name}"
                               maxlength="100" pattern="[a-zA-Z\s\-'\.]{1,100}" title="Only letters, spaces, hyphens, and apostrophes" required>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email Address (Cannot be changed)</label>
                        <input type="email" class="form-control" id="email" value="${sessionScope.user.email}" readonly disabled>
                    </div>
                    
                    <div class="mb-4">
                        <label for="bio" class="form-label">Bio / About Me</label>
                        <textarea class="form-control" id="bio" name="bio" rows="3" maxlength="500"
                                  placeholder="Tell us a bit about your studies..."
                                  oninput="updateBioCount(this)">${sessionScope.user.bio != null ? sessionScope.user.bio : ''}</textarea>
                        <small class="text-muted"><span id="bioCount">0</span> / 500</small>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary-soft btn-lg">Save Profile</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function updateBioCount(textarea) {
    document.getElementById('bioCount').textContent = textarea.value.length;
}
document.addEventListener('DOMContentLoaded', function() {
    var bio = document.getElementById('bio');
    if (bio) updateBioCount(bio);
});
</script>
</body>
</html>
