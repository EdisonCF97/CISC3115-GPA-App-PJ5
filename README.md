# GPA Management System

> A Java app that manages student courses, calculates GPA, and saves data to a file.

---

## 📖 Project Background

This project was my assignment while studying CISC 3115 at Brooklyn College. It adds **file I/O**, **custom exceptions**, and **data persistence** to the basic GPA calculator.

---

## ✨ Features

- Add courses with credits and letter grades (A, B, C, D, F)
- List all courses with grade points
- Calculate GPA (handles empty course list gracefully)
- Save/load data from a text file
- Custom exceptions for file errors, invalid grades, and GPA errors

---

## 🛠️ Tech Stack

- Java
- File I/O (BufferedReader / PrintWriter)
- Custom Exceptions
- ArrayList
- OOP Principles

### Classes

| Class | Purpose |
| :--- | :--- |
| `GpaApp` | Main menu and user interaction |
| `Student` | Course list, GPA, save/load |
| `Course` | Credits + grade |
| `GradeUtils` | Grade to GPA conversion |
| `*Exception` | Custom error handling |

---

## 🚀 How to Run

### Requirements
- Java Development Kit (JDK) 11 or higher

### Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/EdisonCF97/CISC3115-GPA-App-PJ5.git
