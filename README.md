# 💰 Simple Bank System

This is a **console-based and a GUI banking system** developed in Java using **SQLite** as the database. It allows basic banking operations like user registration, account creation, deposits, withdrawals, and viewing account details.

> ✅ I created this as a **learning project** to practice Java programming and get hands-on experience with **JDBC (Java Database Connectivity)** and **SQLite**.  
> It’s a great starting point for anyone learning how to integrate a database into a Java application.

---

## 📋 Features

- Add new users with name and email.
- Create bank accounts for users.
- Deposit and withdraw money from accounts.
- View all users and their associated accounts and balances.
- Uses SQLite as a lightweight local database.

---

## 🧰 Technologies Used

- Java (JDK 8 or above)
- JDBC (Java Database Connectivity)
- SQLite (file-based database)

---

## 🚀 Getting Started

### 1. Clone this Repository
After cloning, go to the main folder. 

### 2. Compile and Run
Make sure you have Java installed and added to your system PATH:
For console based:
```bash
javac BankSystemConsole.java
java BankSystemConsole
```
For GUI based:
```bash
javac BankSystemGUI.java
java BankSystemGUI
```

---

## 🛠 How It Works

When you run the program:

1. It connects to a local SQLite database file (`bank.db`).
2. On first run, it creates two tables if they don’t already exist:
   - `users`: Stores user names and email addresses.
   - `accounts`: Stores account balances and links each account to a user.
3. The console displays a simple menu with the following options:
   - **Add User**: Register a new user with name and email.
   - **Create Account**: Open a bank account for an existing user with an initial deposit.
   - **Deposit**: Add money to an account.
   - **Withdraw**: Subtract money from an account (with balance check).
   - **View Accounts**: Display all users with their account details.
   - **Exit**: Closes the program.

All operations use prepared SQL statements for safe and efficient data handling.

---

## 📸 Sample Output
Simple Bank System (Console)

1. Add User
2. Create Account
3. Deposit
4. Withdraw
5. View Accounts
6. Exit
```
Choose option: 1
Enter name: Alice
Enter email: alice@example.com
User added successfully.
```
---

## 📚 What I Learned

- 📌 How to connect Java applications to a database using **JDBC**.
- 🗃 How to create and structure relational tables using **SQLite**.
- 🧱 CRUD operations in Java (Create, Read, Update, Delete).
- 🧼 Using `PreparedStatement` to avoid SQL injection and improve performance.
- ⌨ Handling user input via `Scanner` and processing it safely.
- 🛠 Structuring small Java projects with separation of logic.

---

## ✨ Future Improvements

Here are a few ideas I plan to explore in future versions:

- 🔐 Add login or authentication for users.
- 📈 Maintain transaction history (deposits, withdrawals).
- 💡 Better error handling and validation (e.g. email format, user/account existence).
- 📊 Generate reports for users/accounts in CSV or PDF format.
- 🌐 Possibly move to a server-based architecture using Spring Boot.

---

## 🙌 Acknowledgments

This project was built as a **personal learning project** to improve my skills in Java and databases.  
Thanks to the open-source community and online tutorials that helped me along the way.

Feel free to fork, contribute, or use this as inspiration for your own learning!
