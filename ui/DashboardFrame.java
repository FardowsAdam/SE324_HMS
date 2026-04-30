package ui;

import javax.swing.*;
import model.User;

public class DashboardFrame extends JFrame {
    protected User currentUser;

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("HMS Dashboard - " + user.getUsername() + " (" + user.getClass().getSimpleName() + ")");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Basic Layout
        JPanel panel = new JPanel();
        panel.add(new JLabel("Welcome to the Hospital System, " + user.getUsername()));
        add(panel);
    }
}