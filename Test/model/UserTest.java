package Test.model;

import model.Admin;
import model.Doctor;
import model.Receptionist;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    
    @Test
    public void testAdminCreation() {
        System.out.println("=== Testing Admin ===");
        Admin admin = new Admin(1, "admin1", "admin123");
        assertEquals(1, admin.getUserId());
        assertEquals("admin1", admin.getUsername());
        assertNotNull(admin);
        admin.login();
        admin.logout();
        System.out.println("✓ Admin test passed!\n");
    }
    
    @Test
    public void testDoctorCreation() {
        System.out.println("=== Testing Doctor ===");
        Doctor doctor = new Doctor(2, "doc_smith", "doc123");
        assertEquals(2, doctor.getUserId());
        assertEquals("doc_smith", doctor.getUsername());
        assertNotNull(doctor);
        doctor.login();
        doctor.logout();
        System.out.println("✓ Doctor test passed!\n");
    }
    
    @Test
    public void testReceptionistCreation() {
        System.out.println("=== Testing Receptionist ===");
        Receptionist receptionist = new Receptionist(3, "receptionist1", "rec123");
        assertEquals(3, receptionist.getUserId());
        assertEquals("receptionist1", receptionist.getUsername());
        assertNotNull(receptionist);
        receptionist.login();
        receptionist.logout();
        System.out.println("✓ Receptionist test passed!\n");
    }
}