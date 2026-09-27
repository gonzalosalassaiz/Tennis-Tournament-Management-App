# 🎾 Tennis Tournament Management App

> Java Application for Tennis Tournament Management and Organization

This repository contains **Tennis Tournament Management App**, a Java-based application developed to support the organization and management of tennis tournaments.

The project provides functionality for player registration, tournament creation and management, match scheduling, result tracking, rankings and tournament statistics through a graphical desktop interface.

The development process followed a structured **Software Development Lifecycle (SDLC)**, including project planning, effort estimation, progress monitoring, implementation, testing and technical documentation.

---

## 📌 Overview

Managing a tennis tournament requires coordinating players, registrations, matches, rounds, results and rankings while keeping tournament information consistent and accessible.

The main objective of this project is to provide a centralized application that supports the main activities involved in tournament management.

The application follows a complete workflow:

```text
User
  │
  ▼
Authentication
  │
  ▼
Tournament Management
  │
  ├──────────────► Player Registration
  │
  ├──────────────► Match Scheduling
  │
  ├──────────────► Results Management
  │
  └──────────────► Rankings & Statistics
                         │
                         ▼
                    MySQL Database
```

---

## 🎯 Objectives

The main objectives of the project are:

* Develop a desktop application for tennis tournament management.
* Provide user registration and authentication functionality.
* Create and configure tennis tournaments.
* Manage player registrations and tournament participation.
* Organize matches and tournament rounds.
* Record and update match results.
* Generate player rankings and tournament statistics.
* Persist application data using a relational database.
* Apply software engineering principles throughout the development process.
* Follow a structured Software Development Lifecycle.
* Provide technical documentation to facilitate maintenance and future development.

---

## 🎾 Tournament Management

The application provides functionality for managing the main entities involved in a tennis tournament.

### Tournament Creation

Users can create tournaments and configure information such as:

* 🏆 Tournament name
* 📅 Tournament year
* ⏳ Registration deadline
* 👥 Number of players
* 🎯 Tournament type
* 🔄 Tournament rounds
* 🎾 Match information

### Player Management

The system supports player-related operations including:

* 👤 User registration
* 📝 Tournament registration
* 🎾 Player participation
* 📊 Player statistics
* 🏅 Ranking information

### Match Management

Tournament matches can be managed throughout the competition:

* Schedule and organize matches.
* Track tournament rounds.
* Record match results.
* Update player progression.
* Maintain tournament statistics.

---

## 🖥️ Application Interface

The application uses a graphical desktop interface built with **Java Swing**.

The interface is organized into multiple views and panels covering the main application workflows:

```text
Login / Sign Up
       │
       ▼
     Home
       │
       ├──────────────► Create Tournament
       │
       ├──────────────► Manage Tournaments
       │
       ├──────────────► Register Tournament
       │
       ├──────────────► Match Results
       │
       ├──────────────► Ranking
       │
       ├──────────────► Tournament Statistics
       │
       └──────────────► User Profile
```

The interface also includes application-specific icons, fonts, colors and reusable utility components.

---

## 🗄️ Data Management

The application uses a relational database to persist tournament and user information.

A dedicated database management layer is responsible for handling data access and application operations.

### Main Data Entities

The domain model includes:

* **User** — User and player information
* **Tournament** — Tournament configuration and state
* **Match** — Match information and results
* **PlayerTournamentStats** — Player statistics within tournaments
* **TournamentType** — Tournament classification

The application uses **MySQL Connector/J** to communicate with the database.

---

## 🧪 Testing

The project includes automated tests using **JUnit 5**.

Testing is integrated into the Maven project structure:

```text
src/
├── main/
│   ├── java/
│   └── resources/
│
└── test/
    └── java/
```

Tests provide a foundation for validating application behavior and supporting future maintenance and refactoring.

---

## 🛠️ Technologies

<p align="left">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java%20Swing-5382A1?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/JUnit%205-25A162?style=for-the-badge&logo=junit5&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/JavaFX-2C2255?style=for-the-badge"/>
</p>

### Main Technologies

* **Java 17** — Core application development
* **Java Swing** — Desktop graphical user interface
* **Maven** — Dependency management and project build
* **MySQL** — Relational data persistence
* **MySQL Connector/J** — Database connectivity
* **JUnit 5** — Automated testing
* **Spring Boot** — Supporting web and mail components
* **JavaFX** — Included application dependency
* **Git** — Version control and collaborative development

---

## 📁 Repository Structure

```text
Tennis-Tournament-Management-App/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tenis_upm/
│   │   │       └── grupo11/
│   │   │           ├── App.java
│   │   │           ├── data/
│   │   │           │   ├── Match.java
│   │   │           │   ├── PlayerTournamentStats.java
│   │   │           │   ├── Tournament.java
│   │   │           │   ├── TournamentType.java
│   │   │           │   └── User.java
│   │   │           ├── functionality/
│   │   │           │   ├── DBManager.java
│   │   │           │   └── Manager.java
│   │   │           └── view/
│   │   │               ├── MainFrame.java
│   │   │               ├── insidepanels/
│   │   │               ├── outsidepanels/
│   │   │               ├── interfaces/
│   │   │               └── utils/
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── icons/
│   │
│   └── test/
│       └── java/
│
├── pom.xml
├── logs/
└── README.md
```

> **Note:** The repository currently contains generated build artifacts and application logs. These files should ideally be excluded through `.gitignore` in a future cleanup.

---

## 🚀 Getting Started

### Prerequisites

Make sure you have installed:

* Java Development Kit (JDK) 17
* Maven
* MySQL
* Git

Clone the repository:

```bash
git clone https://github.com/gonzalosalassaiz/Tennis-Tournament-Management-App.git
```

Navigate to the project:

```bash
cd Tennis-Tournament-Management-App
```

Build the project with Maven:

```bash
mvn clean package
```

Run the application from the generated build or directly from your IDE using:

```text
tenis_upm.grupo11.App
```

> **Important:** Database and email configuration should be provided through secure external configuration before running the application. Credentials should not be committed to the repository.

---

## 🔍 Key Areas Explored

This project combines several areas that are relevant to Software Engineering and application development:

```text
Requirements & Planning
        ↓
Software Architecture
        ↓
Object-Oriented Design
        ↓
Graphical User Interface
        ↓
Database Management
        ↓
Tournament Logic
        ↓
Testing
        ↓
Technical Documentation
```

The project provides a practical example of applying software engineering practices to a domain-specific management application.

---

## 📚 Academic Context

This project was developed as an academic Software Engineering project focused on the complete development lifecycle of a desktop application.

The development process included:

* Requirements analysis
* Effort estimation
* Project planning
* Progress monitoring
* Software implementation
* Testing
* Technical documentation

**Author:** Gonzalo Salas Saiz  
**Degree:** Computer Engineering  
**Project:** Tennis Tournament Management App

---

## 🔮 Future Improvements

Potential future developments include:

* Refactoring the current package structure into a more professional architecture.
* Removing generated `target/` and log files from version control.
* Adding a comprehensive `.gitignore`.
* Externalizing database and email credentials using environment variables.
* Improving database connection management.
* Adding stronger validation and error handling.
* Expanding automated unit and integration test coverage.
* Introducing a clearer service and repository architecture.
* Improving tournament scheduling and bracket management.
* Adding richer player and tournament analytics.
* Adding CI/CD with automated builds and tests.
* Improving the graphical user interface and responsiveness.
* Migrating toward a more modern web-based interface in a future version.

---

## ⭐ About the Project

Tennis Tournament Management App is a project that combines **Software Engineering, Object-Oriented Programming, desktop application development, database management and software testing** in a single application.

It represents a practical project focused on building a complete software solution, from planning and architecture through implementation, testing and technical documentation.

---

<p align="center">
  <i>From tournament organization to match results — bringing tennis management into one application.</i>
</p>
