package Test.model;

import model.Admin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AdminTest {
    private Admin admin;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        admin = new Admin(1, "test_admin", "test123");
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    public void testAdminLoginOutput() {
        admin.login();
        assertTrue(outContent.toString().contains("Administrator test_admin logged in"));
        System.out.println("✓ Admin login output test passed!\n");
    }
    
    @Test
    public void testAdminLogoutOutput() {
        admin.logout();
        assertTrue(outContent.toString().contains("Administrator t"));
        System.out.println("✓ Admin logout output test passed!\n");
    }
    
    @Test
    public void testRegisterDoctor() {
        admin.registerDoctor("Dr. House");
        assertTrue(outContent.toString().contains("registering new doctor: Dr. House"));
        System.out.println("✓ Register doctor test passed!\n");
    }
    
    @Test
    public void testGenerateReport() {
        admin.generateReport();
        assertTrue(outContent.toString().contains("Generating Hospital Statistics Report"));
        System.out.println("✓ Generate report test passed!\n");
    }
}