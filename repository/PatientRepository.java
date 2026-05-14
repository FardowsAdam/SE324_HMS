package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

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



    // Update this method in PatientRepository.java
// Update this method in PatientRepository.java
public boolean bookAppointment(int patientId, int doctorId, String date, String time, String symptoms) {
    // Format time to ensure it has proper MySQL TIME format (HH:MM:SS)
    String formattedTime = time;
    if (time != null && !time.isEmpty()) {
        // If time doesn't have seconds, add :00
        if (time.length() == 5 && time.contains(":")) {
            formattedTime = time + ":00";
        } else if (time.length() == 4 && time.contains(":")) {
            // Handle "9:30" format
            String[] parts = time.split(":");
            formattedTime = String.format("%02d:%s:00", Integer.parseInt(parts[0]), parts[1]);
        }
    }
    
    System.out.println("DEBUG: Booking - Original time: " + time + ", Formatted time: " + formattedTime);
    
    // First check if slot is already booked //////
    String checkSql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'Cancelled'";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
        
        checkStmt.setInt(1, doctorId);
        checkStmt.setString(2, date);
        checkStmt.setString(3, formattedTime);
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "This time slot is already booked!");
            return false;
        }
        
        // If slot is free, book it
        String insertSql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, symptoms, status) VALUES (?, ?, ?, ?, ?, 'Scheduled')";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorId);
            pstmt.setString(3, date);
            pstmt.setString(4, formattedTime);
            pstmt.setString(5, symptoms);
            
            int result = pstmt.executeUpdate();
            System.out.println("DEBUG: Insert result: " + result + " row(s) affected");
            return result > 0;
        }
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
    String sql = "SELECT a.appointment_id, p.full_name, u.username, a.appointment_date, " +
                "a.appointment_time, a.status FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users u ON a.doctor_id = u.user_id " +
                "WHERE a.status != 'Cancelled' " +  // Exclude cancelled appointments
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
            if (conn != null) { ///////////////
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
    String sql = "SELECT b.bill_id, p.full_name, b.total_amount, b.created_at, a.status " +
                 "FROM bills b " +
                 "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                 "JOIN patients p ON a.patient_id = p.patient_id " +
                 "WHERE b.payment_status = 'Pending' OR b.payment_status = 'Paid' " +
                 "ORDER BY b.created_at DESC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            bills.add(new Object[]{
                rs.getInt("bill_id"),
                rs.getString("full_name"),
                "$" + rs.getDouble("total_amount"),
                rs.getTimestamp("created_at").toString()
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