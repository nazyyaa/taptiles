# Taptiles Game

**Taptiles** is a logic-based tile-matching puzzle game implemented in Java as a full-stack application. It supports both a web and console interface and includes backend infrastructure for user accounts, ratings, scores, and comments.

🎬 Watch Gameplay on [YouTube](https://youtu.be/Z9ywDNWspI4)

This project was created as part of a university assignment and demonstrates the integration of core game logic with a modern Spring Boot web backend.

---

## 🧠 Project Overview

- The game logic revolves around a 2D board of tiles that can be removed in pairs if not blocked.
- The game ends when all tiles are removed or no moves remain.
- Designed to track user progress, ratings, and feedback persistently.

---

## 💡 Key Systems & Technologies

### 🎮 Game Logic (Core Layer)
- Custom tile-matching logic in `Board.java` and `Tile.java`
- Object-oriented structure with tile states, board state, and match detection
- Encapsulated game rules and validation logic

### 🌐 Web Application (Spring Boot)
- Web interface using **Spring MVC** and **Thymeleaf**
- REST API for scores, comments, ratings, and users
- Server entry point: `GameStudioServer.java`
- Main controller: `TaptilesController.java`

### 📊 Database & Persistence
- Entities: `User`, `Score`, `Rating`, `Comment`
- JPA/Hibernate for ORM
- Uses an H2 database (in-memory or persistent)
- Repositories for each entity (Spring Data JPA)

### 🔐 Authentication & Sessions
- Users can register and log in
- Session-based user management using `@SessionAttributes`
- Logged-in users can submit ratings, comments, and scores

### 📤 REST API
- Exposes endpoints for:
  - Getting and submitting **scores**
  - Managing **user accounts**
  - Posting and retrieving **comments**
  - Submitting and viewing **ratings**
- JSON-based request/response handling

### 🖥️ Console Interface
- Fully playable from terminal via `ConsoleUI.java`
- Game logic interacts directly with console inputs
- Prints board and accepts tile pair selection

### ⚠️ Error Handling
- Global exception handling via `GlobalExceptionHandler.java`
- User-friendly messages for invalid moves, input, or server errors

---

## 📎 Technologies Used

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- H2 Database
- Thymeleaf (HTML templating)
- Maven

---

## 🧩 Purpose

This project was developed as part of a software engineering course at the **Technical University of Košice (TUKE)**. It demonstrates proficiency in:

- Full-stack Java development
- Clean MVC architecture
- Backend API design and integration
- Game development and UI design (web and console)

---

## 🎓 Author

- Created by Nazar Andriichuk
- Faculty of Electrical Engineering and Informatics  
- Technical University of Košice  
