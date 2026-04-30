package ui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import model.User;
import java.util.List;

public class ReceptionistDashboard extends DashboardFrame {
    private final JPanel content;
    private final repository.PatientRepository patientRepo = new repository.PatientRepository();
    
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color SIDEBAR_COLOR = new Color(44, 62, 80);
    private final Color BG_COLOR = new Color(240, 242, 245);
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);

    public ReceptionistDashboard(User user) {
        super(user);
        setTitle("HMS Receptionist - " + user.getUsername());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(230, 800));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        JLabel brand = new JLabel("HMS RECEPTION");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 18));
        brand.setBorder(new EmptyBorder(20, 0, 30, 0));
        sidebar.add(brand);

        JButton btnSearch = createSidebarBtn("View/Search Patients");
        JButton btnRegister = createSidebarBtn("Register Patient");
        JButton btnManageAppts = createSidebarBtn("Manage Appointments");
        JButton btnBilling = createSidebarBtn("Generate Bill");
        JButton btnViewBills = createSidebarBtn("Billing History");
        JButton btnLogout = createSidebarBtn("Logout");

        sidebar.add(btnSearch); 
        sidebar.add(btnRegister); 
        sidebar.add(btnManageAppts);
        sidebar.add(btnBilling); 
        sidebar.add(btnViewBills);
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        // --- MAIN CONTENT AREA ---
        content = new JPanel(new BorderLayout());
        content.setBackground(BG_COLOR);
        add(content, BorderLayout.CENTER);

        // --- ACTION LISTENERS ---
        btnSearch.addActionListener(e -> showPatientSearch());
        btnRegister.addActionListener(e -> showPatientRegistration());
        btnBilling.addActionListener(e -> showBillingPanel());
        btnManageAppts.addActionListener(e -> showAppointmentManager());
        btnLogout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        btnViewBills.addActionListener(e -> showBillingHistory());

        showPatientSearch(); 
    }

    private void showPatientRegistration() {
        content.removeAll();
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_COLOR);
        
        JPanel formCard = new JPanel(new GridLayout(0, 1, 0, 15));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            new EmptyBorder(30, 40, 30, 40)
        ));
        formCard.setPreferredSize(new Dimension(500, 650));

        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField phoneField = new JTextField();
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(BorderFactory.createTitledBorder("Full Address"));

        styleEditField(nameField, "Patient Full Name");
        styleEditField(ageField, "Age");
        styleEditField(phoneField, "Phone Number");

        JButton btnSubmit = new JButton("REGISTER PATIENT");
        btnSubmit.setBackground(ACCENT_COLOR);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSubmit.setPreferredSize(new Dimension(0, 50));

        formCard.add(new JLabel("New Patient Onboarding", SwingConstants.CENTER));
        formCard.add(nameField);
        formCard.add(ageField);
        formCard.add(new JLabel("Gender:"));
        formCard.add(genderBox);
        formCard.add(phoneField);
        formCard.add(addressScroll);
        formCard.add(btnSubmit);

        btnSubmit.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = (String) genderBox.getSelectedItem();
                String phone = phoneField.getText();
                String address = addressArea.getText();

                if (patientRepo.registerPatient(name, age, gender, phone, address)) {
                    JOptionPane.showMessageDialog(this, "Patient Registered Successfully!");
                    showPatientSearch();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please check input fields.");
            }
        });

        center.add(formCard);
        content.add(center, BorderLayout.CENTER);
        refreshUI();
    }


    private void showPatientSearch() {
    content.removeAll();
    JPanel main = new JPanel(new BorderLayout(20, 20));
    main.setBackground(BG_COLOR);
    main.setBorder(new EmptyBorder(25, 25, 25, 25));

    // --- Search Bar ---
    JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
    searchPanel.setOpaque(false);
    
    JTextField searchField = new JTextField();
    searchField.setPreferredSize(new Dimension(0, 45));
    searchField.setBorder(BorderFactory.createTitledBorder("Search by Patient Name or ID"));
    
    JButton btnSearchAction = new JButton("SEARCH");
    btnSearchAction.setBackground(PRIMARY_BLUE);
    btnSearchAction.setForeground(Color.WHITE);
    
    searchPanel.add(searchField, BorderLayout.CENTER);
    searchPanel.add(btnSearchAction, BorderLayout.EAST);
    main.add(searchPanel, BorderLayout.NORTH);

    // --- Table ---
    String[] columns = {"ID", "Full Name", "Age", "Gender", "Phone", "Reg Date"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable table = new JTable(model);
    table.setRowHeight(40);
    main.add(new JScrollPane(table), BorderLayout.CENTER);

    // --- Bottom Actions ---
    JPanel bottomActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
    bottomActions.setOpaque(false);
    JButton btnBook = new JButton("Book Appointment");
    btnBook.setBackground(ACCENT_COLOR);
    btnBook.setForeground(Color.WHITE);
    btnBook.setPreferredSize(new Dimension(200, 45));
    bottomActions.add(btnBook);
    main.add(bottomActions, BorderLayout.SOUTH);

    // --- LOGIC: Refresh Table ---
    // This helper fetches data based on the current search text
    Runnable refreshTable = () -> {
        String query = searchField.getText().trim();
        List<Object[]> results = patientRepo.searchPatients(query);
        model.setRowCount(0); // Clear existing rows
        for (Object[] row : results) {
            model.addRow(row);
        }
    };

    // Trigger search when button is clicked
    btnSearchAction.addActionListener(e -> refreshTable.run());

    // Trigger search when "Enter" is pressed in the search field
    searchField.addActionListener(e -> refreshTable.run());

    btnBook.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row != -1) {
            showAppointmentBooking(table.getValueAt(row, 0).toString(), table.getValueAt(row, 1).toString());
        } else {
            JOptionPane.showMessageDialog(this, "Select a patient first.");
        }
    });

    // --- THE FIX: Load all patients immediately ---
    refreshTable.run(); 

    content.add(main, BorderLayout.CENTER);
    refreshUI();
}

    private void showAppointmentBooking(String pId, String pName) {
        content.removeAll();
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG_COLOR);

        JPanel card = new JPanel(new GridLayout(0, 1, 0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(550, 650));

        JLabel title = new JLabel("Booking for: " + pName, SwingConstants.CENTER);
        DefaultListModel<String> docModel = new DefaultListModel<>();
        new repository.UserRepository().getAllStaffFullProfiles().stream()
            .filter(data -> data[2].toString().equalsIgnoreCase("Doctor"))
            .forEach(data -> docModel.addElement(data[0] + " - Dr. " + data[3]));
        
        JList<String> docList = new JList<>(docModel);
        JTextField dateField = new JTextField("2026-05-01");
        JTextField timeField = new JTextField("10:00");
        JTextArea symptomArea = new JTextArea(3, 20);
        symptomArea.setBorder(BorderFactory.createTitledBorder("Symptoms"));

        styleEditField(dateField, "Date (YYYY-MM-DD)");
        styleEditField(timeField, "Time (HH:MM)");

        JButton btnConfirm = new JButton("CONFIRM APPOINTMENT");
        btnConfirm.setBackground(ACCENT_COLOR);
        btnConfirm.setForeground(Color.WHITE);

        card.add(title);
        card.add(new JScrollPane(docList));
        card.add(dateField);
        card.add(timeField);
        card.add(new JScrollPane(symptomArea));
        card.add(btnConfirm);

        btnConfirm.addActionListener(e -> {
            if (docList.getSelectedIndex() != -1) {
                int docId = Integer.parseInt(docList.getSelectedValue().split(" - ")[0]);
                if (patientRepo.bookAppointment(Integer.parseInt(pId), docId, dateField.getText(), timeField.getText(), symptomArea.getText())) {
                    JOptionPane.showMessageDialog(this, "Success!");
                    showAppointmentManager();
                }
            }
        });

        main.add(card);
        content.add(main, BorderLayout.CENTER);
        refreshUI();
    }

    private void showAppointmentManager() {
        content.removeAll();
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Appointment Management", new ImageIcon(), SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        main.add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Patient", "Doctor", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(new String[][]{}, columns);
        JTable table = new JTable(model);
        
        List<Object[]> appointments = patientRepo.getAllAppointments();
        for (Object[] row : appointments) model.addRow(row);

        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReschedule = new JButton("Reschedule");
        JButton btnCancel = new JButton("Cancel Appointment");
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);
        
        actions.add(btnCancel);
        actions.add(btnReschedule);
        main.add(actions, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int apptId = (int) table.getValueAt(row, 0);
                if (patientRepo.cancelAppointment(apptId)) {
                    JOptionPane.showMessageDialog(this, "Cancelled");
                    showAppointmentManager();
                }
            }
        });

        btnReschedule.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) openRescheduleDialog((int) table.getValueAt(row, 0));
        });

        content.add(main);
        refreshUI();
    }

    private void openRescheduleDialog(int apptId) {
        JTextField d = new JTextField(10);
        JTextField t = new JTextField(10);
        JPanel p = new JPanel(new GridLayout(0, 1));
        p.add(new JLabel("New Date:")); p.add(d);
        p.add(new JLabel("New Time:")); p.add(t);

        if (JOptionPane.showConfirmDialog(null, p, "Reschedule", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (patientRepo.rescheduleAppointment(apptId, d.getText(), t.getText())) {
                showAppointmentManager();
            }
        }
    }

    private void showBillingPanel() {
        content.removeAll();
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG_COLOR);

        JPanel billForm = new JPanel(new GridLayout(0, 1, 0, 10));
        billForm.setBackground(Color.WHITE);
        billCardStyle(billForm);

        JTextField apptIdField = new JTextField();
        JTextField amountField = new JTextField();
        JButton btnCreateBill = new JButton("CONFIRM BILLING");
        btnCreateBill.setBackground(ACCENT_COLOR);
        btnCreateBill.setForeground(Color.WHITE);

        billForm.add(new JLabel("Enter Appointment ID:")); billForm.add(apptIdField);
        billForm.add(new JLabel("Total Amount ($):")); billForm.add(amountField);
        billForm.add(btnCreateBill);

        btnCreateBill.addActionListener(e -> {
            try {
                int id = Integer.parseInt(apptIdField.getText());
                double amt = Double.parseDouble(amountField.getText());
                if (patientRepo.generateBill(id, amt)) {
                    JOptionPane.showMessageDialog(this, "Bill Processed!");
                    showPatientSearch();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Entry"); }
        });

        main.add(billForm);
        content.add(main, BorderLayout.CENTER);
        refreshUI();
    }

    private void billCardStyle(JPanel p) {
        p.setPreferredSize(new Dimension(400, 300));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Billing Engine"), 
            new EmptyBorder(20, 20, 20, 20)));
    }

    private JButton createSidebarBtn(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(210, 45));
        b.setBackground(SIDEBAR_COLOR);
        b.setForeground(new Color(189, 195, 199));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void styleEditField(JTextField f, String hint) {
        f.setBorder(BorderFactory.createTitledBorder(hint));
    }

    private void refreshUI() {
        content.revalidate();
        content.repaint();
    }



    private void showBillingHistory() {
    content.removeAll();
    JPanel main = new JPanel(new BorderLayout(20, 20));
    main.setBackground(BG_COLOR);
    main.setBorder(new EmptyBorder(25, 25, 25, 25));

    JLabel title = new JLabel("Processed Bills History", SwingConstants.LEFT);
    title.setFont(new Font("SansSerif", Font.BOLD, 18));
    main.add(title, BorderLayout.NORTH);

    String[] columns = {"Bill ID", "Patient Name", "Amount", "Date/Time"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);
    
    // Fetch tracked bills from repo
    List<Object[]> billData = patientRepo.getAllBills();
    for (Object[] row : billData) model.addRow(row);

    main.add(new JScrollPane(table), BorderLayout.CENTER);
    
    content.add(main);
    refreshUI();
}
}