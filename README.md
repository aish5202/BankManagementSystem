# 🏦 Bank Management System

A desktop-based Bank Management System developed using Java (Swing) and JDBC.  
This application allows banks to manage customers, accounts, transactions, and loans efficiently.

---

## 📖 Project Overview

The Bank Management System simulates core banking operations such as:

- Customer Registration
- Account Creation
- Deposit & Withdrawal
- Loan Processing
- Transaction Reports
- Admin Settings

This project demonstrates:
- Object-Oriented Programming (OOP)
- GUI development using Java Swing
- Database connectivity using JDBC
- Structured multi-form application design

---

## 🛠️ Technologies Used

- Java
- Java Swing (GUI)
- JDBC
- Oracle Database
- NetBeans IDE

---

## 📂 Project Structure
BankManagementSystem
│
├── src/bankmanagement
│ ├── AccountForm.java
│ ├── CustomerForm.java
│ ├── Dashboard.java
│ ├── DBConnection.java
│ ├── LoanForm.java
│ ├── Login.java
│ ├── ReportsForm.java
│ ├── SettingsForm.java
│ └── TransactionForm.java
│
├── build.xml
├── manifest.mf
└── README.md

---

## 🚀 Features

✔ User Login System  
✔ Customer Management  
✔ Account Management  
✔ Deposit & Withdraw Transactions  
✔ Loan Management  
✔ Transaction Reports  
✔ Database Connectivity  

---

## 🔐 Database Configuration

1. Install Oracle Database.
2. Update database credentials inside `DBConnection.java`.

Example:

```java
Connection con = DriverManager.getConnection(
    "jdbc:oracle:thin:@localhost:1521:xe",
    "username",
    "password"
);

▶ How to Run

Clone the repository:

git clone https://github.com/aish5202/BankManagementSystem.git

Open the project in NetBeans.

Configure database credentials.

Build and Run the project.

🎯 Learning Outcomes

Applied OOP principles

Designed GUI using Swing

Implemented CRUD operations

Integrated Oracle DB using JDBC

Structured multi-form Java application

📌 Future Enhancements

Role-based authentication

Export reports to PDF

Improved UI design

Migration to Spring Boot

👩‍💻 Author

Aiswarya Valsalan
GitHub: https://github.com/aish5202