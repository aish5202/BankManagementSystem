/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Login.java
 * ──────────────────────────────────────────────────────────────
 * Two-panel login window:
 *   LEFT  → dark navy branding panel with bank icon + info
 *   RIGHT → white/dark form panel with username + password
 * Matches the dark navy (#0a1628) design from the UI guide.
 */

public class Login extends JFrame {
    
    // ── Colours matching the UI guide ─────────────────────────
    private static final Color C_NAVY       = new Color(13,  71, 161);   // left panel
    private static final Color C_NAVY_DARK  = new Color( 8,  40,  90);   // left panel bottom
    private static final Color C_ACCENT     = new Color( 0, 180, 216);   // accent teal
    private static final Color C_FORM_BG    = new Color(20,  30,  48);   // right panel bg
    private static final Color C_FIELD_BG   = new Color(30,  45,  71);   // input background
    private static final Color C_FIELD_BOR  = new Color(50,  80, 130);   // input border
    private static final Color C_FIELD_FOCUS= new Color( 0, 180, 216);   // focused border
    private static final Color C_WHITE      = Color.WHITE;
    private static final Color C_MUTED      = new Color(160, 180, 210);
    private static final Color C_BTN        = new Color(21, 101, 192);
    private static final Color C_BTN_HOVER  = new Color(30, 136, 229);
    private static final Color C_STATUS_BG  = new Color(15,  25,  45);

    private JTextField     txtUser;
    private JPasswordField txtPass;
    private JButton        btnLogin, btnCancel;
    private JLabel         lblStatus;
    /**
     * Creates new form Login
     */
    public Login() {
        setTitle("Bank Management System — Login");
        setSize(820, 520);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_FORM_BG);

        add(buildLeftPanel(),   BorderLayout.WEST);
        add(buildRightPanel(),  BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnLogin);
    }

    // ── LEFT PANEL ────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, C_NAVY, 0, getHeight(), C_NAVY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-50, -50, 240, 240);
                g2.fillOval(getWidth() - 90, getHeight() - 90, 200, 200);
            }
        };
        panel.setPreferredSize(new Dimension(310, 520));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.anchor = GridBagConstraints.CENTER;

        JLabel icon = new JLabel("\uD83C\uDFE6");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 0; g.insets = new Insets(0, 20, 10, 20);
        panel.add(icon, g);

        JLabel t1 = new JLabel("Bank Management");
        t1.setFont(new Font("Arial", Font.BOLD, 20));
        t1.setForeground(C_WHITE); t1.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1; g.insets = new Insets(0, 20, 2, 20);
        panel.add(t1, g);

        JLabel t2 = new JLabel("System");
        t2.setFont(new Font("Arial", Font.BOLD, 20));
        t2.setForeground(C_WHITE); t2.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2; g.insets = new Insets(0, 20, 6, 20);
        panel.add(t2, g);

        JLabel sub = new JLabel("Secure Banking Portal");
        sub.setFont(new Font("Arial", Font.ITALIC, 12));
        sub.setForeground(new Color(187, 222, 251));
        g.gridy = 3; g.insets = new Insets(0, 20, 30, 20);
        panel.add(sub, g);

        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(220, 1));
        sep.setForeground(new Color(255, 255, 255, 50));
        g.gridy = 4; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 20, 18, 20);
        panel.add(sep, g); g.fill = GridBagConstraints.NONE;

        String[] hints = {"Default Credentials", "admin / admin123", "staff1 / staff123"};
        Color[]  cols  = {new Color(255,255,255,90), new Color(255,255,255,140), new Color(255,255,255,140)};
        Font[]   fonts = {new Font("Arial",Font.BOLD,11), new Font("Arial",Font.PLAIN,11), new Font("Arial",Font.PLAIN,11)};
        for (int i = 0; i < hints.length; i++) {
            JLabel l = new JLabel(hints[i]);
            l.setFont(fonts[i]); l.setForeground(cols[i]);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            g.gridy = 5 + i; g.insets = new Insets(2, 20, 2, 20);
            panel.add(l, g);
        }

        JLabel badge = new JLabel("  Powered by Oracle Database  ");
        badge.setFont(new Font("Arial", Font.PLAIN, 10));
        badge.setForeground(C_ACCENT); badge.setOpaque(true);
        badge.setBackground(new Color(0, 180, 216, 20));
        badge.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 216, 70), 1));
        g.gridy = 8; g.insets = new Insets(30, 20, 0, 20);
        panel.add(badge, g);

        return panel;
    }

    // ── RIGHT PANEL ───────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(C_FORM_BG);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1;

        JLabel welcome = new JLabel("Welcome Back \uD83D\uDC4B");
        welcome.setFont(new Font("Arial", Font.BOLD, 26));
        welcome.setForeground(C_WHITE);
        g.gridy = 0; g.insets = new Insets(0, 0, 6, 0);
        panel.add(welcome, g);

        JLabel sub = new JLabel("Sign in to access the banking portal");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(C_MUTED);
        g.gridy = 1; g.insets = new Insets(0, 0, 34, 0);
        panel.add(sub, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 6, 0);
        panel.add(makeFormLabel("Username"), g);

        txtUser = new JTextField("admin");
        styleField(txtUser);
        g.gridy = 3; g.insets = new Insets(0, 0, 18, 0);
        panel.add(txtUser, g);

        g.gridy = 4; g.insets = new Insets(0, 0, 6, 0);
        panel.add(makeFormLabel("Password"), g);

        txtPass = new JPasswordField();
        styleField(txtPass);
        g.gridy = 5; g.insets = new Insets(0, 0, 30, 0);
        panel.add(txtPass, g);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnLogin  = makeButton("\uD83D\uDD10  LOGIN", C_BTN, C_BTN_HOVER);
        btnCancel = makeButton("\u2715  Cancel",
                new Color(55, 75, 110), new Color(75, 100, 145));
        btnRow.add(btnLogin); btnRow.add(btnCancel);
        g.gridy = 6; g.insets = new Insets(0, 0, 22, 0);
        panel.add(btnRow, g);

        lblStatus = new JLabel(
                "\u2705  Connected to Oracle XEPDB1 \u2014 localhost:1521");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(0, 200, 120));
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(0, 90, 45, 40));
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 160, 80, 70), 1),
                new EmptyBorder(8, 12, 8, 12)));
        g.gridy = 7; g.insets = new Insets(0, 0, 0, 0);
        panel.add(lblStatus, g);

        btnLogin.addActionListener(e  -> doLogin());
        btnCancel.addActionListener(e -> System.exit(0));

        return panel;
    }

    // ── STATUS BAR ────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        bar.setPreferredSize(new Dimension(820, 30));
        bar.setBackground(C_STATUS_BG);
        bar.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, new Color(255, 255, 255, 25)));
        JLabel l = new JLabel(
                "\uD83D\uDD12  Secure Login  |  Oracle Database  |  BMS v1.0");
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(new Color(110, 140, 185));
        bar.add(l);
        return bar;
    }

    // ── LOGIN LOGIC ───────────────────────────────────────────
    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT role FROM USERS WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                new Dashboard(user, role).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                txtPass.setText("");
                txtPass.requestFocus();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "DB Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── HELPERS ───────────────────────────────────────────────
    private JLabel makeFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(new Color(160, 190, 230));
        return l;
    }

    private void styleField(JComponent c) {
        c.setPreferredSize(new Dimension(340, 44));
        c.setFont(new Font("Arial", Font.PLAIN, 14));
        c.setBackground(C_FIELD_BG);
        c.setForeground(C_WHITE);
        if (c instanceof JTextField) ((JTextField) c).setCaretColor(C_WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_FIELD_BOR, 2),
                new EmptyBorder(6, 12, 6, 12)));
        c.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_FIELD_FOCUS, 2),
                        new EmptyBorder(6, 12, 6, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_FIELD_BOR, 2),
                        new EmptyBorder(6, 12, 6, 12)));
            }
        });
    }

    private JButton makeButton(String text, Color bg, Color hover) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(C_WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(160, 44));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }   catch (Exception ex) {
            ex.printStackTrace();
            }
        //</editor-fold>

        /* Create and display the form */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
            SwingUtilities.invokeLater(() -> new Login().setVisible(true));
        }
    }

    // Variables declaration - do not modify                     
    // End of variables declaration                   

