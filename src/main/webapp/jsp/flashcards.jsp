<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.aistudy.model.Flashcard" %>
<%@ page import="com.aistudy.util.InputValidator" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Generate Flashcards - AI Study Companion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=2" rel="stylesheet">
    <style>
        .flashcard {
            perspective: 1000px;
            height: 200px;
            cursor: pointer;
        }
        .flashcard-inner {
            position: relative;
            width: 100%;
            height: 100%;
            text-align: center;
            transition: transform 0.6s;
            transform-style: preserve-3d;
        }
        .flashcard.is-flipped .flashcard-inner {
            transform: rotateY(180deg);
        }
        .flashcard-front, .flashcard-back {
            position: absolute;
            width: 100%;
            height: 100%;
            -webkit-backface-visibility: hidden;
            backface-visibility: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1.5rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        }
        .flashcard-front {
            background-color: var(--card-bg);
            border: 2px solid var(--primary-color);
            color: var(--text-main);
        }
        .flashcard-back {
            background-color: var(--primary-color);
            color: white;
            transform: rotateY(180deg);
        }
    </style>
</head>
<body>

<jsp:include page="navbar.jsp" />

<div class="container py-5">
    <div class="row mb-4">
        <div class="col-12">
            <h2 class="fw-bold">AI Flashcard Generator</h2>
            <p class="text-muted">Extract Q&A pairs for active recall practice.</p>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-5 mb-4">
            <div class="saas-card h-100">
                <form action="${pageContext.request.contextPath}/flashcards" method="post" enctype="multipart/form-data" class="h-100 d-flex flex-column">
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
                    <button type="submit" class="btn btn-primary-soft w-100">Generate Flashcards</button>
                </form>
            </div>
        </div>

        <div class="col-lg-7 mb-4">
            <div class="saas-card h-100">
                <h5 class="fw-bold mb-3">Your Flashcards</h5>
                <p class="text-muted small mb-4">Click a card to reveal the answer.</p>
                
                <div class="row g-4">
                <% List<Flashcard> flashcards = (List<Flashcard>) request.getAttribute("flashcards");
                   if(flashcards != null && !flashcards.isEmpty()) { 
                       for(Flashcard f : flashcards) { %>
                        <div class="col-md-6">
                            <div class="flashcard" onclick="this.classList.toggle('is-flipped')">
                                <div class="flashcard-inner">
                                    <div class="flashcard-front">
                                        <h6 class="fw-bold mb-0"><%= InputValidator.escapeHtml(f.getQuestion()) %></h6>
                                    </div>
                                    <div class="flashcard-back">
                                        <p class="mb-0 fw-medium"><%= InputValidator.escapeHtml(f.getAnswer()) %></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                <%     }
                   } else { %>
                    <div class="col-12 d-flex align-items-center justify-content-center text-muted" style="min-height: 200px;">
                        <div class="text-center">
                            <p class="mb-0">Your flashcards will appear here.</p>
                        </div>
                    </div>
                <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function updateCount(textarea, countId) {
    document.getElementById(countId).textContent = textarea.value.length;
}
document.addEventListener('DOMContentLoaded', function() {
    var ta = document.getElementById('content');
    if (ta) updateCount(ta, 'charCount');
});
</script>
</body>
</html>
