# SoftwareTesting-JUnit-BugHunt
### Group 07: Sam Hersick and Mushtariy Ishmukhamedova  

## 📘 Overview  
This project was developed for **CS483 (Object-Oriented Programming & Design)** Assignment 3A–3B: *“Bug Hunt via JUnit.”*  
The goal is to design a small-but-nontrivial Java program with multiple classes and realistic seeded bugs, then later identify and fix them using JUnit tests.

---

## 🧩 Project Description  
Our program simulates a **Student Check-In System for Student-Athlete Study Hours**, written in **Java 21**.  
It includes several interacting classes, clear logic, and intentional subtle bugs across different categories (boundary, state, nullability, floating-point, etc.).  

---
## Project Details
### Usage
Student-Athletes are required to spend atleast **8 hours p/week** in the study. They check-in/check-out for each session that they spend in the study. Athletes are allowed to **bank hours**, meaning they spend **>= 8 hours** in the study in 1 week. Athletes may only spend up to **4 hours per day** in the study, all additional hours automatically are added to the banked hours. For the scope of this project, we will be able to calculate a student's **completed weekly required hours** and the **banked hours** the student generated for **1 week at a time**. Administrators will also be able to look up a deadline, and see **who has not completed their required hours.**

### Requirements
- Students must attend study **8 hours p/week**
- All hours **over** 8 p/week are added to **banked hours**
- All hours exceeding **4 hours in one day**, automatically contribute to banked hours (not current week hours)
- Student can calculate **hours completed** and **banked hours** given a deadline (only using days in week up to deadline)
- Admin's can **add a study session** to a student (by their valid id number)
- Admin's can view all students who **did not meet weekly requirement** (by deadline)
- A session cannot **span multiple days**.
- Students are allowed **multiple sessions per day**
- Deadlines are traditionally every **Thursday at midnight** (1-week example, deadline = Thursday, **Wednesday morning** of previous week - **Thursday night** of current week)

### Assumptions
- Student starts week with **no banked hours**
- Only checking student hours for **1 week at a time.** We aren't considering the whole lifespan of user sessions, Just week up to deadline.
- Admin has **valid metrics** including: required hours, maximum day hours, student id's, and list of students
- Admin may or may not create valid sessions (should throw error if invalid)
- Student session's **will not be overlapping** (illogical)

---

### Testing
### Test Cases (Recommendation)
- Ensure admin can add session using **student's ID** number (should throw exception on failure)
- Ensure only valid sessions are created (should exception on failure)
- Ensure **Banked** hours are calculated correctly
- Ensure **Weekly** hours are calculated correctly
- Ensure list of **students not meeting requirements** are calculated correctly

### Additional Consideration
In the real-world, students will walk up to the study desk, and an administrator will sign them in/out in real-time. For the scope of this project, administrators will just create sessions and add them to a user by id. This will ease in the testing process by allowing users to easily test different datetimes for session data instead of relying on real-time data (such as LocalDateTime.now()).

---

## ⚙️ How to Run  
1. Clone the repository:
   ```bash
   git clone https://github.com/sghersick1/SoftwareTesting-JUnit-BugHunt.git
   cd SoftwareTesting-JUnit-BugHunt
