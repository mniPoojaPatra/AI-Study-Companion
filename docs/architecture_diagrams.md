# Architecture & Flow Diagrams

This document illustrates the complete software architecture of the **AI Study Companion** application. It details the core **Model-View-Controller (MVC)** design pattern implementation, full static class structural relationships, and dynamic interaction sequences across all major user pathways.

---

## 1. High-Level MVC Design Pattern Flow

The application follows the classic Java EE **MVC (Model-View-Controller)** pattern where HTTP requests pass through standard lifecycle filters before reaching domain-specific Servlets (Controllers). The Servlets utilize Data Access Objects (DAOs) to interact with the relational MySQL Database (Models) and forward processed structures to JavaServer Pages (Views) for presentation.

```mermaid
graph LR
    %% Defining external/internal entities
    Client([Web Client / Browser])
    
    subgraph Controller Layer [Controller / Routing Layer]
        Filters[EncodingFilter & AuthFilter]
        Servlets[Domain Servlets<br/>Dashboard, Summary, MCQ, Flashcard, Profile]
    end
    
    subgraph Business Logic & Integration [Services & Utils]
        FileExt[FileExtractor<br/>TXT / PDF Parsing]
        Gemini[GeminiAPI Utility]
        ExtAI((Google Gemini AI))
    end
    
    subgraph Data Access Layer [DAO Layer]
        DAOs[UserDAO, StudyDAO, AILogDAO]
    end
    
    subgraph Model Layer [Data Entities & Storage]
        Models[User, MCQ, Flashcard]
        DB[(MySQL Database)]
    end
    
    subgraph View Layer [Presentation Layer]
        JSPs[JSP Templates<br/>dashboard.jsp, profile.jsp, summary.jsp, etc.]
    end

    %% Flow mapping
    Client -- "1. HTTP Request" --> Filters
    Filters -- "2. Authenticated/Clean Request" --> Servlets
    Servlets -- "3. File Extraction (If Multipart)" --> FileExt
    Servlets -- "4. Generative AI Prompt" --> Gemini
    Gemini -- "REST / Stream" --> ExtAI
    Servlets -- "5. Database CRUD" --> DAOs
    DAOs -- "Entity Mapping" --> Models
    DAOs -- "JDBC / SQL Queries" --> DB
    Servlets -- "6. Set Attributes & Forward" --> JSPs
    JSPs -- "7. Render HTML5 / CSS3" --> Client
```

---

## 2. Complete Class Diagram

Below is the structured representation of all classes present within the project packages (`com.aistudy.*`). It showcases inheritance, field compositions, and service calls.

```mermaid
classDiagram
    %% Models
    class User {
        -int id
        -String name
        -String email
        -String password
        -String profileImage
        -String bio
        -Date lastLoginDate
        -int streakCount
        +getId() int
        +getName() String
        +getProfileImage() String
        +getStreakCount() int
        +setStreakCount(int)
    }

    class MCQ {
        -int id
        -int userId
        -String question
        -String optionA
        -String optionB
        -String optionC
        -String optionD
        -String answer
        +getQuestion() String
        +getAnswer() String
    }

    class Flashcard {
        -int id
        -int userId
        -String question
        -String answer
        +getQuestion() String
        +getAnswer() String
    }

    %% DAOs
    class UserDAO {
        +registerUser(User user) boolean
        +loginUser(String email, String password) User
        +updateProfile(User user) boolean
        -updateStreak(User user, Connection conn)
    }

    class StudyDAO {
        +saveSummary(int userId, String content, String summary)
        +saveMCQ(MCQ mcq)
        +saveFlashcard(Flashcard flashcard)
        +getCount(int userId, String tableName) int
        +getRecentSummaries(int userId) List~Map~
    }

    class AILogDAO {
        +logRequest(String featureType, String requestText, String responseText)$
    }

    %% Controllers
    class LoginServlet {
        -UserDAO userDAO
        +doGet(req, resp)
        +doPost(req, resp)
    }

    class RegisterServlet {
        -UserDAO userDAO
        +doGet(req, resp)
        +doPost(req, resp)
    }

    class DashboardServlet {
        -StudyDAO studyDAO
        +doGet(req, resp)
    }

    class SummaryServlet {
        -StudyDAO studyDAO
        +doGet(req, resp)
        +doPost(req, resp)
    }

    class MCQServlet {
        -StudyDAO studyDAO
        +doGet(req, resp)
        +doPost(req, resp)
    }

    class FlashcardServlet {
        -StudyDAO studyDAO
        +doGet(req, resp)
        +doPost(req, resp)
    }

    class ProfileServlet {
        -UserDAO userDAO
        +doGet(req, resp)
        +doPost(req, resp)
    }

    %% Utilities
    class DBConnection {
        +getConnection()$ Connection
    }

    class GeminiAPI {
        -String API_KEY
        -String API_URL
        +generateContent(String prompt)$ String
    }

    class FileExtractor {
        +extractText(Part filePart)$ String
    }

    %% Relationships
    UserDAO ..> User : utilizes
    StudyDAO ..> MCQ : utilizes
    StudyDAO ..> Flashcard : utilizes
    UserDAO ..> DBConnection : uses
    StudyDAO ..> DBConnection : uses
    AILogDAO ..> DBConnection : uses

    LoginServlet --> UserDAO : has-a
    RegisterServlet --> UserDAO : has-a
    ProfileServlet --> UserDAO : has-a
    DashboardServlet --> StudyDAO : has-a
    SummaryServlet --> StudyDAO : has-a
    MCQServlet --> StudyDAO : has-a
    FlashcardServlet --> StudyDAO : has-a

    SummaryServlet ..> GeminiAPI : invokes
    MCQServlet ..> GeminiAPI : invokes
    FlashcardServlet ..> GeminiAPI : invokes
    SummaryServlet ..> FileExtractor : invokes
    MCQServlet ..> FileExtractor : invokes
    FlashcardServlet ..> FileExtractor : invokes
```

---

## 3. Sequence Diagrams for Core Application Flows

### 3.1 Authentication Flow (Registration & Login)
Illustrates how users onboard, authenticate, and update consecutive study streaks.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web Browser
    participant Controller as LoginServlet / RegisterServlet
    participant DAO as UserDAO
    participant DB as MySQL Database
    participant Session as HTTP Session
    participant View as JSP Page

    %% Registration Pathway
    Note over Client, View: 1. User Registration Flow
    Client->>Controller: POST /register (name, email, password)
    Controller->>DAO: registerUser(User)
    DAO->>DB: INSERT INTO users (...)
    DB-->>DAO: Success / Rows affected
    DAO-->>Controller: true
    Controller->>Client: Redirect to /login with Success Message

    %% Login Pathway
    Note over Client, View: 2. User Login & Streak Update Flow
    Client->>Controller: POST /login (email, password)
    Controller->>DAO: loginUser(email, password)
    DAO->>DB: SELECT * FROM users WHERE email=? AND password=?
    DB-->>DAO: ResultSet (User Record)
    
    %% Internal Streak Check
    Note over DAO: Execute private updateStreak(User)<br/>Compares last_login_date with current date
    DAO->>DB: UPDATE users SET last_login_date=?, streak_count=?
    DB-->>DAO: Acknowledged
    
    DAO-->>Controller: User Entity Object
    Controller->>Session: setAttribute("user", User)
    Controller->>Client: Redirect to /dashboard
```

---

### 3.2 Dashboard Loading Flow
Highlights how user history and statistical counts are aggregated dynamically upon loading the landing interface.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web Browser
    participant Filter as AuthFilter
    participant Controller as DashboardServlet
    participant DAO as StudyDAO
    participant DB as MySQL Database
    participant View as dashboard.jsp

    Client->>Filter: GET /dashboard
    Filter->>Filter: Verify HTTP Session exists
    Filter->>Controller: Forward Request
    
    Controller->>DAO: getCount(userId, "study_history")
    DAO->>DB: SELECT COUNT(*) FROM study_history
    DB-->>DAO: Count result
    DAO-->>Controller: Integer value
    
    Controller->>DAO: getCount(userId, "mcqs")
    DAO->>DB: SELECT COUNT(*) FROM mcqs
    DB-->>DAO: Count result
    DAO-->>Controller: Integer value

    Controller->>DAO: getCount(userId, "flashcards")
    DAO->>DB: SELECT COUNT(*) FROM flashcards
    DB-->>DAO: Count result
    DAO-->>Controller: Integer value

    Controller->>DAO: getRecentSummaries(userId)
    DAO->>DB: SELECT content, summary ... ORDER BY created_at DESC LIMIT 5
    DB-->>DAO: List of Map records
    DAO-->>Controller: List~Map~
    
    Controller->>Controller: Set attributes (summaryCount, mcqCount, flashcardCount, recentSummaries)
    Controller->>View: RequestDispatcher.forward(request, response)
    View-->>Client: Rendered Pastel Dashboard UI
```

---

### 3.3 AI Summary Generation Flow (With File Upload Integration)
Demonstrates multi-format file extraction, external API invoking, and persistent payload logging.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web Browser
    participant Controller as SummaryServlet
    participant Extractor as FileExtractor
    participant AI as GeminiAPI
    participant LogDAO as AILogDAO
    participant DAO as StudyDAO
    participant View as summary.jsp

    Client->>Controller: POST /summary (Multipart payload: fileUpload + content)
    
    %% File Extraction Logic
    opt Uploaded File exists (.txt or .pdf)
        Controller->>Extractor: extractText(filePart)
        Note over Extractor: Uses Apache PDFBox for .pdf<br/>Uses InputStreamReader for .txt
        Extractor-->>Controller: Extracted raw textual string
        Controller->>Controller: Override input content variable
    end
    
    %% Generative Process
    Controller->>AI: generateContent("Summarize the following notes...")
    AI-->>Controller: AI generated Markdown Summary String
    
    %% Parallel Database Logging
    par Log Payload for Traceability
        Controller->>LogDAO: logRequest("Summary", promptText, responseText)
    and Store User History
        Controller->>DAO: saveSummary(userId, originalContent, aiResponse)
    end
    
    Controller->>Controller: Set request attributes (summary, originalContent)
    Controller->>View: Forward
    View-->>Client: Dynamic Results Presentation
```

---

### 3.4 AI MCQ Generation Flow
Demonstrates JSON structured responses mapped into domain POJOs.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web Browser
    participant Controller as MCQServlet
    participant Extractor as FileExtractor
    participant AI as GeminiAPI
    participant LogDAO as AILogDAO
    participant DAO as StudyDAO
    participant View as mcq.jsp

    Client->>Controller: POST /mcq (Multipart payload)
    
    opt Uploaded File present
        Controller->>Extractor: extractText(filePart)
        Extractor-->>Controller: Extracted text
    end
    
    Note over Controller, AI: Appends prompt constraint: "Return EXACTLY a valid JSON array..."
    Controller->>AI: generateContent(Prompt)
    AI-->>Controller: JSON String representation
    
    Controller->>LogDAO: logRequest("MCQ", promptText, jsonString)
    
    Note over Controller: Parse string using Gson JsonParser<br/>Iterates and constructs List~MCQ~ POJOs
    
    loop Each Question Object
        Controller->>DAO: saveMCQ(MCQ Entity)
    end
    
    Controller->>Controller: request.setAttribute("mcqs", List~MCQ~)
    Controller->>View: Forward
    View-->>Client: Render fully styled 5-question test layout
```

---

### 3.5 AI Flashcard Generation Flow
Demonstrates extracting Q&A structures mapped to reactive CSS components.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web Browser
    participant Controller as FlashcardServlet
    participant Extractor as FileExtractor
    participant AI as GeminiAPI
    participant LogDAO as AILogDAO
    participant DAO as StudyDAO
    participant View as flashcards.jsp

    Client->>Controller: POST /flashcards (Multipart payload)
    
    opt Uploaded File present
        Controller->>Extractor: extractText(filePart)
        Extractor-->>Controller: Extracted text
    end
    
    Note over Controller, AI: Appends prompt constraint: "Return JSON array of question/answer pairs"
    Controller->>AI: generateContent(Prompt)
    AI-->>Controller: JSON String
    
    Controller->>LogDAO: logRequest("Flashcards", promptText, jsonString)
    
    Note over Controller: Parses JSON Array into List~Flashcard~
    
    loop Each Flashcard Record
        Controller->>DAO: saveFlashcard(Flashcard Entity)
    end
    
    Controller->>Controller: request.setAttribute("flashcards", List~Flashcard~)
    Controller->>View: Forward
    View-->>Client: Render 3D flip-card components
```

---

### 3.6 User Profile Updating & Avatar Upload Flow
Traces uploading multipart image assets natively to the server context directory.

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web Browser
    participant Controller as ProfileServlet
    participant Storage as FileSystem (Server /uploads)
    participant DAO as UserDAO
    participant DB as MySQL Database
    participant Session as HTTP Session
    participant View as profile.jsp

    Client->>Controller: POST /profile (name, bio, profileImage Part)
    
    opt Image Part exists and size > 0
        Note over Controller: Generate unique identifier: userId_timestamp_fileName
        Controller->>Storage: filePart.write(uploadPath + uniqueFileName)
        Storage-->>Controller: Saved locally
        Controller->>Controller: Update User Object profileImage field
    end
    
    Controller->>DAO: updateProfile(User)
    DAO->>DB: UPDATE users SET name=?, profile_image=?, bio=? WHERE id=?
    DB-->>DAO: Success
    DAO-->>Controller: true
    
    Controller->>Session: setAttribute("user", UpdatedUser)
    Controller->>Controller: setAttribute("successMessage", "Profile updated successfully!")
    Controller->>View: Forward Request
    View-->>Client: Render refreshed avatar and input states
```
