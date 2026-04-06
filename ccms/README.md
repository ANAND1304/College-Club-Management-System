# 🎓 ClubHub — College Club Management System

A production-grade full-stack web application for managing college clubs, events, memberships, and announcements.

**Tech Stack:** Spring Boot 3 · Spring Security + JWT · JPA/Hibernate · MySQL · React 18 · Axios

---

## 📸 Features

### 👨‍🎓 Student
- Register & login with JWT authentication
- Browse and search all clubs by name/category
- Send join requests to clubs
- View upcoming & past events
- View club members and event details
- Manage personal profile

### 🛠️ Admin
- Full dashboard with system-wide statistics
- Create, update, and delete clubs
- Create and delete events
- Approve or reject membership requests
- Manage all users (enable/disable, promote roles)
- View all pending join requests

---

## 🏗️ Architecture

```
┌──────────────────┐    REST/JSON    ┌─────────────────────────┐
│  React Frontend  │◄───────────────►│   Spring Boot Backend   │
│  (Vercel)        │                 │   (Render.com)          │
└──────────────────┘                 └────────────┬────────────┘
                                                  │ JPA/Hibernate
                                                  ▼
                                        ┌──────────────────┐
                                        │  MySQL Database   │
                                        │  (PlanetScale /  │
                                        │   Railway / RDS) │
                                        └──────────────────┘
```

### Security Flow
```
Client ──POST /api/auth/login──► AuthController
                                       │ validates credentials
                                       ▼
                               AuthenticationManager
                                       │ success
                                       ▼
                                  JwtService.generateToken()
                                       │
                                  ◄────┘ returns JWT
Client stores JWT in localStorage
Client ──GET /api/clubs──► JwtAuthFilter extracts token
                                       │ validates signature + expiry
                                       ▼
                               SecurityContextHolder
                                       │
                               Controller / @PreAuthorize
```

---

## 📂 Project Structure

```
ccms/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/clubmanagement/
│       │   ├── CollegeClubManagementApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java       ← CORS, JWT filter chain
│       │   │   └── DataSeeder.java           ← Seeds default admin
│       │   ├── controller/
│       │   │   ├── AuthController.java       ← /api/auth/**
│       │   │   ├── ClubController.java       ← /api/clubs/**
│       │   │   ├── EventController.java      ← /api/events/**
│       │   │   ├── AdminController.java      ← /api/admin/**
│       │   │   ├── UserController.java       ← /api/users/me
│       │   │   └── AnnouncementController.java
│       │   ├── service/                      ← Business logic layer
│       │   ├── repository/                   ← JPA repositories
│       │   ├── model/                        ← JPA entities
│       │   ├── dto/                          ← Request/Response DTOs
│       │   ├── security/
│       │   │   ├── JwtService.java
│       │   │   ├── JwtAuthFilter.java
│       │   │   └── CustomUserDetailsService.java
│       │   └── exception/
│       │       ├── GlobalExceptionHandler.java
│       │       ├── ResourceNotFoundException.java
│       │       └── UnauthorizedException.java
│       └── resources/
│           ├── application.properties        ← Dev config
│           └── application-prod.properties   ← Prod config
│
└── frontend/
    ├── package.json
    ├── .env                                  ← Dev API URL
    ├── .env.production                       ← Prod API URL
    ├── vercel.json                           ← SPA routing
    └── src/
        ├── App.jsx                           ← Routes + guards
        ├── index.css                         ← Global dark theme
        ├── context/
        │   └── AuthContext.jsx               ← Auth state + JWT
        ├── services/
        │   └── api.js                        ← Axios + all API calls
        ├── components/
        │   └── layout/
        │       └── Navbar.jsx
        └── pages/
            ├── Dashboard.jsx
            ├── Profile.jsx
            ├── auth/
            │   ├── Login.jsx
            │   └── Register.jsx
            ├── clubs/
            │   ├── Clubs.jsx
            │   └── ClubDetail.jsx
            ├── events/
            │   └── Events.jsx
            └── admin/
                └── AdminPanel.jsx
```

---

## 🗄️ Database Schema

```sql
users         (id, name, email, password, role, department, phone, active, created_at)
clubs         (id, name, description, category, image_url, active, created_at, created_by)
memberships   (id, user_id, club_id, status, club_role, joined_at, requested_at)
events        (id, title, description, event_date, location, max_participants, active, club_id, created_by, created_at)
announcements (id, title, content, club_id, created_by, created_at)
```

---

## 📡 API Endpoints

### Auth (Public)
| Method | Endpoint              | Description        |
|--------|-----------------------|--------------------|
| POST   | /api/auth/register    | Register new user  |
| POST   | /api/auth/login       | Login → get JWT    |

### Clubs
| Method | Endpoint                   | Access        |
|--------|----------------------------|---------------|
| GET    | /api/clubs                 | Public        |
| GET    | /api/clubs/{id}            | Public        |
| GET    | /api/clubs/{id}/members    | Authenticated |
| POST   | /api/clubs                 | ADMIN         |
| PUT    | /api/clubs/{id}            | ADMIN/HEAD    |
| DELETE | /api/clubs/{id}            | ADMIN         |
| POST   | /api/clubs/{id}/join       | Authenticated |
| DELETE | /api/clubs/{id}/leave      | Authenticated |

### Events
| Method | Endpoint                   | Access        |
|--------|----------------------------|---------------|
| GET    | /api/events                | Public        |
| GET    | /api/events/{id}           | Public        |
| GET    | /api/events/club/{clubId}  | Public        |
| POST   | /api/events                | ADMIN/HEAD    |
| PUT    | /api/events/{id}           | ADMIN/HEAD    |
| DELETE | /api/events/{id}           | ADMIN         |

### Admin
| Method | Endpoint                              | Access |
|--------|---------------------------------------|--------|
| GET    | /api/admin/dashboard                  | ADMIN  |
| GET    | /api/admin/users                      | ADMIN  |
| GET    | /api/admin/memberships/pending        | ADMIN  |
| PUT    | /api/admin/memberships/{id}/approve   | ADMIN  |
| PUT    | /api/admin/memberships/{id}/reject    | ADMIN  |
| PUT    | /api/admin/users/{id}/toggle          | ADMIN  |
| PUT    | /api/admin/users/{id}/role            | ADMIN  |

---

## ⚙️ Local Setup

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.8+

### 1. Database
```sql
CREATE DATABASE club_management;
```
Spring Boot will auto-create tables on first run (`ddl-auto=update`).  
A default admin is seeded automatically: **admin@college.com / password**

### 2. Backend
```bash
cd backend

# Option A: set environment variables
export DB_URL="jdbc:mysql://localhost:3306/club_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437

# Option B: edit src/main/resources/application.properties directly

mvn clean install -DskipTests
mvn spring-boot:run

# API available at http://localhost:8080
```

### 3. Frontend
```bash
cd frontend
npm install
# .env already set to http://localhost:8080/api
npm start
# App available at http://localhost:3000
```

---

## ☁️ Deployment

### Backend → Render.com

1. Push the `backend/` folder to a GitHub repo
2. Render → New Web Service → connect repo
3. **Build command:** `mvn clean package -DskipTests`
4. **Start command:** `java -jar target/college-club-management-1.0.0.jar --spring.profiles.active=prod`
5. Set environment variables in Render dashboard:

| Key           | Value                                           |
|---------------|-------------------------------------------------|
| DB_URL        | jdbc:mysql://your-db-host/club_management?...   |
| DB_USERNAME   | your_db_user                                    |
| DB_PASSWORD   | your_db_password                                |
| JWT_SECRET    | (any 64-char hex string)                        |
| CORS_ORIGINS  | https://your-frontend.vercel.app                |

> **Free MySQL options:** PlanetScale (free tier), Railway ($5/mo), Aiven (free tier)

### Frontend → Vercel

```bash
cd frontend
# Update .env.production with your Render backend URL
npm run build
```

Or connect GitHub repo directly to Vercel:
1. Import project
2. Framework: Create React App
3. Set env var: `REACT_APP_API_URL=https://your-backend.onrender.com/api`
4. Deploy!

---

## 🧪 Test Credentials

| Role    | Email               | Password |
|---------|---------------------|----------|
| Admin   | admin@college.com   | password |
| Student | (register a new account) | — |

---

## 🔐 Security Notes

- Passwords are hashed with BCrypt (strength 10)
- JWT tokens expire in 24 hours (configurable via `JWT_EXPIRATION`)
- All sensitive APIs require `Authorization: Bearer <token>` header
- Role-based access enforced via `@PreAuthorize` on every protected endpoint
- CORS is configurable — only allow your frontend origin in production

---

## 🔮 Future Enhancements

- [ ] Email notifications for membership approval
- [ ] Club banner/image upload (AWS S3)
- [ ] Real-time notifications (WebSockets)
- [ ] Club analytics charts
- [ ] Docker + docker-compose setup
- [ ] Profile picture upload
- [ ] Event RSVP / attendance tracking
- [ ] Announcement system UI

---

## 👨‍💻 Author

Built as a showcase project demonstrating:
- **Spring Boot 3** layered architecture
- **JWT authentication** with Spring Security
- **JPA/Hibernate** with proper entity relationships
- **React 18** with Context API for state management
- **Production deployment** (Render + Vercel)

⭐ Star this repo if it helped you!
