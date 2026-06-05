<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.aistudy.util.InputValidator" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Summarize Notes - AI Study Companion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2" rel="stylesheet">
</head>
<body>

<jsp:include page="navbar.jsp" />

<div class="container py-5">
    <div class="row mb-4">
        <div class="col-12">
            <h2 class="fw-bold">Summarize Your Notes</h2>
            <p class="text-muted">Paste your lengthy study material below and let AI extract the key points.</p>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-6 mb-4">
            <div class="saas-card h-100">
                <form action="${pageContext.request.contextPath}/summary" method="post" enctype="multipart/form-data" class="h-100 d-flex flex-column">
                    <% String error = (String) request.getAttribute("error");
                       if(error != null) { %>
                        <div class="alert alert-danger" role="alert"><%= InputValidator.escapeHtml(error) %></div>
                    <% } %>
                    <div class="mb-3 flex-grow-1 d-flex flex-column">
                        <label for="fileUpload" class="form-label text-muted small">Upload .TXT or .PDF</label>
                        <input class="form-control mb-3" type="file" id="fileUpload" name="fileUpload" accept=".txt,.pdf">
                        
                        <div class="d-flex justify-content-between align-items-center">
                            <label for="content" class="form-label">Or paste your text here...</label>
                            <small class="text-muted"><span id="charCount">0</span> / 50,000</small>
                        </div>
                        <textarea class="form-control flex-grow-1" id="content" name="content" placeholder="Paste your text here..." maxlength="50000" style="min-height: 200px;" oninput="updateCount(this, 'charCount')"><%= (request.getAttribute("originalContent") != null) ? InputValidator.escapeHtml((String) request.getAttribute("originalContent")) : "" %></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary-soft w-100">Generate Summary</button>
                </form>
            </div>
        </div>

        <div class="col-lg-6 mb-4">
            <div class="saas-card h-100">
                <h5 class="fw-bold mb-3">AI Summary</h5>
                <% String summary = (String) request.getAttribute("summary");
                   if(summary != null) { %>
                    <div class="p-3 bg-light rounded" style="white-space: pre-wrap; font-size: 0.95rem; line-height: 1.6;"><%= InputValidator.escapeHtml(summary) %></div>
                <% } else { %>
                    <div class="d-flex align-items-center justify-content-center h-100 text-muted" style="min-height: 300px;">
                        <div class="text-center">
                            <p class="mb-0">Your summary will appear here.</p>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function updateCount(textarea, countId) {
    document.getElementById(countId).textContent = textarea.value.length;
}
// Initialize count on page load
document.addEventListener('DOMContentLoaded', function() {
    var ta = document.getElementById('content');
    if (ta) updateCount(ta, 'charCount');
});
</script>
</body>
</html>
