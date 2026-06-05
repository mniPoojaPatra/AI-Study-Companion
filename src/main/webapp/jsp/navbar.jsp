<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg glass-navbar sticky-top">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">AI Study</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/summary">Summarize Notes</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/mcq">Generate MCQs</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/flashcards">Flashcards</a>
                </li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item d-flex align-items-center me-3">
                    <span class="streak-badge">
                        🔥 Streak: ${sessionScope.user.streakCount} Day(s)
                    </span>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle fw-medium d-flex align-items-center" href="#" role="button" data-bs-toggle="dropdown">
                        <% 
                            com.aistudy.model.User currentUser = (com.aistudy.model.User) session.getAttribute("user");
                            String navAvatar = currentUser != null && currentUser.getProfileImage() != null && !currentUser.getProfileImage().equals("default-avatar.png") 
                                ? request.getContextPath() + "/uploads/" + currentUser.getProfileImage() 
                                : "https://ui-avatars.com/api/?name=" + (currentUser != null ? currentUser.getName().replace(" ", "+") : "User") + "&background=random";
                        %>
                        <img src="<%= navAvatar %>" alt="Profile" style="width: 32px; height: 32px; border-radius: 50%; object-fit: cover; margin-right: 8px; border: 2px solid var(--primary-color);">
                        ${sessionScope.user.name}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile">My Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item py-2 text-danger" href="${pageContext.request.contextPath}/logout">Log out</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>
