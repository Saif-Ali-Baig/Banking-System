import java.sql.*;
import java.util.Scanner;

public class BankSystemConsole {
    private static final String DB_URL = "jdbc:sqlite:bank.db";

    public static void main(String[] args) {
        initializeDatabase();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Simple Bank System (Console)");
        while (running) {
            System.out.println("\n1. Add User\n2. Create Account\n3. Deposit\n4. Withdraw\n5. View Accounts\n6. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addUser(scanner);
                    break;
                case 2:
                    createAccount(scanner);
                    break;
                case 3:
                    deposit(scanner);
                    break;
                case 4:
                    withdraw(scanner);
                    break;
                case 5:
                    viewAccounts();
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT NOT NULL)");
            // Create accounts table
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (account_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, balance REAL, FOREIGN KEY(user_id) REFERENCES users(id))");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void addUser(Scanner scanner) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    private static void createAccount(Scanner scanner) {
        System.out.print("Enter user ID: ");
        int userId = scanner.nextInt();
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO accounts (user_id, balance) VALUES (?, ?)")) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, balance);
            pstmt.executeUpdate();
            System.out.println("Account created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private static void deposit(Scanner scanner) {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_id = ?")) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error depositing: " + e.getMessage());
        }
    }

    private static void withdraw(Scanner scanner) {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement selectStmt = conn.prepareStatement("SELECT balance FROM accounts WHERE account_id = ?");
            selectStmt.setInt(1, accountId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_id = ?");
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, accountId);
                    updateStmt.executeUpdate();
                    System.out.println("Withdrawal successful.");
                } else {
                    System.out.println("Insufficient balance.");
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error withdrawing: " + e.getMessage());
        }
    }

    private static void viewAccounts() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT u.id, u.name, u.email, a.account_id, a.balance " +
                     "FROM users u LEFT JOIN accounts a ON u.id = a.user_id")) {
            System.out.println("\nAccounts:");
            while (rs.next()) {
                System.out.printf("User ID: %d, Name: %s, Email: %s, Account ID: %s, Balance: %.2f%n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("account_id") != null ? rs.getString("account_id") : "None",
                        rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing accounts: " + e.getMessage());
        }
    }
}