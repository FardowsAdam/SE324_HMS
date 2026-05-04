package Test.model;

import model.Receptionist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ReceptionistTest {
    private Receptionist receptionist;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        receptionist = new Receptionist(3, "test_rec", "rec123");
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    public void testReceptionistLoginOutput() {
        receptionist.login();
        assertTrue(outContent.toString().contains("Receptionist test_rec logged in. Ready for patient intake"));
        System.out.println("✓ Receptionist login output test passed!\n");
    }
    
    @Test
    public void testReceptionistLogoutOutput() {
        receptionist.logout();
        assertTrue(outContent.toString().contains("Receptionist test_rec logged out. Shift completed"));
        System.out.println("✓ Receptionist logout output test passed!\n");
    }
    
    @Test
    public void testRegisterPatient() {
        receptionist.registerPatient("John Doe");
        assertTrue(outContent.toString().contains("Receptionist test_rec is registering new patient: John Doe"));
        System.out.println("✓ Register patient test passed!\n");
    }
    
    @Test
    public void testScheduleAppointment() {
        receptionist.scheduleAppointment(100, "2026-05-10");
        assertTrue(outContent.toString().contains("Appointment scheduled for Patient #100 on 2026-05-10"));
        System.out.println("✓ Schedule appointment test passed!\n");
    }
}