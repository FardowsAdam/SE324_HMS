package model;

/**
 * This is a Child Class.
 * It inherits everything from User and adds Doctor-specific features.
 */
public class Doctor extends User {

    // Constructor: It passes the ID, name, and password up to the User parent
    public Doctor(int userId, String username, String password) {
        super(userId, username, password);
    }

    // Implementing the required methods from the Parent
    @Override
    public void login() {
        System.out.println("Doctor " + username + " has logged into the Medical Portal.");
    }

    @Override
    public void logout() {
        System.out.println("Doctor " + username + " has securely logged out.");
    }

    // Functional Requirement: Specific to Doctors
    public void addMedicalRecord(int patientId, String diagnosis) {
        System.out.println("Recording diagnosis for Patient #" + patientId + ": " + diagnosis);
    }
}
