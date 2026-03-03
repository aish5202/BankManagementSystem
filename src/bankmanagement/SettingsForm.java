/*
 * Bank Management System
 * SettingsForm.java - System settings & user management
 */
package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class SettingsForm extends JFrame {

    private static final Color C_NAVY      = new Color(10,  22,  40);
    private static final Color C_PANEL     = new Color(18,  22,  32);
    private static final Color C_HEADER    = new Color(33,  33,  33);
    private static final Color C_HEADER2   = new Color(55,  65,  80);
    private static final Color C_CARD_BG   = new Color(22,  28,  42);
    private static final Color C_FIELD_BG  = new Color(30,  38,  58);
    private static final Color C_FIELD_BOR = new Color(70,  90, 130);
    private static final Color C_FIELD_FOC = new Color(0,  180, 216);
    private static final Color C_THEAD     = new Color(40,  55,  80);
    private static final Color C_SEL       = new Color(20,  55,  95);
    private static final Color C_ALT       = new Color(15,  20,  32);
    private static final Color C_WHITE     = Color.WHITE;
    private static final Color C_MUTED     = new Color(130, 155, 195);
    private static final Color C_STATUS    = new Color(12,  15,  25);
    private static final Color C_ACCENT    = new Color(0,  180, 216);
    private static final Color C_GREEN     = new Color(0,  200, 130);
    private static final Color C_RED       = new Color(220,  70,  70);
    private static final Color C_YELLOW    = new Color(255, 200,  50);

    // ── User Management tab ───────────────────────────────────
    private JTextField     txtUserId, txtUsername, txtNewPass, txtConfirmPass;
    private JComboBox<String> cmbRole;
    private JTable            userTable;
    private DefaultTableModel userModel;
    private JButton btnAddUser, btnUpdateUser, btnDeleteUser,
                    btnClearUser, btnRefreshUser;

    // ── Change Password tab ───────────────────────────────────
    private JTextField     txtCPUser;
    private JPasswordField txtCPOld, txtCPNew, txtCPConfirm;
    private JButton        btnChangePass;

    // ── DB Info tab ───────────────────────────────────────────
    private JLabel lblDBStatus, lblDBUrl, lblDBUser, lblDBVersion;

    // ── Tabs ──────────────────────────────────────────────────
    private JTabbedPane tabs;

    public SettingsForm() {
        setTitle("Settings — BMS");
        setSize(980, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_NAVY);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        loadUsers();
        loadDBInfo();
    }

    // ── HEADER ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,C_HEADER,getWidth(),0,C_HEADER2));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        h.setPreferredSize(new Dimension(980, 58));
        h.setLayout(new FlowLayout(FlowLayout.LEFT, 22, 0));
        JLabel l = new JLabel("\u2699\uFE0F   System Settings");
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setForeground(C_WHITE);
        h.add(l);
        return h;
    }

    // ── CENTER — tabbed pane ──────────────────────────────────
    private JPanel buildCenter() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(C_NAVY);
        c.setBorder(new EmptyBorder(16, 20, 0, 20));

        tabs = new JTabbedPane();
        tabs.setBackground(C_PANEL);
        tabs.setForeground(C_WHITE);
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        // Style tab colours
        UIManager.put("TabbedPane.selected",            C_CARD_BG);
        UIManager.put("TabbedPane.background",          C_PANEL);
        UIManager.put("TabbedPane.foreground",          C_WHITE);
        UIManager.put("TabbedPane.contentAreaColor",    C_CARD_BG);
        UIManager.put("TabbedPane.tabAreaBackground",   C_PANEL);

        tabs.addTab("\uD83D\uDC64  User Management",  buildUserManagementTab());
        tabs.addTab("\uD83D\uDD11  Change Password",  buildChangePasswordTab());
        tabs.addTab("\uD83D\uDDA5\uFE0F  DB Info",    buildDBInfoTab());
        tabs.addTab("\u2139\uFE0F  About",            buildAboutTab());

        c.add(tabs, BorderLayout.CENTER);
        return c;
    }

    // ══════════════════════════════════════════════════════════
    // TAB 1 — USER MANAGEMENT
    // ══════════════════════════════════════════════════════════
    private JPanel buildUserManagementTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 14));
        tab.setBackground(C_CARD_BG);
        tab.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70,90,130,100),1),
            new EmptyBorder(16,18,16,18)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,8,6,8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel st = new JLabel("\u25B8  User Details");
        st.setFont(new Font("Arial",Font.BOLD,12));
        st.setForeground(C_ACCENT);
        g.gridx=0; g.gridy=0; g.gridwidth=4;
        form.add(st,g); g.gridwidth=1;

        txtUserId   = addField(form,g,"User ID (Auto)", 1,0,true);
        txtUsername = addField(form,g,"Username ✱",     1,2,false);

        g.gridx=0; g.gridy=2; g.weightx=0;
        JLabel lr = new JLabel("Role ✱");
        lr.setFont(new Font("Arial",Font.BOLD,11)); lr.setForeground(C_MUTED);
        form.add(lr,g);
        g.gridx=1; g.weightx=1;
        cmbRole = new JComboBox<>(new String[]{"admin","staff"});
        cmbRole.setFont(new Font("Arial",Font.PLAIN,13));
        cmbRole.setBackground(C_FIELD_BG); cmbRole.setForeground(C_WHITE);
        form.add(cmbRole,g);

        txtNewPass = addField(form,g,"Password ✱", 2,2,false);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        btnRow.setBackground(C_CARD_BG);
        btnAddUser     = makeBtn("➕  Add User",   new Color(21,101,192), new Color(30,136,229));
        btnUpdateUser  = makeBtn("✏\uFE0F  Update",   new Color(0,121,107),  new Color(0,150,136));
        btnDeleteUser  = makeBtn("\uD83D\uDDD1  Delete",  new Color(183,28,28),  new Color(211,47,47));
        btnClearUser   = makeBtn("\uD83D\uDD04  Clear",   new Color(69,90,100),  new Color(96,125,139));
        btnRefreshUser = makeBtn("\u21BB  Refresh",  new Color(74,20,140),   new Color(106,27,154));
        btnRow.add(btnAddUser); btnRow.add(btnUpdateUser); btnRow.add(btnDeleteUser);
        btnRow.add(btnClearUser); btnRow.add(btnRefreshUser);

        // Table
        userModel = new DefaultTableModel(
            new String[]{"ID","Username","Role"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        userTable = new JTable(userModel){
            @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
                Component c=super.prepareRenderer(r,row,col);
                if(!isRowSelected(row)){
                    c.setBackground(row%2==0?C_PANEL:C_ALT);
                    c.setForeground(C_WHITE);
                    if(col==2){
                        String role=userModel.getValueAt(row,2).toString();
                        c.setForeground("admin".equals(role)?C_YELLOW:C_GREEN);
                    }
                } else { c.setBackground(C_SEL); c.setForeground(C_WHITE); }
                return c;
            }
        };
        styleTable(userTable, C_THEAD);

        // Row click
        userTable.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()&&userTable.getSelectedRow()>=0){
                int r=userTable.getSelectedRow();
                txtUserId.setText(userModel.getValueAt(r,0).toString());
                txtUsername.setText(userModel.getValueAt(r,1).toString());
                cmbRole.setSelectedItem(userModel.getValueAt(r,2).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.getViewport().setBackground(C_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(70,90,130,100),1));

        JLabel tl = new JLabel("\u25B8  System Users");
        tl.setFont(new Font("Arial",Font.BOLD,12));
        tl.setForeground(C_ACCENT);
        tl.setBorder(new EmptyBorder(0,0,6,0));

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(C_CARD_BG);
        tableWrap.add(tl,     BorderLayout.NORTH);
        tableWrap.add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0,10));
        top.setBackground(C_CARD_BG);
        top.add(form,   BorderLayout.CENTER);
        top.add(btnRow, BorderLayout.SOUTH);

        tab.add(top,       BorderLayout.NORTH);
        tab.add(tableWrap, BorderLayout.CENTER);

        // Wire buttons
        btnAddUser.addActionListener(e    -> addUser());
        btnUpdateUser.addActionListener(e -> updateUser());
        btnDeleteUser.addActionListener(e -> deleteUser());
        btnClearUser.addActionListener(e  -> clearUserFields());
        btnRefreshUser.addActionListener(e-> loadUsers());

        return tab;
    }

    // ══════════════════════════════════════════════════════════
    // TAB 2 — CHANGE PASSWORD
    // ══════════════════════════════════════════════════════════
    private JPanel buildChangePasswordTab() {
        JPanel tab = new JPanel(new GridBagLayout());
        tab.setBackground(C_CARD_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(C_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70,90,130,100),1),
            new EmptyBorder(30,40,30,40)));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx=0; g.weightx=1;

        JLabel title = new JLabel("\uD83D\uDD11  Change Your Password");
        title.setFont(new Font("Arial",Font.BOLD,18));
        title.setForeground(C_WHITE);
        g.gridy=0; g.insets=new Insets(0,0,6,0); card.add(title,g);

        JLabel sub = new JLabel("Enter your username, current password and new password below.");
        sub.setFont(new Font("Arial",Font.PLAIN,12));
        sub.setForeground(C_MUTED);
        g.gridy=1; g.insets=new Insets(0,0,28,0); card.add(sub,g);

        String[] lbls = {"Username", "Current Password", "New Password", "Confirm New Password"};
        g.insets = new Insets(0,0,6,0);

        // Username
        g.gridy=2; card.add(makeFormLabel("Username"),g);
        g.gridy=3; g.insets=new Insets(0,0,16,0);
        txtCPUser = new JTextField(); styleField(txtCPUser); card.add(txtCPUser,g);

        // Old password
        g.gridy=4; g.insets=new Insets(0,0,6,0); card.add(makeFormLabel("Current Password"),g);
        g.gridy=5; g.insets=new Insets(0,0,16,0);
        txtCPOld = new JPasswordField(); styleField(txtCPOld); card.add(txtCPOld,g);

        // New password
        g.gridy=6; g.insets=new Insets(0,0,6,0); card.add(makeFormLabel("New Password"),g);
        g.gridy=7; g.insets=new Insets(0,0,16,0);
        txtCPNew = new JPasswordField(); styleField(txtCPNew); card.add(txtCPNew,g);

        // Confirm password
        g.gridy=8; g.insets=new Insets(0,0,6,0); card.add(makeFormLabel("Confirm New Password"),g);
        g.gridy=9; g.insets=new Insets(0,0,26,0);
        txtCPConfirm = new JPasswordField(); styleField(txtCPConfirm); card.add(txtCPConfirm,g);

        btnChangePass = makeBtn("\uD83D\uDD10  Change Password",
                new Color(21,101,192), new Color(30,136,229));
        btnChangePass.setPreferredSize(new Dimension(200,44));
        g.gridy=10; g.insets=new Insets(0,0,0,0);
        card.add(btnChangePass,g);

        btnChangePass.addActionListener(e -> changePassword());

        GridBagConstraints outer = new GridBagConstraints();
        outer.fill=GridBagConstraints.NONE; outer.anchor=GridBagConstraints.CENTER;
        outer.ipadx=0; outer.ipady=0;
        Dimension cardSize = new Dimension(460,460);
        card.setPreferredSize(cardSize);
        tab.add(card, outer);

        return tab;
    }

    // ══════════════════════════════════════════════════════════
    // TAB 3 — DB INFO
    // ══════════════════════════════════════════════════════════
    private JPanel buildDBInfoTab() {
        JPanel tab = new JPanel(new GridBagLayout());
        tab.setBackground(C_CARD_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(C_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70,90,130,100),1),
            new EmptyBorder(30,40,30,40)));
        card.setPreferredSize(new Dimension(500,360));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx=0; g.weightx=1;

        JLabel title = new JLabel("\uD83D\uDDA5\uFE0F  Database Connection Info");
        title.setFont(new Font("Arial",Font.BOLD,18));
        title.setForeground(C_WHITE);
        g.gridy=0; g.insets=new Insets(0,0,24,0); card.add(title,g);

        lblDBStatus  = new JLabel("Checking...");
        lblDBUrl     = new JLabel("—");
        lblDBUser    = new JLabel("—");
        lblDBVersion = new JLabel("—");

        String[][] rows = {
            {"Status",           null},
            {"URL",              null},
            {"User",             null},
            {"DB Version",       null}
        };
        JLabel[] valLabels = {lblDBStatus, lblDBUrl, lblDBUser, lblDBVersion};

        for (int i = 0; i < rows.length; i++) {
            JPanel row = new JPanel(new BorderLayout(20,0));
            row.setBackground(i%2==0?C_CARD_BG:C_ALT);
            row.setBorder(new EmptyBorder(12,16,12,16));

            JLabel key = new JLabel(rows[i][0]);
            key.setFont(new Font("Arial",Font.BOLD,13));
            key.setForeground(C_MUTED);
            key.setPreferredSize(new Dimension(140,20));

            valLabels[i].setFont(new Font("Arial",Font.PLAIN,13));
            valLabels[i].setForeground(C_WHITE);

            row.add(key,          BorderLayout.WEST);
            row.add(valLabels[i], BorderLayout.CENTER);

            g.gridy = i+1; g.insets=new Insets(0,0,2,0);
            card.add(row,g);
        }

        JButton btnTestConn = makeBtn("\uD83D\uDD04  Test Connection",
                new Color(0,121,107), new Color(0,150,136));
        btnTestConn.setPreferredSize(new Dimension(180,40));
        g.gridy=6; g.insets=new Insets(24,0,0,0);
        card.add(btnTestConn,g);
        btnTestConn.addActionListener(e -> loadDBInfo());

        tab.add(card, new GridBagConstraints());
        return tab;
    }

    // ══════════════════════════════════════════════════════════
    // TAB 4 — ABOUT
    // ══════════════════════════════════════════════════════════
    private JPanel buildAboutTab() {
        JPanel tab = new JPanel(new GridBagLayout());
        tab.setBackground(C_CARD_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(C_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70,90,130,100),1),
            new EmptyBorder(40,60,40,60)));
        card.setPreferredSize(new Dimension(500,400));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.anchor=GridBagConstraints.CENTER;

        JLabel icon = new JLabel("\uD83C\uDFE6");
        icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,60));
        g.gridy=0; g.insets=new Insets(0,0,16,0); card.add(icon,g);

        JLabel name = new JLabel("Bank Management System");
        name.setFont(new Font("Arial",Font.BOLD,22));
        name.setForeground(C_WHITE);
        g.gridy=1; g.insets=new Insets(0,0,6,0); card.add(name,g);

        JLabel ver = new JLabel("Version 1.0");
        ver.setFont(new Font("Arial",Font.PLAIN,13));
        ver.setForeground(C_ACCENT);
        g.gridy=2; g.insets=new Insets(0,0,24,0); card.add(ver,g);

        String[][] info = {
            {"\uD83D\uDCBB", "Language",  "Java SE (JDK 8+)"},
            {"\uD83C\uDFA8", "GUI",       "Java Swing (JFrame)"},
            {"\uD83D\uDDC4\uFE0F", "Database",  "Oracle 21c XE — XEPDB1"},
            {"\uD83D\uDD0C", "Driver",    "ojdbc8.jar (Oracle JDBC)"},
            {"\uD83D\uDEE0\uFE0F", "IDE",       "NetBeans IDE"},
        };
        for (int i = 0; i < info.length; i++) {
            JLabel l = new JLabel(info[i][0]+"  "+info[i][1]+":  "+info[i][2]);
            l.setFont(new Font("Arial",Font.PLAIN,13));
            l.setForeground(C_MUTED);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            g.gridy=3+i; g.insets=new Insets(4,0,4,0); card.add(l,g);
        }

        tab.add(card, new GridBagConstraints());
        return tab;
    }

    // ── STATUS BAR ────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT,14,0));
        b.setPreferredSize(new Dimension(980,28));
        b.setBackground(C_STATUS);
        b.setBorder(BorderFactory.createMatteBorder(
                1,0,0,0,new Color(255,255,255,20)));
        JLabel l = new JLabel(
                "\u2699\uFE0F  Settings  |  User Management  |  DB Configuration");
        l.setFont(new Font("Arial",Font.PLAIN,11));
        l.setForeground(new Color(110,140,180));
        b.add(l); return b;
    }

    // ══════════════════════════════════════════════════════════
    // USER CRUD
    // ══════════════════════════════════════════════════════════
    private void addUser() {
        if (txtUsername.getText().trim().isEmpty() || txtNewPass.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and Password are required!", "Validation",
                    JOptionPane.WARNING_MESSAGE); return;
        }
        String sql = "INSERT INTO USERS VALUES(users_seq.NEXTVAL,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtUsername.getText().trim());
            ps.setString(2, txtNewPass.getText().trim());
            ps.setString(3, cmbRole.getSelectedItem().toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "\u2705  User added!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers(); clearUserFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        if (txtUserId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user first!",
                    "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        String sql = "UPDATE USERS SET username=?,role=? WHERE user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtUsername.getText().trim());
            ps.setString(2, cmbRole.getSelectedItem().toString());
            ps.setInt(3, Integer.parseInt(txtUserId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "\u2705  User updated!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers(); clearUserFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        if (txtUserId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user to delete!",
                    "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete user: " + txtUsername.getText() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM USERS WHERE user_id=?")) {
            ps.setInt(1, Integer.parseInt(txtUserId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "\u2705  User deleted!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers(); clearUserFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        userModel.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT user_id, username, role FROM USERS ORDER BY user_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) userModel.addRow(new Object[]{
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("role")
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearUserFields() {
        txtUserId.setText(""); txtUsername.setText("");
        txtNewPass.setText(""); cmbRole.setSelectedIndex(0);
        userTable.clearSelection();
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────
    private void changePassword() {
        String user    = txtCPUser.getText().trim();
        String oldPass = new String(txtCPOld.getPassword()).trim();
        String newPass = new String(txtCPNew.getPassword()).trim();
        String confirm = new String(txtCPConfirm.getPassword()).trim();

        if (user.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!",
                    "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this,
                    "New password and confirm password do not match!",
                    "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        if (newPass.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 4 characters!",
                    "Validation", JOptionPane.WARNING_MESSAGE); return;
        }

        // Verify old password first
        String checkSql  = "SELECT user_id FROM USERS WHERE username=? AND password=?";
        String updateSql = "UPDATE USERS SET password=? WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement check = c.prepareStatement(checkSql)) {
            check.setString(1, user); check.setString(2, oldPass);
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Current password is incorrect!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Update
            try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                ps.setString(1, newPass); ps.setString(2, user); ps.setString(3, oldPass);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this,
                    "\u2705  Password changed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            txtCPUser.setText(""); txtCPOld.setText("");
            txtCPNew.setText(""); txtCPConfirm.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── LOAD DB INFO ──────────────────────────────────────────
    private void loadDBInfo() {
        try (Connection c = DBConnection.getConnection()) {
            DatabaseMetaData meta = c.getMetaData();
            lblDBStatus.setText("\u2705  Connected");
            lblDBStatus.setForeground(C_GREEN);
            lblDBUrl.setText(meta.getURL());
            lblDBUrl.setForeground(C_WHITE);
            lblDBUser.setText(meta.getUserName());
            lblDBUser.setForeground(C_WHITE);
            lblDBVersion.setText(meta.getDatabaseProductName()
                    + " " + meta.getDatabaseProductVersion());
            lblDBVersion.setForeground(C_WHITE);
        } catch (SQLException e) {
            lblDBStatus.setText("\u274C  Disconnected — " + e.getMessage());
            lblDBStatus.setForeground(C_RED);
        }
    }

    // ── HELPERS ───────────────────────────────────────────────
    private JTextField addField(JPanel p, GridBagConstraints g,
                                 String label, int row, int col, boolean ro) {
        g.gridx=col; g.gridy=row; g.weightx=0;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial",Font.BOLD,11));
        l.setForeground(C_MUTED);
        p.add(l,g);
        g.gridx=col+1; g.weightx=1;
        JTextField t = new JTextField();
        t.setFont(new Font("Arial",Font.PLAIN,13));
        t.setBackground(ro?new Color(18,22,32):C_FIELD_BG);
        t.setForeground(ro?new Color(80,100,140):C_WHITE);
        t.setCaretColor(C_WHITE); t.setEditable(!ro);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ro?new Color(40,55,80):C_FIELD_BOR,1),
            new EmptyBorder(6,10,6,10)));
        if (!ro) t.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_FOC,2),
                    new EmptyBorder(5,9,5,9)));}
            public void focusLost(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_BOR,1),
                    new EmptyBorder(6,10,6,10)));}
        });
        p.add(t,g); return t;
    }

    private void styleField(JComponent c) {
        c.setPreferredSize(new Dimension(380, 42));
        c.setFont(new Font("Arial",Font.PLAIN,14));
        c.setBackground(C_FIELD_BG); c.setForeground(C_WHITE);
        if (c instanceof JTextField) ((JTextField)c).setCaretColor(C_WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_FIELD_BOR,1),
            new EmptyBorder(6,12,6,12)));
        c.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_FOC,2),
                    new EmptyBorder(5,11,5,11)));}
            public void focusLost(FocusEvent e){
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_BOR,1),
                    new EmptyBorder(6,12,6,12)));}
        });
    }

    private JLabel makeFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial",Font.BOLD,12));
        l.setForeground(C_MUTED);
        return l;
    }

    private void styleTable(JTable t, Color thead) {
        t.setRowHeight(30);
        t.setFont(new Font("Arial",Font.PLAIN,13));
        t.setForeground(C_WHITE); t.setBackground(C_PANEL);
        t.setGridColor(new Color(50,70,110));
        t.setShowGrid(true); t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader h = t.getTableHeader();
        h.setBackground(thead); h.setForeground(Color.BLACK);
        h.setFont(new Font("Arial",Font.BOLD,12));
        h.setPreferredSize(new Dimension(0,36));
    }

    private JButton makeBtn(String text, Color bg, Color hover) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?hover:bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial",Font.BOLD,12)); b.setForeground(C_WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(130,36)); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
