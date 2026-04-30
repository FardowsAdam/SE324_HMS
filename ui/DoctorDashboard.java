package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import repository.DoctorRepository;

public class DoctorDashboard extends DashboardFrame {
    private JPanel content;
    private final int doctorId;
    private final String doctorName;
    private final DoctorRepository doctorRepo = new DoctorRepository();

    // Colors consistent with your theme
    private final Color SIDEBAR_COLOR = new Color(44, 62, 80);
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color BG_COLOR = new Color(245, 246, 250);

    public DoctorDashboard(model.Doctor doctor) {
        super(doctor);
        this.doctorId = doctor.getUserId();
        this.doctorName = doctor.getUsername();;
        
        setTitle("Hospital Management System - Doctor: " + doctorName);
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 700));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JLabel lblProfile = new JLabel("Dr. " + doctorName);
        lblProfile.setForeground(Color.WHITE);
        lblProfile.setFont(new Font("SansSerif", Font.BOLD, 16));
        sidebar.add(lblProfile);

        JButton btnQueue = createSidebarBtn("Today's Appointments");
        JButton btnRecords = createSidebarBtn("Medical Records");
        JButton btnLogout = createSidebarBtn("Logout");



        sidebar.add(btnQueue);
        sidebar.add(btnRecords);
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        // --- Main Content Area ---
        content = new JPanel(new BorderLayout());
        content.setBackground(BG_COLOR);
        add(content, BorderLayout.CENTER);

        // Initial View
        btnQueue.addActionListener(e -> showTodayQueue());
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        btnRecords.addActionListener(e -> showMedicalRecords());

        showTodayQueue();
    }

    private void showTodayQueue() {
        content.removeAll();
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Patient Queue - Today", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        main.add(title, BorderLayout.NORTH);

        String[] columns = {"Appt ID", "Patient Name", "Time", "Status", "Symptoms"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        
        // Fetch logic will go here
        List<Object[]> data = doctorRepo.getTodayAppointments(doctorId);
        model.setRowCount(0); 
        for (Object[] row : data) {
            model.addRow(row);
        }

        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConsult = new JButton("Start Consultation");
        btnConsult.setBackground(PRIMARY_BLUE);
        btnConsult.setForeground(Color.WHITE);

        // Connect the button to the action
        btnConsult.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                // Get data from the selected row
                int apptId = (int) table.getValueAt(row, 0);
                String patientName = (String) table.getValueAt(row, 1);
                String symptoms = (String) table.getValueAt(row, 4);
                
                // Launch the consultation window
                openConsultationDialog(apptId, patientName, symptoms);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a patient from the queue first.");
            }
        });
        actions.add(btnConsult);
        main.add(actions, BorderLayout.SOUTH);

        content.add(main);
        content.revalidate();
        content.repaint();
    }

    private JButton createSidebarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }


    private void openConsultationDialog(int apptId, String pName, String symptoms) {
    JDialog dialog = new JDialog(this, "Consultation: " + pName, true);
    dialog.setSize(500, 600);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(15, 15));

    JPanel main = new JPanel(new GridLayout(6, 1, 10, 10));
    main.setBorder(new EmptyBorder(20, 20, 20, 20));

    main.add(new JLabel("Patient: " + pName));
    main.add(new JLabel("Symptoms: " + symptoms));

    JTextArea txtDiagnosis = new JTextArea();
    txtDiagnosis.setBorder(BorderFactory.createTitledBorder("Diagnosis"));
    
    JTextArea txtPrescription = new JTextArea();
    txtPrescription.setBorder(BorderFactory.createTitledBorder("Prescription (Medication & Dosage)"));

    main.add(new JScrollPane(txtDiagnosis));
    main.add(new JScrollPane(txtPrescription));

    JButton btnSave = new JButton("COMPLETE CONSULTATION");
    btnSave.setBackground(PRIMARY_BLUE);
    btnSave.setForeground(Color.WHITE);

    btnSave.addActionListener(e -> {
        String diagnosis = txtDiagnosis.getText().trim();
        String prescription = txtPrescription.getText().trim();

        if (diagnosis.isEmpty() || prescription.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please fill in both fields.");
            return;
        }

        if (doctorRepo.saveConsultation(apptId, diagnosis, prescription)) {
            JOptionPane.showMessageDialog(this, "Consultation Saved Successfully!");
            dialog.dispose();
            showTodayQueue(); // Refresh the table
        }
    });

    dialog.add(main, BorderLayout.CENTER);
    dialog.add(btnSave, BorderLayout.SOUTH);
    dialog.setVisible(true);
}



        private void showMedicalRecords() {
            content.removeAll();
            JPanel main = new JPanel(new BorderLayout(20, 20));
            main.setBackground(BG_COLOR);
            main.setBorder(new EmptyBorder(25, 25, 25, 25));

            // Header with Search Bar
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(BG_COLOR);
            
            JLabel title = new JLabel("Global Medical Records Search");
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            
            JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            searchBar.setBackground(BG_COLOR);
            JTextField txtSearch = new JTextField(20);
            JButton btnSearch = new JButton("Search Patient");
            searchBar.add(new JLabel("Patient Name: "));
            searchBar.add(txtSearch);
            searchBar.add(btnSearch);
            
            header.add(title, BorderLayout.WEST);
            header.add(searchBar, BorderLayout.EAST);
            main.add(header, BorderLayout.NORTH);

            // Results Table
            String[] columns = {"Date", "Diagnosis", "Prescription"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            table.setRowHeight(30);
            main.add(new JScrollPane(table), BorderLayout.CENTER);

            // Search Action
            btnSearch.addActionListener(e -> {
                String name = txtSearch.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a name to search.");
                    return;
                }
                
                List<Object[]> history = doctorRepo.getPatientHistory(name);
                model.setRowCount(0);
                for (Object[] row : history) {
                    model.addRow(row);
                }
                
                if (history.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No records found for: " + name);
                }
            });

            content.add(main);
            content.revalidate();
            content.repaint();
        }
}
