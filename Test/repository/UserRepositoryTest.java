package Test.repository;

import repository.UserRepository;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

public class UserRepositoryTest {
    private UserRepository userRepository;
    
    @BeforeEach
    public void setUp() {
        userRepository = new UserRepository();
    }
    
    @Test
    public void testValidLogin() {
        User user = userRepository.login("admin1", "admin123");
        assertNotNull(user);
        assertEquals("admin1", user.getUsername());
        System.out.println("✓ Valid login test passed!\n");
    }
    
    @Test
    public void testInvalidLogin() {
        User user = userRepository.login("wrong_user", "wrong_pass");
        assertNull(user);
        System.out.println("✓ Invalid login test passed!\n");
    }
    
    @Test
    public void testGetAllStaffProfiles() {
        List<String[]> staff = userRepository.getAllStaffFullProfiles();
        assertNotNull(staff);
        System.out.println("✓ Get all staff profiles test passed! Found " + staff.size() + " staff members\n");
    }
    
    @Test
    public void testGetExtendedStats() {
        Map<String, Object> stats = userRepository.getExtendedStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("Doctor"));
        assertTrue(stats.containsKey("Receptionist"));
        System.out.println("✓ Extended stats test passed!\n");
    }
}

