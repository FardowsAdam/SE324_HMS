package ui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.List;
import model.User;
import repository.UserRepository;
import services.AdminService;

public class AdminDashboard extends DashboardFrame {
    private final UserRepository userRepo = new UserRepository();
    private final AdminService adminService = new AdminService();
    private final JPanel content;
    
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SIDEBAR_COLOR = new Color(44, 62, 80);
    private final Color BG_COLOR = new Color(240, 242, 245);

    public AdminDashboard(User user) {
        super(user);
        setTitle("HMS Administration - " + user.getUsername());
        setSize(1150, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(230, 800));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        JLabel brand = new JLabel("HMS ADMIN");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 20));
        brand.setBorder(new EmptyBorder(20, 0, 30, 0));
        sidebar.add(brand);

        JButton btnHome = createSidebarBtn("Dashboard Home");
        JButton btnReg = createSidebarBtn("Staff Registration");
        JButton btnManage = createSidebarBtn("Manage Employees");
        JButton btnSchedule = createSidebarBtn("Manage Dr Schedule");
        JButton btnReport = createSidebarBtn("Generate Reports");
        JButton btnLogout = createSidebarBtn("Logout");
        
        sidebar.add(btnHome); sidebar.add(btnReg); 
        sidebar.add(btnManage); sidebar.add(btnSchedule);
        sidebar.add(btnReport); sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        content = new JPanel(new BorderLayout());
        content.setBackground(BG_COLOR);
        add(content, BorderLayout.CENTER);

        // --- ACTIONS ---
        btnHome.addActionListener(e -> showStatsPanel());
        btnReg.addActionListener(e -> showRegistrationForm());
        btnManage.addActionListener(e -> showManageStaffPanel());
        btnSchedule.addActionListener(e -> showSchedulePanel());
        btnReport.addActionListener(e -> generateSystemReport());
        btnLogout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        showStatsPanel();
    }

    // --- NEW: MANAGE SCHEDULE PANEL ---
    private void showSchedulePanel() {
        content.removeAll();
        JPanel main = new JPanel(new BorderLayout(25, 25));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Left side: Doctor List
        DefaultListModel<String> docListModel = new DefaultListModel<>();
        userRepo.getAllStaffFullProfiles().stream()
                .filter(data -> data[2].equalsIgnoreCase("Doctor"))
                .forEach(data -> docListModel.addElement(data[0] + " - " + data[3]));
        
        JList<String> docList = new JList<>(docListModel);
        docList.setFixedCellHeight(40);
        docList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane listScroll = new JScrollPane(docList);
        listScroll.setPreferredSize(new Dimension(250, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Select Doctor"));
        
        // Right side: Schedule Form Card
        JPanel formCard = new JPanel(new GridLayout(0, 1, 0, 15));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        JComboBox<String> days = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});
        JTextField startT = new JTextField("09:00");
        JTextField endT = new JTextField("17:00");
        JButton btnSave = new JButton("Set Availability");
        btnSave.setBackground(PRIMARY_COLOR); btnSave.setForeground(Color.WHITE);

        formCard.add(new JLabel("Working Day:")); formCard.add(days);
        formCard.add(new JLabel("Start Time (24h):")); formCard.add(startT);
        formCard.add(new JLabel("End Time (24h):")); formCard.add(endT);
        formCard.add(new JLabel("")); formCard.add(btnSave);

        btnSave.addActionListener(e -> {
            if (docList.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Select a doctor first."); return;
            }
            int docId = Integer.parseInt(docList.getSelectedValue().split(" - ")[0]);
            if (userRepo.addDoctorSchedule(docId, (String)days.getSelectedItem(), startT.getText(), endT.getText())) {
                JOptionPane.showMessageDialog(this, "Schedule updated for Doctor #" + docId);
            }
        });

        main.add(listScroll, BorderLayout.WEST);
        main.add(formCard, BorderLayout.CENTER);
        content.add(main);
        refreshUI();
    }

    // --- NEW: GENERATE REPORT LOGIC ---
    private void generateSystemReport() {
        Map<String, Object> stats = userRepo.getExtendedStats();
        StringBuilder report = new StringBuilder();
        report.append("==============================\n");
        report.append("  HOSPITAL MANAGEMENT REPORT  \n");
        report.append("==============================\n\n");
        report.append("Total Doctors: ").append(stats.getOrDefault("Doctor", 0)).append("\n");
        report.append("Total Receptionists: ").append(stats.getOrDefault("Receptionist", 0)).append("\n");
        report.append("Average Staff Salary: $").append(String.format("%.2f", stats.getOrDefault("avgSalary", 0.0))).append("\n\n");
        report.append("Detailed Staff List:\n");
        
        List<String[]> staff = userRepo.getAllStaffFullProfiles();
        for (String[] s : staff) {
            report.append(String.format("- [%s] %s | %s | Salary: %s\n", s[2], s[3], s[4], s[5]));
        }

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "System Summary Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- EXISTING METHODS (From your provided code) ---
    private void showStatsPanel() {
        content.removeAll();
        JPanel wrapper = new JPanel(new GridLayout(2, 2, 25, 25));
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(new EmptyBorder(40, 40, 40, 40));
        Map<String, Object> stats = userRepo.getExtendedStats();
        wrapper.add(createCard("Total Doctors", String.valueOf(stats.getOrDefault("Doctor", 0)), PRIMARY_COLOR));
        wrapper.add(createCard("Receptionists", String.valueOf(stats.getOrDefault("Receptionist", 0)), new Color(46, 204, 113)));
        wrapper.add(createCard("Avg Salary", String.format("$%.2f", stats.getOrDefault("avgSalary", 0.0)), new Color(241, 196, 15)));
        wrapper.add(createCard("System Users", "Active", new Color(155, 89, 182)));
        content.add(wrapper, BorderLayout.CENTER);
        refreshUI();
    }

    private void showManageStaffPanel() {
        content.removeAll();
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(25, 25, 25, 25));
        String[] cols = {"ID", "User", "Role", "Full Name", "Specialty/Shift", "Salary"};
        DefaultTableModel model = new DefaultTableModel(userRepo.getAllStaffFullProfiles().toArray(new String[0][0]), cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(235, 245, 251));
        table.setShowVerticalLines(false);
        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel editArea = new JPanel(new GridLayout(1, 5, 10, 0));
        editArea.setOpaque(false);
        JTextField fName = new JTextField(); JTextField fSal = new JTextField(); JTextField fExtra = new JTextField();
        JButton btnUp = new JButton("Update"); JButton btnDel = new JButton("Delete");
        styleEditField(fName, "Full Name"); styleEditField(fSal, "Salary"); styleEditField(fExtra, "Spec/Shift");
        btnUp.setBackground(PRIMARY_COLOR); btnUp.setForeground(Color.WHITE);
        btnDel.setBackground(DANGER_COLOR); btnDel.setForeground(Color.WHITE);
        editArea.add(fName); editArea.add(fSal); editArea.add(fExtra); editArea.add(btnUp); editArea.add(btnDel);
        main.add(editArea, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                fName.setText((String)table.getValueAt(r, 3));
                fExtra.setText((String)table.getValueAt(r, 4));
                fSal.setText((String)table.getValueAt(r, 5));
            }
        });

        btnUp.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1 && adminService.updateStaff((String)table.getValueAt(r, 0), fName.getText(), fSal.getText(), fExtra.getText(), (String)table.getValueAt(r, 2))) {
                JOptionPane.showMessageDialog(this, "Staff Updated"); showManageStaffPanel();
            }
        });

        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1 && JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (userRepo.deleteUser((String)table.getValueAt(r, 0))) showManageStaffPanel();
            }
        });
        content.add(main, BorderLayout.CENTER);
        refreshUI();
    }

    private void showRegistrationForm() {
        content.removeAll();
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_COLOR);
        JPanel formCard = new JPanel(new GridLayout(0, 1, 0, 15));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), new EmptyBorder(30, 40, 30, 40)));
        formCard.setPreferredSize(new Dimension(450, 600));
        JTextField u = new JTextField(); JTextField p = new JPasswordField(); 
        JTextField n = new JTextField(); JTextField s = new JTextField(); JTextField sp = new JTextField();
        JComboBox<String> r = new JComboBox<>(new String[]{"Doctor", "Receptionist"});
        styleEditField(u, "Username"); styleEditField(p, "Password"); styleEditField(n, "Full Name");
        styleEditField(s, "Salary"); styleEditField(sp, "Specialty/Shift");
        JButton sub = new JButton("CREATE ACCOUNT");
        sub.setBackground(PRIMARY_COLOR); sub.setForeground(Color.WHITE);
        formCard.add(new JLabel("Staff Onboarding", SwingConstants.CENTER));
        formCard.add(u); formCard.add(p); formCard.add(n); formCard.add(s); formCard.add(r); formCard.add(sp); formCard.add(sub);
        sub.addActionListener(e -> {
            Map<String, String> d = Map.of("name", n.getText(), "salary", s.getText(), "specialty", sp.getText(), "shift", sp.getText(), "phone", "N/A");
            if (adminService.registerStaffWithProfile(u.getText(), p.getText(), (String)r.getSelectedItem(), d)) {
                JOptionPane.showMessageDialog(this, "Success!"); showStatsPanel();
            }
        });
        center.add(formCard);
        content.add(center, BorderLayout.CENTER);
        refreshUI();
    }

    private JButton createSidebarBtn(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(210, 45));
        b.setBackground(SIDEBAR_COLOR); b.setForeground(new Color(189, 195, 199));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel createCard(String title, String value, Color accent) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 0, accent));
        JLabel t = new JLabel(title); t.setBorder(new EmptyBorder(15, 20, 0, 0));
        JLabel v = new JLabel(value); v.setFont(new Font("SansSerif", Font.BOLD, 30));
        v.setBorder(new EmptyBorder(0, 20, 15, 0));
        c.add(t, BorderLayout.NORTH); c.add(v, BorderLayout.CENTER);
        return c;
    }

    private void styleEditField(JTextField f, String hint) {
        f.setBorder(BorderFactory.createTitledBorder(hint));
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void refreshUI() { content.revalidate(); content.repaint(); }
}