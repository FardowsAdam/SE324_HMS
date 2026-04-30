package model;

/**
 * This is a Child Class.
 * Admins handle system management and staff registration.
 */
public class Admin extends User {

    public Admin(int userId, String username, String password) {
        super(userId, username, password);
    }

    @Override
    public void login() {
        System.out.println("Administrator " + username + " logged in with full system access.");
    }

    @Override
    public void logout() {
        System.out.println("Administrator " + username + " logged out. Audit log updated.");
    }

    // Functional Requirement: Specific to Admins
    public void registerDoctor(String doctorName) {
        System.out.println("Admin " + username + " is registering new doctor: " + doctorName);
    }

    public void generateReport() {
        System.out.println("Generating Hospital Statistics Report...");
    }
}
