package src.main.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        if (action == null || username == null || password == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            out.write("Missing input");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                if ("create".equals(action)) {
                    createAccount(conn, username.trim(), password, out);
                } else if ("login".equals(action)) {
                    loginAccount(conn, username.trim(), password, out);
                } else {
                    out.write("Invalid action");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.write("Server error: " + e.getMessage());
        }
    }

    private void createAccount(Connection conn, String username, String password, PrintWriter out)
            throws Exception {

        String checkSql = "SELECT username FROM accounts WHERE username = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                out.write("Username already exists");
                return;
            }
        }

        String insertSql = "INSERT INTO accounts (username, password) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            out.write("success");
        }
    }

    private void loginAccount(Connection conn, String username, String password, PrintWriter out)
            throws Exception {

        String sql = "SELECT * FROM accounts WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                out.write("success");
            } else {
                out.write("Invalid username or password");
            }
        }
    }
}