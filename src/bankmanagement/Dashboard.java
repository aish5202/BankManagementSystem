package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Dashboard.java
 * ──────────────────────────────────────────────────────────────
 * Main navigation hub after login.
 * Dark navy background (#0a1628) with 6 clickable module cards.
 * Top bar shows logged-in user + logout button.
 */
public class Dashboard extends JFrame {

    // ── Colours ───────────────────────────────────────────────
    private static final Color C_NAVY      = new Color(10,  22,  40);
    private static final Color C_TOPBAR    = new Color(13,  71, 161);
    private static final Color C_TOPBAR2   = new Color(21, 101, 192);
    private static final Color C_CARD_BG   = new Color(20,  35,  60);
    private static final Color C_CARD_BOR  = new Color(40,  65, 110);
    private static final Color C_CARD_HOV  = new Color(30,  50,  85);
    private static final Color C_STATUS    = new Color(15,  25,  45);
    private static final Color C_WHITE     = Color.WHITE;
    private static final Color C_MUTED     = new Color(130, 160, 200);
    private static final Color C_ACCENT    = new Color(0, 180, 216);

    private final String loggedUser;
    private final String userRole;

    public Dashboard(String user, String role) {
        this.loggedUser = user;
        this.userRole   = role;

        setTitle("Bank Management System — Dashboard");
        setSize(960, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_NAVY);

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════
    // TOP BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, C_TOPBAR,
                        getWidth(), 0, C_TOPBAR2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setPreferredSize(new Dimension(960, 62));
        bar.setLayout(new BorderLayout());
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Logo
        JLabel logo = new JLabel("🏦   BANK MANAGEMENT SYSTEM");
        logo.setFont(new Font("Arial", Font.BOLD, 20));
        logo.setForeground(C_WHITE);
        bar.add(logo, BorderLayout.WEST);

        // Right side: user info + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        JLabel userLbl = new JLabel("👤   " + loggedUser.toUpperCase()
                + "   |   " + userRole.toUpperCase());
        userLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        userLbl.setForeground(new Color(187, 222, 251));

        JButton btnLogout = new JButton("Logout") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? new Color(211, 47, 47)
                        : new Color(183, 28, 28));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogout.setForeground(C_WHITE);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(90, 34));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });

        right.add(userLbl);
        right.add(btnLogout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════
    // CENTER — welcome text + 6 module cards
    // ══════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(C_NAVY);
        outer.setBorder(new EmptyBorder(30, 40, 20, 40));

        // Welcome label
        JLabel welcome = new JLabel("Welcome to the Dashboard", SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 26));
        welcome.setForeground(C_ACCENT);
        welcome.setBorder(new EmptyBorder(0, 0, 28, 0));
        outer.add(welcome, BorderLayout.NORTH);

        // Card grid 2 × 3
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(C_NAVY);

        grid.add(makeCard("👥", "Customers",    "Manage customer records", new Color(21, 101, 192),
                () -> new CustomerForm().setVisible(true)));
        grid.add(makeCard("🏧", "Accounts",     "Create & view accounts",  new Color(0, 121, 107),
                () -> new AccountForm().setVisible(true)));
        grid.add(makeCard("💸", "Transactions", "Deposit & Withdraw",      new Color(130, 0, 80),
                () -> new TransactionForm().setVisible(true)));
        grid.add(makeCard("🏦", "Loans",        "Loan management",         new Color(230, 81, 0),
                () -> new LoanForm().setVisible(true)));
        grid.add(makeCard("📊", "Reports",      "View summaries",          new Color(74, 20, 140),
                () -> new ReportsForm().setVisible(true)));
        grid.add(makeCard("⚙️", "Settings",    "System configuration",    new Color(50, 65, 80),
                () -> new SettingsForm().setVisible(true)));

        outer.add(grid, BorderLayout.CENTER);
        return outer;
    }

    // ══════════════════════════════════════════════════════════
    // STATUS BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        bar.setPreferredSize(new Dimension(960, 30));
        bar.setBackground(C_STATUS);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(255, 255, 255, 20)));
        JLabel lbl = new JLabel(
                "✅   Connected to Oracle Database   |   User: "
                + loggedUser + "   |   Role: " + userRole);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(new Color(120, 150, 190));
        bar.add(lbl);
        return bar;
    }

    // ══════════════════════════════════════════════════════════
    // MODULE CARD builder
    // ══════════════════════════════════════════════════════════
    private JPanel makeCard(String icon, String title, String subtitle,
                             Color accentColor, Runnable action) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getClientProperty("hovered") != null
                        ? C_CARD_HOV : C_CARD_BG;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                // left accent stripe
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(
                accentColor.getRed(), accentColor.getGreen(),
                accentColor.getBlue(), 80), 1,  true),
            new EmptyBorder(22, 22, 22, 22)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.anchor = GridBagConstraints.CENTER;

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 0; g.insets = new Insets(0, 0, 12, 0);
        card.add(iconLbl, g);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        titleLbl.setForeground(new Color(
                Math.min(accentColor.getRed()   + 80, 255),
                Math.min(accentColor.getGreen() + 80, 255),
                Math.min(accentColor.getBlue()  + 80, 255)));
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1; g.insets = new Insets(0, 0, 5, 0);
        card.add(titleLbl, g);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        subLbl.setForeground(C_MUTED);
        subLbl.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2; g.insets = new Insets(0, 0, 0, 0);
        card.add(subLbl, g);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.run(); }
            public void mouseEntered(MouseEvent e) {
                card.putClientProperty("hovered", true);
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                card.putClientProperty("hovered", null);
                card.repaint();
            }
        });

        return card;
    }
}