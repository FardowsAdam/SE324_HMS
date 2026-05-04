package Test.repository;
import repository.PatientRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class PatientRepositoryTest {
    private PatientRepository patientRepository;
    
    @BeforeEach
    public void setUp() {
        patientRepository = new PatientRepository();
    }
    
    @Test
    public void testPatientRegistrationWithEmptyName() {
        boolean result = patientRepository.registerPatient("", 30, "Male", "1234567890", "123 Test St");
        assertFalse(result);
        System.out.println("✓ Patient registration with empty name test passed!\n");
    }
    
    @Test
    public void testSearchPatients() {
        List<Object[]> patients = patientRepository.searchPatients("");
        assertNotNull(patients);
        System.out.println("✓ Search patients test passed! Found " + patients.size() + " patients\n");
    }
    
    @Test
    public void testGetAllAppointments() {
        List<Object[]> appointments = patientRepository.getAllAppointments();
        assertNotNull(appointments);
        System.out.println("✓ Get all appointments test passed! Found " + appointments.size() + " appointments\n");
    }
    
    @Test
    public void testGetAllBills() {
        List<Object[]> bills = patientRepository.getAllBills();
        assertNotNull(bills);
        System.out.println("✓ Get all bills test passed! Found " + bills.size() + " bills\n");
    }
    
    @Test
    public void testCancelInvalidAppointment() {
        boolean result = patientRepository.cancelAppointment(-999);
        assertFalse(result);
        System.out.println("✓ Cancel invalid appointment test passed!\n");
    }
}


