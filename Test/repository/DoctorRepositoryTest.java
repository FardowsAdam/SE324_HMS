
package Test.repository;

import repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class DoctorRepositoryTest {
    private DoctorRepository doctorRepository;
    
    @BeforeEach
    public void setUp() {
        doctorRepository = new DoctorRepository();
    }
    
    @Test
    public void testGetTodayAppointments() {
        List<Object[]> appointments = doctorRepository.getTodayAppointments(1);
        assertNotNull(appointments);
        System.out.println("✓ Get today's appointments test passed! Found " + appointments.size() + " appointments\n");
    }
    
    @Test
    public void testSaveConsultationWithEmptyFields() {
        boolean result = doctorRepository.saveConsultation(999, "", "");
        assertFalse(result);
        System.out.println("✓ Save consultation with empty fields test passed!\n");
    }
    
    @Test
    public void testGetPatientHistory() {
        List<Object[]> history = doctorRepository.getPatientHistory("");
        assertNotNull(history);
        System.out.println("✓ Get patient history test passed! Found " + history.size() + " records\n");
    }
}

