# 🏥 Hospital Management System (HMS)
**Final Technical Project | SE324 Software Engineering (2026)**

## 📋 Project Overview
The **Hospital Management System (HMS)** is a comprehensive desktop application designed to digitize clinical workflows and manage patient-doctor interactions. Built using the **MVC (Model-View-Controller)** architecture, it provides a secure, role-based environment for hospital staff to manage data efficiently.

---

## 🔑 Role-Based Access Control (RBAC)

### 👨‍⚕️ Doctor Module (Clinical Operations)
* **Live Patient Queue**: Fetches today's appointments in real-time, filtered by the logged-in doctor's ID.
* **Consultation Suite**: interface to record **Diagnoses** and **Prescriptions**.
* **Workflow Automation**: Automatically marks patients as "Completed" upon saving, removing them from the active queue.
* **Medical Records Search**: Access to historical patient data for informed clinical decision-making.

### 🏢 Receptionist Module (Front Desk)
* **Patient Onboarding**: Registration of new patients and profile management.
* **Scheduling**: Booking appointments and managing the hospital calendar.
* **Billing System**: Processing records and viewing financial history for completed consultations.

### ⚙️ Admin Module (System Management)
* **Staff Management**: Full CRUD (Create, Read, Update, Delete) operations for hospital personnel.
* **System Audit**: Ensuring role-specific access and data privacy across all modules.

---

## 🧪 Software Engineering & Testing Methodologies
This project implements several key software engineering concepts discussed throughout the term:

* **Data Flow Testing**: Validated the definition-use (def-use) paths of appointment objects as they transition from "Scheduled" to "Completed".
* **Software Complexity Management**: Logic is decoupled into **Repository Classes** to keep cyclomatic complexity low within the UI layer.
* **Predicate Use (P-use)**: implemented rigorous input validation (e.g., ensuring a patient is selected before a consultation begins).
* **Integration Testing**: Successfully identified and resolved a schema mismatch where clinical columns were missing in the database.

---

## 🚀 Technical Setup

### 1. Prerequisites
* **Java**: JDK 17 or higher.
* **Database**: MySQL Server 8.0+.
* **IDE**: VS Code, IntelliJ, or Eclipse.

### 2. Database Migration
Import the following schema into your MySQL instance to ensure the persistence layer is ready:
```sql
-- Core clinical schema update
ALTER TABLE appointments 
ADD COLUMN diagnosis TEXT, 
ADD COLUMN prescription TEXT;

### 3. Running the Application
1. **Clone** the repository.
2. **Configure** your database credentials in `src/util/DatabaseConnection.java`.
3. **Compile and run** `src/ui/LoginFrame.java`.

---

## 📂 Project Structure
```text
SE324_HMS/
├── src/
│   ├── model/       # Data entities (Doctor, Patient, Appointment)
│   ├── ui/          # Java Swing GUI components
│   ├── repository/  # DAO and SQL logic
│   └── util/        # DB Connection utilities
├── sql/             # Database scripts and migrations
└── README.md        # Project documentation



