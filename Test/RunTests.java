package Test;

import org.junit.platform.console.ConsoleLauncher;

public class RunTests {
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Running ALL Hospital Management System Tests");
        System.out.println("===========================================\n");
        
        String[] junitArgs = {
            "--class-path", ".",
            "--scan-class-path",
            "--details", "verbose"
        };
        
        ConsoleLauncher.main(junitArgs);
    }
}