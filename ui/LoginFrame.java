package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        
        // Window Setup
        setTitle("HMS | Secure Login");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250)); // Light Gray Background

        // Main Container with Padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        // --- HEADER SECTION ---
        JLabel lblTitle = new JLabel("Hospital System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 62, 80)); // Dark Navy
        
        JLabel lblSubTitle = new JLabel("Please enter your credentials", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubTitle.setForeground(Color.GRAY);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- INPUT SECTION ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        inputPanel.setBackground(Color.WHITE);

        userField = new JTextField();
        passField = new JPasswordField();
        
        // Styling inputs
        userField.setBorder(BorderFactory.createTitledBorder("Username"));
        passField.setBorder(BorderFactory.createTitledBorder("Password"));

        inputPanel.add(userField);
        inputPanel.add(passField);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // --- BUTTON SECTION ---
        loginButton = new JButton("LOGIN");
        loginButton.setBackground(new Color(41, 128, 185)); // Professional Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        mainPanel.add(loginButton, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Logic
        loginButton.addActionListener(e -> handleLogin());
        
        // Press Enter to Login
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        User user = userRepo.login(username, password);
        
        if (user != null) {
            DashboardFrame dashboard;
            if (user instanceof model.Admin) {
                dashboard = new AdminDashboard(user);
            } else {
                dashboard = new DashboardFrame(user); 
            }
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}