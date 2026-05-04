package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DatabaseConnection;

public class PatientRepository {

    /**
     * Registers a new patient in the database.
     */
/**
     * Registers a new patient in the database.
     */
    public boolean registerPatient(String name, int age, String gender, String phone, String address) {
        // VALIDATION: Reject empty or null names to satisfy the test case
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Registration failed: Patient name cannot be empty.");
            return false; 
        }

        String sql = "INSERT INTO patients (full_name, age, gender, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Searches for patients by name or ID.
     */
    public List<Object[]> searchPatients(String query) {
        List<Object[]> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE full_name LIKE ? OR CAST(patient_id AS CHAR) LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                patients.add(new Object[]{
                    rs.getInt("patient_id"),
                    rs.getString("full_name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getTimestamp("created_at").toString()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public boolean bookAppointment(int patientId, int doctorId, String date, String time, String symptoms) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, symptoms) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorId);
            pstmt.setString(3, date);
            pstmt.setString(4, time);
            pstmt.setString(5, symptoms);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all appointments with Patient and Doctor names for the Manager Table.
     */
    public List<Object[]> getAllAppointments() {
        List<Object[]> list = new ArrayList<>();
        // Change u.full_name to u.username or u.name based on your database schema
        String sql = "SELECT a.appointment_id, p.full_name, u.username, a.appointment_date, " +
                    "a.appointment_time, a.status FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN users u ON a.doctor_id = u.user_id " +
                    "ORDER BY a.appointment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt(1),       // appointment_id
                    rs.getString(2),    // Patient Name
                    rs.getString(3),    // Doctor Username
                    rs.getString(4),    // Date
                    rs.getString(5),    // Time
                    rs.getString(6)     // Status
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updatePatient(int id, String name, int age, String phone, String address) {
        String sql = "UPDATE patients SET full_name=?, age=?, phone=?, address=? WHERE patient_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setInt(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean cancelAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status='Cancelled' WHERE appointment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean generateBill(int appointmentId, double amount) {
        String insertBillSql = "INSERT INTO bills (appointment_id, total_amount) VALUES (?, ?)";
        String updateStatusSql = "UPDATE appointments SET status = 'Billed' WHERE appointment_id = ?";
        
        Connection conn = null;
        try {
            conn = util.DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert the bill record
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertBillSql)) {
                pstmt1.setInt(1, appointmentId);
                pstmt1.setDouble(2, amount);
                pstmt1.executeUpdate();
            }

            // 2. Update appointment status to track that it's been billed
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateStatusSql)) {
                pstmt2.setInt(1, appointmentId);
                pstmt2.executeUpdate();
            }

            conn.commit(); // Save changes
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }



public List<Object[]> getAllBills() {
    List<Object[]> bills = new ArrayList<>();
    // Now selecting the actual created_at column
    String sql = "SELECT b.bill_id, p.full_name, b.total_amount, b.created_at " +
                 "FROM bills b " +
                 "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                 "JOIN patients p ON a.patient_id = p.patient_id " +
                 "ORDER BY b.created_at DESC";
    
    try (Connection conn = util.DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            bills.add(new Object[]{
                rs.getInt("bill_id"),
                rs.getString("full_name"),
                "$" + rs.getDouble("total_amount"),
                rs.getTimestamp("created_at").toString() // Real data instead of "N/A"
            });
        }
    } catch (SQLException e) { 
        e.printStackTrace(); 
    }
    return bills;
}

    public boolean rescheduleAppointment(int appointmentId, String newDate, String newTime) {
        String sql = "UPDATE appointments SET appointment_date = ?, appointment_time = ?, status = 'Rescheduled' WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newDate);
            pstmt.setString(2, newTime);
            pstmt.setInt(3, appointmentId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}