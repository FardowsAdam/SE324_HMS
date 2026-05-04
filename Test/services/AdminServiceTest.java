package Test.services;


import services.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

public class AdminServiceTest {
    private AdminService adminService;
    
    @BeforeEach
    public void setUp() {
        adminService = new AdminService();
    }
    
    @Test
    public void testRegisterStaffWithMissingData() {
        Map<String, String> details = new HashMap<>();
        details.put("name", "");
        details.put("salary", "50000");
        details.put("specialty", "Cardiology");
        details.put("shift", "Morning");
        details.put("phone", "1234567890");
        
        boolean result = adminService.registerStaffWithProfile("", "pass", "Doctor", details);
        assertFalse(result);
        System.out.println("✓ Register staff with missing data test passed!\n");
    }
    
    @Test
    public void testUpdateStaffWithInvalidId() {
        boolean result = adminService.updateStaff("-999", "Test Name", "50000", "Cardiology", "Doctor");
        assertFalse(result);
        System.out.println("✓ Update staff with invalid ID test passed!\n");
    }
    
    @Test
    public void testRegisterStaffWithInvalidSalary() {
        Map<String, String> details = new HashMap<>();
        details.put("name", "Test Doctor");
        details.put("salary", "invalid_salary");
        details.put("specialty", "Cardiology");
        details.put("shift", "Morning");
        details.put("phone", "1234567890");
        
        boolean result = adminService.registerStaffWithProfile("testuser", "testpass", "Doctor", details);
        assertFalse(result);
        System.out.println("✓ Register staff with invalid salary test passed!\n");
    }
}
