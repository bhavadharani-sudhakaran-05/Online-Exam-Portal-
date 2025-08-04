public import java.io.*;
import java.sql.*;
import java.util.Random;
import org.apache.poi.ss.usermodel.*; // For Sheet, Row, Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // For XSSFWorkbook (Excel files .xlsx)
public class StudentExcelImporter {
    // Method to read Excel and store student details into DB
    public void importFromExcel(String excelPath) {
        // Open Excel file and establish DB connection using try-with-resources
        try (
            FileInputStream fis = new FileInputStream(new File(excelPath));
            XSSFWorkbook workbook = new XSSFWorkbook(fis); // Correct constructor for .xlsx files
            Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/Onlineexam", "root", "Bhuvana@4000"
);
 // DB connection
        ) {
            // Read the first sheet from the Excel file
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                // Reading student details from Excel
                String name = row.getCell(1).getStringCellValue();
String dept = row.getCell(2).getStringCellValue();
String email = row.getCell(6).getStringCellValue();
String phone = row.getCell(7).getStringCellValue();


                // Insert student data into students table
                String insertStudent = "INSERT INTO students (name, email, phone, department) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, name);
                pst.setString(2, email);
                pst.setString(3, phone);
                pst.setString(4, dept);
                pst.executeUpdate();

                // Get generated student ID
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int studentId = rs.getInt(1);  // Get student ID from generated keys
                    String loginId = "STD" + String.format("%03d", studentId); // Example: STD001, STD002, etc.
                    String password = generateRandomPassword(8); // Generate 8-character random password

                    // Insert login credentials into logins table
                    String insertLogin = "INSERT INTO logins (login_id, student_id, password) VALUES (?, ?, ?)";
                    PreparedStatement pstLogin = conn.prepareStatement(insertLogin);
                    pstLogin.setString(1, loginId);
                    pstLogin.setInt(2, studentId);
                    pstLogin.setString(3, password);
                    pstLogin.executeUpdate();

                    System.out.println("Login generated for " + name + " ID: " + loginId + ", Password: " + password);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to generate random password of a given length
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Replace with the actual path to your Excel file
        String excelPath = "Stud.xlsx"; // Update the path
        new StudentExcelImporter().importFromExcel(excelPath);
    }
}

//java -cp "C:\Users\Bhuvanashri SK\Desktop\Online exam\libs\*;." StudentExcelImporter {
    
}
