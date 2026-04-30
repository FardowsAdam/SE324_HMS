package ui;

import javax.swing.*;
import java.awt.*;
import repository.UserRepository;
import model.User;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private UserRepository userRepo;

    public LoginFrame() {
        userRepo = new UserRepository();
        
        setTitle("Hospital Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window
        setLayout(new GridLayout(3, 2, 10, 10));

        // UI Components
        add(new JLabel(" Username:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel(" Password:"));
        passField = new JPasswordField();
        add(passField);

        loginButton = new JButton("Login");
        add(new JLabel("")); // Empty space for alignment
        add(loginButton);

        // Action Logic
        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        User user = userRepo.login(username, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getUsername());
            user.login(); // Logs to console for now
            // Future: Open the correct dashboard here
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}