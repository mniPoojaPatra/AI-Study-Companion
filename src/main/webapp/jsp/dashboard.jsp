<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - AI Study Companion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Quicksand:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=3" rel="stylesheet">
</head>
<body>

<jsp:include page="navbar.jsp" />

<div class="container py-5">
    <div class="row mb-5">
        <div class="col-12">
            <h2 class="fw-bold">Welcome back, ${sessionScope.user.name}! 👋</h2>
            <p class="text-muted">Here is an overview of your AI study progress.</p>
        </div>
    </div>

    <!-- Stats Row -->
    <div class="row g-4 mb-5">
        <div class="col-md-4">
            <div class="saas-card stat-card text-center h-100">
                <div class="display-4 fw-bold text-primary-accent mb-2">${summaryCount != null ? summaryCount : 0}</div>
                <div class="text-muted fw-medium">Summaries Generated</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="saas-card stat-card text-center h-100">
                <div class="display-4 fw-bold text-primary-accent mb-2">${mcqCount != null ? mcqCount : 0}</div>
                <div class="text-muted fw-medium">MCQs Created</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="saas-card stat-card text-center h-100">
                <div class="display-4 fw-bold text-primary-accent mb-2">${flashcardCount != null ? flashcardCount : 0}</div>
                <div class="text-muted fw-medium">Flashcards Saved</div>
            </div>
        </div>
    </div>

    <!-- Recent Activity -->
    <div class="row">
        <div class="col-12">
            <div class="saas-card">
                <h5 class="fw-bold mb-4">Recent Summaries</h5>
                <% List<Map<String, String>> recent = (List<Map<String, String>>) request.getAttribute("recentSummaries");
                   if (recent != null && !recent.isEmpty()) { %>
                    <div class="list-group list-group-flush">
                    <% for (Map<String, String> map : recent) { %>
                        <div class="list-group-item px-0 py-3 border-bottom border-0" style="border-bottom: 1px solid #e2e8f0 !important;">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1 text-truncate fw-medium text-dark" style="max-width: 70%;"><%= map.get("content") %></h6>
                                <small class="text-muted"><%= map.get("created_at") %></small>
                            </div>
                            <p class="mb-1 text-muted small text-truncate"><%= map.get("summary").substring(0, Math.min(map.get("summary").length(), 120)) %>...</p>
                        </div>
                    <% } %>
                    </div>
                <% } else { %>
                    <div class="text-center text-muted py-5">
                        <p class="mb-0">No recent activity yet. Start generating summaries!</p>
                        <a href="${pageContext.request.contextPath}/summary" class="btn btn-primary-soft mt-3 text-decoration-none d-inline-block">Create a Summary</a>
                    </div>
                <% } %>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
