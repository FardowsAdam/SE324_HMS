package repository;

import java.sql.*;
import util.DatabaseConnection;
import model.*;

public class UserRepository {

    /**
     * Finds a user in the database and returns the specific Role object.
     */
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

                // We use the database role to create the specific Java object
                return switch (role) {
                    case "Admin" -> new Admin(id, username, password);
                    case "Doctor" -> new Doctor(id, username, password);
                    case "Receptionist" -> new Receptionist(id, username, password);
                    default -> null;
                };
            }
        } catch (SQLException e) {
            System.err.println("Database error during login!");
            e.printStackTrace();
        }
        return null; // Return null if user is not found
    }
}