package repository;

import java.sql.*;
import java.util.*;
import util.DatabaseConnection;
import model.*;

public class UserRepository {

    public User login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String role = rs.getString("role");
                return switch (role) {
                    case "Admin" -> new Admin(id, username, password);
                    case "Doctor" -> new Doctor(id, username, password);
                    case "Receptionist" -> new Receptionist(id, username, password);
                    default -> null;
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Map<String, Object> getExtendedStats() {
            Map<String, Object> stats = new HashMap<>();
            try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
                
                ResultSet rs = stmt.executeQuery("SELECT role, COUNT(*) FROM users GROUP BY role");
                while (rs.next()) { stats.put(rs.getString(1), rs.getInt(2)); }
                
                ResultSet rs2 = stmt.executeQuery(
                    "SELECT AVG(salary) FROM (SELECT salary FROM doctors UNION ALL SELECT salary FROM receptionists) as combined");
                if (rs2.next()) { stats.put("avgSalary", rs2.getDouble(1)); }
                
            } catch (SQLException e) { e.printStackTrace(); }
            return stats;
        }



        // Add this method to UserRepository.java if missing
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(userId));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }


    public List<String[]> getAllStaffFullProfiles() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT u.user_id, u.username, u.role, " +
                       "COALESCE(d.full_name, r.full_name) AS name, " +
                       "COALESCE(d.specialty, r.shift_type) AS extra, " +
                       "COALESCE(d.salary, r.salary) AS salary " +
                       "FROM users u " +
                       "LEFT JOIN doctors d ON u.user_id = d.doctor_id " +
                       "LEFT JOIN receptionists r ON u.user_id = r.receptionist_id " +
                       "WHERE u.role != 'Admin'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("user_id"), rs.getString("username"), rs.getString("role"),
                    rs.getString("name"), rs.getString("extra"), rs.getString("salary")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateDoctorProfile(String id, String name, double salary, String specialty) {
            String sql = "UPDATE doctors SET full_name = ?, salary = ?, specialty = ? WHERE doctor_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, salary);
                pstmt.setString(3, specialty);
                pstmt.setInt(4, Integer.parseInt(id));
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        }

        public boolean updateReceptionistProfile(String id, String name, double salary, String shift) {
            String sql = "UPDATE receptionists SET full_name = ?, salary = ?, shift_type = ? WHERE receptionist_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, salary);
                pstmt.setString(3, shift);
                pstmt.setInt(4, Integer.parseInt(id));
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        }



    public boolean addDoctorSchedule(int doctorId, String day, String start, String end) {
        String sql = "INSERT INTO doctor_schedules (doctor_id, available_day, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = util.DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, day);
            pstmt.setString(3, start);
            pstmt.setString(4, end);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
}

}