package Test.model;

import model.Doctor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DoctorTest {
    private Doctor doctor;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        doctor = new Doctor(2, "test_doctor", "doc123");
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    public void testDoctorLoginOutput() {
        doctor.login();
        assertTrue(outContent.toString().contains("Doctor test_doctor has logged into the Medical Portal"));
        System.out.println("✓ Doctor login output test passed!\n");
    }
    
    @Test
    public void testDoctorLogoutOutput() {
        doctor.logout();
        assertTrue(outContent.toString().contains("Doctor test_doctor has securely logged out"));
        System.out.println("✓ Doctor logout output test passed!\n");
    }
    
    @Test
    public void testAddMedicalRecord() {
        doctor.addMedicalRecord(100, "Patient has flu symptoms");
        assertTrue(outContent.toString().contains("Recording diagnosis for Patient #100: Patient has flu symptoms"));
        System.out.println("✓ Add medical record test passed!\n");
    }
}