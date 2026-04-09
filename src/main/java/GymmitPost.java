package src.main.webapp;

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

@WebServlet("/gymmit")
public class GymmitPost extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gymmit_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT username, topic, content FROM posts ORDER BY id DESC";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                StringBuilder json = new StringBuilder();
                json.append("[");

                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        json.append(",");
                    }

                    json.append("{");
                    json.append("\"username\":\"").append(escapeJson(rs.getString("username"))).append("\",");
                    json.append("\"topic\":\"").append(escapeJson(rs.getString("topic"))).append("\",");
                    json.append("\"content\":\"").append(escapeJson(rs.getString("content"))).append("\"");
                    json.append("}");

                    first = false;
                }

                json.append("]");

                response.getWriter().write(json.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("[]");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        String username = request.getParameter("username");
        String topic = request.getParameter("topic");
        String content = request.getParameter("content");

        if (username == null || topic == null || content == null ||
                username.trim().isEmpty() || topic.trim().isEmpty() || content.trim().isEmpty()) {
            response.getWriter().write("Missing input");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO posts (username, topic, content) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username.trim());
                stmt.setString(2, topic.trim());
                stmt.setString(3, content.trim());
                stmt.executeUpdate();

                response.getWriter().write("success");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Server error");
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t");
    }
}