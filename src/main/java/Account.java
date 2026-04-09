package src.main.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/account")
public class Account extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gym_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (action == null || username == null || password == null
                || username.trim().isEmpty() || password.trim().isEmpty()) {
            out.println("Missing username or password.");
            return;
        }

        username = username.trim();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                if (action.equals("create")) {
                    createAccount(conn, username, password, out);
                } else if (action.equals("login")) {
                    loginAccount(conn, username, password, out);
                } else {
                    out.println("Invalid action.");
                }
            }

        } catch (ClassNotFoundException e) {
            out.println("MySQL JDBC driver not found.");
        } catch (SQLException e) {
            out.println("Database error: " + e.getMessage());
        }
    }

    private void createAccount(Connection conn, String username, String password, PrintWriter out)
            throws SQLException {

        String checkSql = "SELECT username FROM accounts WHERE username = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                out.println("Username already exists.");
                return;
            }
        }

        String insertSql = "INSERT INTO accounts (username, password) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            out.println("Account created successfully.");
        }
    }

    private void loginAccount(Connection conn, String username, String password, PrintWriter out)
            throws SQLException {

        String loginSql = "SELECT * FROM accounts WHERE username = ? AND password = ?";
        try (PreparedStatement loginStmt = conn.prepareStatement(loginSql)) {
            loginStmt.setString(1, username);
            loginStmt.setString(2, password);

            ResultSet rs = loginStmt.executeQuery();

            if (rs.next()) {
                out.println("Login successful.");
            } else {
                out.println("Invalid username or password.");
            }
        }
    }
}