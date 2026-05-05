package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DatabaseConnection;

public class DoctorRepository {

public List<Object[]> getTodayAppointments(int doctorId) {
    List<Object[]> list = new ArrayList<>();
    // Use TIME_FORMAT to ensure consistent output format (HH:MM)
    String sql = "SELECT a.appointment_id, p.full_name, " +
                 "TIME_FORMAT(a.appointment_time, '%H:%i') as appointment_time, " +
                 "a.status, a.symptoms " +
                 "FROM appointments a " +
                 "JOIN patients p ON a.patient_id = p.patient_id " +
                 "WHERE a.doctor_id = ? AND DATE(a.appointment_date) = CURDATE() " +
                 "AND a.status = 'Scheduled' " + 
                 "ORDER BY a.appointment_time ASC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, doctorId);
        ResultSet rs = pstmt.executeQuery();
        
        System.out.println("DEBUG: Fetching appointments for doctor ID: " + doctorId);
        
        int count = 0;
        while (rs.next()) {
            count++;
            list.add(new Object[]{
                rs.getInt("appointment_id"),
                rs.getString("full_name"),
                rs.getString("appointment_time"), // This will be in HH:MM format
                rs.getString("status"),
                rs.getString("symptoms")
            });
            System.out.println("DEBUG: Found appointment - Patient: " + rs.getString("full_name") + 
                             ", Time: " + rs.getString("appointment_time"));
        }
        
        System.out.println("DEBUG: Total appointments found: " + count);
        
    } catch (SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        e.printStackTrace();
    }
    return list;
}
    // Existing method
    public boolean saveConsultation(int appointmentId, String diagnosis, String prescription) {
        String sql = "UPDATE appointments SET diagnosis = ?, prescription = ?, status = 'Completed' WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
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

    // Existing method
    public List<Object[]> getPatientHistory(String patientName) {
        List<Object[]> history = new ArrayList<>();
        String sql = "SELECT a.appointment_date, a.diagnosis, a.prescription " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "WHERE p.full_name LIKE ? AND a.status = 'Completed' " +
                    "ORDER BY a.appointment_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + patientName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(new Object[]{
                    rs.getDate("appointment_date"),
                    rs.getString("diagnosis"),
                    rs.getString("prescription")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    // ** NEW METHOD - Add this to DoctorRepository **
    public List<String> getAvailableTimeSlots(int doctorId, String date) {
        List<String> availableSlots = new ArrayList<>();
        
        // First, get doctor's schedule for that day of week
        String dayOfWeek = getDayOfWeek(date);
        String scheduleSql = "SELECT start_time, end_time FROM doctor_schedules " +
                            "WHERE doctor_id = ? AND available_day = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(scheduleSql)) {
            
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, dayOfWeek);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                
                // Generate 30-minute slots
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(startTime);
                java.util.Calendar endCal = java.util.Calendar.getInstance();
                endCal.setTime(endTime);
                
                // Get booked slots for this doctor on this date
                List<String> bookedSlots = getBookedSlots(doctorId, date);
                
                while (cal.before(endCal)) {
                    String timeSlot = String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), 
                                                   cal.get(java.util.Calendar.MINUTE));
                    if (!bookedSlots.contains(timeSlot)) {
                        availableSlots.add(timeSlot);
                    }
                    cal.add(java.util.Calendar.MINUTE, 30);
                }
            } else {
                // If no schedule found, add default slots (9 AM to 5 PM)
                availableSlots.add("09:00");
                availableSlots.add("09:30");
                availableSlots.add("10:00");
                availableSlots.add("10:30");
                availableSlots.add("11:00");
                availableSlots.add("11:30");
                availableSlots.add("14:00");
                availableSlots.add("14:30");
                availableSlots.add("15:00");
                availableSlots.add("15:30");
                availableSlots.add("16:00");
                availableSlots.add("16:30");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return availableSlots;
    }

    private String getDayOfWeek(String date) {
        java.time.LocalDate localDate = java.time.LocalDate.parse(date);
        return localDate.getDayOfWeek().toString();
    }

    private List<String> getBookedSlots(int doctorId, String date) {
        List<String> booked = new ArrayList<>();
        String sql = "SELECT appointment_time FROM appointments " +
                    "WHERE doctor_id = ? AND appointment_date = ? " +
                    "AND status IN ('Scheduled', 'Rescheduled')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String time = rs.getString("appointment_time");
                // Handle both formats (with or without seconds)
                if (time.length() > 5) {
                    time = time.substring(0, 5);
                }
                booked.add(time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booked;
    }
}