package services;

import java.sql.*;
import java.util.Map;
import repository.UserRepository;

public class AdminService {
    private UserRepository repo = new UserRepository();

    public boolean registerStaffWithProfile(String user, String pass, String role, Map<String, String> details) {
        if (user.isEmpty() || pass.isEmpty() || details.get("name").isEmpty()) return false;

        Connection conn = null;
        try {
            conn = util.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, user);
            ps1.setString(2, pass);
            ps1.setString(3, role);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                double sal = Double.parseDouble(details.get("salary"));
                
                if (role.equals("Doctor")) {
                    // Corrected: 4 placeholders for 4 values
                    String sql = "INSERT INTO doctors (doctor_id, full_name, specialty, salary) VALUES (?,?,?,?)";
                    PreparedStatement ps2 = conn.prepareStatement(sql);
                    ps2.setInt(1, id);
                    ps2.setString(2, details.get("name"));
                    ps2.setString(3, details.get("specialty"));
                    ps2.setDouble(4, sal);  // Changed from index 5 to 4
                    ps2.executeUpdate();
                } else {
                    // Receptionist - removed phone column
                    String sql = "INSERT INTO receptionists (receptionist_id, full_name, shift_type, salary) VALUES (?,?,?,?)";
                    PreparedStatement ps2 = conn.prepareStatement(sql);
                    ps2.setInt(1, id);
                    ps2.setString(2, details.get("name"));
                    ps2.setString(3, details.get("shift"));  // shift_type at position 3
                    ps2.setDouble(4, sal);  // salary at position 4
                    ps2.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStaff(String id, String name, String salary, String extra, String role) {
        try {
            double sal = Double.parseDouble(salary);
            return role.equals("Doctor") ? 
                   repo.updateDoctorProfile(id, name, sal, extra) : 
                   repo.updateReceptionistProfile(id, name, sal, extra);
        } catch (Exception e) { 
            e.printStackTrace();
            return false; 
        }
    }
}