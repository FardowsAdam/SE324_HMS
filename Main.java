
import repository.UserRepository;
import model.User;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();

        // 1. Try to login with the 'admin1'
        System.out.println("--- HMS Login Test ---");
        User myUser = userRepo.login("admin1", "admin123");

        if (myUser != null) {
            System.out.println("Login Successful!");
            System.out.println("User: " + myUser.getUsername());
            
            // This calls the specific login() method we wrote in Admin/Doctor/Receptionist
            myUser.login(); 
        } else {
            System.out.println("Login Failed: Invalid username or password.");
        }
    }
}
