package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DatabaseConnection;

public class DoctorRepository {

    /**
     * Fetches appointments specifically for the logged-in doctor for today.
     */
    public List<Object[]> getTodayAppointments(int doctorId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT a.appointment_id, p.full_name, a.appointment_time, a.status, a.symptoms " +
                        "FROM appointments a " +
                        "JOIN patients p ON a.patient_id = p.patient_id " +
                        "WHERE a.doctor_id = ? AND a.appointment_date = CURDATE() " +
                        "AND a.status = 'Scheduled' " + 
                        "ORDER BY a.appointment_time ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("appointment_id"),
                    rs.getString("full_name"),
                    rs.getString("appointment_time"),
                    rs.getString("status"),
                    rs.getString("symptoms")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean saveConsultation(int appointmentId, String diagnosis, String prescription) {
        // The key here is: status = 'Completed'
        String sql = "UPDATE appointments SET diagnosis = ?, prescription = ?, status = 'Completed' WHERE appointment_id = ?";
        
        try (Connection conn = util.DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, diagnosis);
            pstmt.setString(2, prescription);
            pstmt.setInt(3, appointmentId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public List<Object[]> getPatientHistory(String patientName) {
        List<Object[]> history = new java.util.ArrayList<>();
        // Join appointments with patients to search by name
        String sql = "SELECT a.appointment_date, a.diagnosis, a.prescription " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "WHERE p.full_name LIKE ? AND a.status = 'Completed' " +
                    "ORDER BY a.appointment_date DESC";

        try (java.sql.Connection conn = util.DatabaseConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + patientName + "%");
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(new Object[]{
                    rs.getDate("appointment_date"),
                    rs.getString("diagnosis"),
                    rs.getString("prescription")
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}