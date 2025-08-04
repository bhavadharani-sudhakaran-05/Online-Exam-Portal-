import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();

        // Read JSON input from frontend
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse JSON and extract loginId and password
        JSONObject json = new JSONObject(sb.toString());
        String loginId = json.getString("loginId");
        String password = json.getString("password");

        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM logins WHERE login_id = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, loginId);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Credentials are valid
                out.print("{ \"status\": \"success\", \"message\": \"Login successful\" }");
            } else {
                // Credentials are invalid
                res.setStatus(401);
                out.print("{ \"status\": \"error\", \"message\": \"Invalid login ID or password\" }");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            out.print("{ \"status\": \"error\", \"message\": \"Server error\" }");
        }
    }

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/onlineexam";
        String user = "root";
        String pass = "Bhuvana@4000";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }
}