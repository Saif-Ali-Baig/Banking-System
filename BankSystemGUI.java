import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class BankSystemGUI extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:bank.db";
    private JTextField nameField, emailField, userIdField, balanceField, accountIdField, amountField;
    private JTextArea outputArea;

    public BankSystemGUI() {
        initializeDatabase();
        setTitle("Simple Bank System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("User ID (for account):"));
        userIdField = new JTextField();
        inputPanel.add(userIdField);

        inputPanel.add(new JLabel("Initial Balance:"));
        balanceField = new JTextField();
        inputPanel.add(balanceField);

        inputPanel.add(new JLabel("Account ID:"));
        accountIdField = new JTextField();
        inputPanel.add(accountIdField);

        inputPanel.add(new JLabel("Amount (deposit/withdraw):"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        add(inputPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addUserBtn = new JButton("Add User");
        JButton createAccountBtn = new JButton("Create Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton viewAccountsBtn = new JButton("View Accounts");

        buttonPanel.add(addUserBtn);
        buttonPanel.add(createAccountBtn);
        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(viewAccountsBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // Output Area
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Action Listeners
        addUserBtn.addActionListener(e -> addUser());
        createAccountBtn.addActionListener(e -> createAccount());
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        viewAccountsBtn.addActionListener(e -> viewAccounts());

        setVisible(true);
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (account_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, balance REAL, FOREIGN KEY(user_id) REFERENCES users(id))");
        } catch (SQLException e) {
            outputArea.append("Database error: " + e.getMessage() + "\n");
        }
    }

    private void addUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        if (name.isEmpty() || email.isEmpty()) {
            outputArea.append("Name and email cannot be empty.\n");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            outputArea.append("User added successfully.\n");
            nameField.setText("");
            emailField.setText("");
        } catch (SQLException e) {
            outputArea.append("Error adding user: " + e.getMessage() + "\n");
        }
    }

    private void createAccount() {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            double balance = Double.parseDouble(balanceField.getText());

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO accounts (user_id, balance) VALUES (?, ?)")) {
                pstmt.setInt(1, userId);
                pstmt.setDouble(2, balance);
                pstmt.executeUpdate();
                outputArea.append("Account created successfully.\n");
                userIdField.setText("");
                balanceField.setText("");
            } catch (SQLException e) {
                outputArea.append("Error creating account: " + e.getMessage() + "\n");
            }
        } catch (NumberFormatException e) {
            outputArea.append("Invalid user ID or balance.\n");
        }
    }

    private void deposit() {
        try {
            int accountId = Integer.parseInt(accountIdField.getText());
            double amount = Double.parseDouble(amountField.getText());

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_id = ?")) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, accountId);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    outputArea.append("Deposit successful.\n");
                } else {
                    outputArea.append("Account not found.\n");
                }
                accountIdField.setText("");
                amountField.setText("");
            } catch (SQLException e) {
                outputArea.append("Error depositing: " + e.getMessage() + "\n");
            }
        } catch (NumberFormatException e) {
            outputArea.append("Invalid account ID or amount.\n");
        }
    }

    private void withdraw() {
        try {
            int accountId = Integer.parseInt(accountIdField.getText());
            double amount = Double.parseDouble(amountField.getText());

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
                        outputArea.append("Withdrawal successful.\n");
                    } else {
                        outputArea.append("Insufficient balance.\n");
                    }
                } else {
                    outputArea.append("Account not found.\n");
                }
                accountIdField.setText("");
                amountField.setText("");
            } catch (SQLException e) {
                outputArea.append("Error withdrawing: " + e.getMessage() + "\n");
            }
        } catch (NumberFormatException e) {
            outputArea.append("Invalid account ID or amount.\n");
        }
    }

    private void viewAccounts() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT u.id, u.name, u.email, a.account_id, a.balance " +
                     "FROM users u LEFT JOIN accounts a ON u.id = a.user_id")) {
            outputArea.append("\nAccounts:\n");
            while (rs.next()) {
                outputArea.append(String.format("User ID: %d, Name: %s, Email: %s, Account ID: %s, Balance: %.2f%n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("account_id") != null ? rs.getString("account_id") : "None",
                        rs.getDouble("balance")));
            }
        } catch (SQLException e) {
            outputArea.append("Error viewing accounts: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankSystemGUI());
    }
}