
/**
 * This is a Child Class.
 * Receptionists handle patient intake and appointment management.
 */
public class Receptionist extends User {

    public Receptionist(int userId, String username, String password) {
        super(userId, username, password);
    }

    @Override
    public void login() {
        System.out.println("Receptionist " + username + " logged in. Ready for patient intake.");
    }

    @Override
    public void logout() {
        System.out.println("Receptionist " + username + " logged out. Shift completed.");
    }

    // Functional Requirement: Specific to Receptionists
    public void registerPatient(String patientName) {
        System.out.println("Receptionist " + username + " is registering new patient: " + patientName);
    }

    public void scheduleAppointment(int patientId, String date) {
        System.out.println("Appointment scheduled for Patient #" + patientId + " on " + date);
    }
}