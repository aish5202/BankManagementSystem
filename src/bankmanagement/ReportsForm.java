/*
 * Bank Management System
 * ReportsForm.java - Reports & Analytics window
 */
package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ReportsForm extends JFrame {

    private static final Color C_NAVY      = new Color(10,  22,  40);
    private static final Color C_PANEL     = new Color(18,  15,  40);
    private static final Color C_HEADER    = new Color(74,  20, 140);
    private static final Color C_HEADER2   = new Color(106, 27, 154);
    private static final Color C_CARD_BG   = new Color(28,  22,  55);
    private static final Color C_CARD_BOR  = new Color(80,  40, 140);
    private static final Color C_FIELD_BG  = new Color(38,  28,  70);
    private static final Color C_FIELD_BOR = new Color(100, 60, 170);
    private static final Color C_THEAD     = new Color(74,  20, 140);
    private static final Color C_SEL       = new Color(55,  15, 100);
    private static final Color C_ALT       = new Color(15,  12,  32);
    private static final Color C_WHITE     = Color.WHITE;
    private static final Color C_MUTED     = new Color(160, 130, 210);
    private static final Color C_STATUS    = new Color(12,  10,  28);
    private static final Color C_ACCENT    = new Color(179, 136, 255);
    private static final Color C_GREEN     = new Color(0,   200, 140);
    private static final Color C_YELLOW    = new Color(255, 200,  50);
    private static final Color C_RED       = new Color(220,  70,  70);
    private static final Color C_BLUE      = new Color(100, 180, 255);

    // Summary stat labels
    private JLabel lblTotalCustomers, lblTotalAccounts, lblTotalBalance;
    private JLabel lblTotalLoans,     lblTotalTxns,     lblApprovedLoans;

    // Report table
    private JTable            table;
    private DefaultTableModel model;

    // Report selector
    private JComboBox<String> cmbReport;
    private JButton           btnGenerate, btnExportCSV;

    public ReportsForm() {
        setTitle("Reports & Analytics — BMS");
        setSize(1050, 720);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_NAVY);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        loadSummaryStats();
        generateReport();
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
        h.setPreferredSize(new Dimension(1050, 58));
        h.setLayout(new FlowLayout(FlowLayout.LEFT, 22, 0));
        JLabel l = new JLabel("\uD83D\uDCCA   Reports & Analytics");
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setForeground(C_WHITE);
        h.add(l);
        return h;
    }

    // ── CENTER ────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel c = new JPanel(new BorderLayout(0, 16));
        c.setBackground(C_NAVY);
        c.setBorder(new EmptyBorder(16, 20, 0, 20));
        c.add(buildSummaryCards(), BorderLayout.NORTH);
        c.add(buildReportSection(), BorderLayout.CENTER);
        return c;
    }

    // ── 6 SUMMARY STAT CARDS ─────────────────────────────────
    private JPanel buildSummaryCards() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setBackground(C_NAVY);

        JLabel title = new JLabel("\u25B8  Live Summary");
        title.setFont(new Font("Arial", Font.BOLD, 12));
        title.setForeground(C_ACCENT);
        title.setBorder(new EmptyBorder(0,2,8,0));
        wrap.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 6, 14, 0));
        grid.setBackground(C_NAVY);

        lblTotalCustomers = new JLabel("—");
        lblTotalAccounts  = new JLabel("—");
        lblTotalBalance   = new JLabel("—");
        lblTotalLoans     = new JLabel("—");
        lblTotalTxns      = new JLabel("—");
        lblApprovedLoans  = new JLabel("—");

        grid.add(makeStatCard("\uD83D\uDC65", "Customers",    lblTotalCustomers, C_BLUE));
        grid.add(makeStatCard("\uD83C\uDFE7", "Accounts",     lblTotalAccounts,  C_GREEN));
        grid.add(makeStatCard("\uD83D\uDCB0", "Total Balance",lblTotalBalance,   C_YELLOW));
        grid.add(makeStatCard("\uD83D\uDCCA", "Transactions",  lblTotalTxns,     C_ACCENT));
        grid.add(makeStatCard("\uD83C\uDFE6", "Loans",        lblTotalLoans,     C_RED));
        grid.add(makeStatCard("\u2705",       "Approved Loans",lblApprovedLoans, C_GREEN));

        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel makeStatCard(String icon, String title, JLabel valLabel, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
                // top accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(
                accent.getRed(), accent.getGreen(), accent.getBlue(), 70), 1, true),
            new EmptyBorder(14, 14, 14, 14)));
        card.setPreferredSize(new Dimension(0, 100));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.anchor = GridBagConstraints.CENTER;

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        g.gridy = 0; g.insets = new Insets(0,0,6,0);
        card.add(iconLbl, g);

        valLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valLabel.setForeground(accent);
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1; g.insets = new Insets(0,0,4,0);
        card.add(valLabel, g);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        titleLbl.setForeground(C_MUTED);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2; g.insets = new Insets(0,0,0,0);
        card.add(titleLbl, g);

        return card;
    }

    // ── REPORT SELECTOR + TABLE ───────────────────────────────
    private JPanel buildReportSection() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setBackground(C_NAVY);

        // Selector row
        JPanel selRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selRow.setBackground(C_NAVY);

        JLabel lbl = new JLabel("Report:");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(C_ACCENT);

        cmbReport = new JComboBox<>(new String[]{
            "All Customers",
            "All Accounts with Balance",
            "Recent Transactions (Last 20)",
            "All Loans & Status",
            "Customers with Multiple Accounts",
            "High Balance Accounts (> \u20B9 1,00,000)",
            "Pending Loans",
            "Approved Loans"
        });
        cmbReport.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbReport.setBackground(C_FIELD_BG);
        cmbReport.setForeground(C_WHITE);
        cmbReport.setPreferredSize(new Dimension(300, 36));
        cmbReport.setBorder(BorderFactory.createLineBorder(C_FIELD_BOR, 1));

        btnGenerate = makeBtn("\u25B6  Generate", C_HEADER, C_HEADER2);
        btnGenerate.setPreferredSize(new Dimension(130, 36));

        btnExportCSV = makeBtn("\uD83D\uDCBE  Export CSV", new Color(0,121,107), new Color(0,150,136));
        btnExportCSV.setPreferredSize(new Dimension(130, 36));

        JButton btnRefresh = makeBtn("\u21BB  Refresh Stats", new Color(21,101,192), new Color(30,136,229));
        btnRefresh.setPreferredSize(new Dimension(145, 36));

        selRow.add(lbl); selRow.add(cmbReport);
        selRow.add(btnGenerate); selRow.add(btnExportCSV); selRow.add(btnRefresh);

        // Table
        model = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_CARD_BG : C_ALT);
                    c.setForeground(C_WHITE);
                } else { c.setBackground(C_SEL); c.setForeground(C_WHITE); }
                return c;
            }
        };
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(C_CARD_BG);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(100, 60, 170, 100), 1));

        JLabel tLabel = new JLabel("\u25B8  Report Output");
        tLabel.setFont(new Font("Arial", Font.BOLD, 12));
        tLabel.setForeground(C_ACCENT);
        tLabel.setBorder(new EmptyBorder(0,2,8,0));

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(C_NAVY);
        tableWrap.add(tLabel, BorderLayout.NORTH);
        tableWrap.add(scroll, BorderLayout.CENTER);

        wrap.add(selRow,    BorderLayout.NORTH);
        wrap.add(tableWrap, BorderLayout.CENTER);

        btnGenerate.addActionListener(e  -> generateReport());
        btnRefresh.addActionListener(e   -> loadSummaryStats());
        btnExportCSV.addActionListener(e -> exportToCSV());

        return wrap;
    }

    // ── STATUS BAR ────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        b.setPreferredSize(new Dimension(1050, 28));
        b.setBackground(C_STATUS);
        b.setBorder(BorderFactory.createMatteBorder(
                1,0,0,0,new Color(255,255,255,20)));
        JLabel l = new JLabel(
                "\uD83D\uDCCA  Live data from Oracle Database  |  Select a report and click Generate");
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(new Color(160, 120, 220));
        b.add(l); return b;
    }

    // ══════════════════════════════════════════════════════════
    // LOAD SUMMARY STATS
    // ══════════════════════════════════════════════════════════
    private void loadSummaryStats() {
        try (Connection c = DBConnection.getConnection()) {
            lblTotalCustomers.setText(queryScalar(c,
                    "SELECT COUNT(*) FROM CUSTOMERS"));
            lblTotalAccounts.setText(queryScalar(c,
                    "SELECT COUNT(*) FROM ACCOUNTS"));
            lblTotalBalance.setText("\u20B9" + queryScalar(c,
                    "SELECT NVL(SUM(balance),0) FROM ACCOUNTS"));
            lblTotalTxns.setText(queryScalar(c,
                    "SELECT COUNT(*) FROM TRANSACTIONS"));
            lblTotalLoans.setText(queryScalar(c,
                    "SELECT COUNT(*) FROM LOANS"));
            lblApprovedLoans.setText(queryScalar(c,
                    "SELECT COUNT(*) FROM LOANS WHERE status='Approved'"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Stats error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String queryScalar(Connection c, String sql) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : "0";
        }
    }

    // ══════════════════════════════════════════════════════════
    // GENERATE REPORT
    // ══════════════════════════════════════════════════════════
    private void generateReport() {
        int sel = cmbReport.getSelectedIndex();
        String sql;
        String[] cols;

        switch (sel) {
            case 0:  // All Customers
                sql  = "SELECT customer_id, name, phone, email, address FROM CUSTOMERS ORDER BY customer_id";
                cols = new String[]{"ID","Name","Phone","Email","Address"};
                break;
            case 1:  // All Accounts
                sql  = "SELECT a.account_no, c.name, a.account_type, a.balance "
                     + "FROM ACCOUNTS a JOIN CUSTOMERS c ON a.customer_id=c.customer_id "
                     + "ORDER BY a.balance DESC";
                cols = new String[]{"Account No","Customer Name","Type","Balance (\u20B9)"};
                break;
            case 2:  // Recent Transactions
                sql  = "SELECT t.transaction_id, a.account_no, c.name, t.type, t.amount, t.transaction_date "
                     + "FROM TRANSACTIONS t "
                     + "JOIN ACCOUNTS a ON t.account_no=a.account_no "
                     + "JOIN CUSTOMERS c ON a.customer_id=c.customer_id "
                     + "ORDER BY t.transaction_date DESC FETCH FIRST 20 ROWS ONLY";
                cols = new String[]{"Txn ID","Account","Customer","Type","Amount (\u20B9)","Date"};
                break;
            case 3:  // All Loans
                sql  = "SELECT l.loan_id, c.name, l.loan_type, l.loan_amount, l.status "
                     + "FROM LOANS l JOIN CUSTOMERS c ON l.customer_id=c.customer_id "
                     + "ORDER BY l.loan_id";
                cols = new String[]{"Loan ID","Customer","Type","Amount (\u20B9)","Status"};
                break;
            case 4:  // Customers with multiple accounts
                sql  = "SELECT c.customer_id, c.name, c.phone, COUNT(a.account_no) AS accounts "
                     + "FROM CUSTOMERS c JOIN ACCOUNTS a ON c.customer_id=a.customer_id "
                     + "GROUP BY c.customer_id, c.name, c.phone HAVING COUNT(a.account_no) > 1 "
                     + "ORDER BY accounts DESC";
                cols = new String[]{"ID","Name","Phone","No. of Accounts"};
                break;
            case 5:  // High balance accounts
                sql  = "SELECT a.account_no, c.name, a.account_type, a.balance "
                     + "FROM ACCOUNTS a JOIN CUSTOMERS c ON a.customer_id=c.customer_id "
                     + "WHERE a.balance > 100000 ORDER BY a.balance DESC";
                cols = new String[]{"Account No","Customer","Type","Balance (\u20B9)"};
                break;
            case 6:  // Pending loans
                sql  = "SELECT l.loan_id, c.name, c.phone, l.loan_type, l.loan_amount "
                     + "FROM LOANS l JOIN CUSTOMERS c ON l.customer_id=c.customer_id "
                     + "WHERE l.status='Pending' ORDER BY l.loan_id";
                cols = new String[]{"Loan ID","Customer","Phone","Type","Amount (\u20B9)"};
                break;
            default:  // Approved loans
                sql  = "SELECT l.loan_id, c.name, l.loan_type, l.loan_amount, l.status "
                     + "FROM LOANS l JOIN CUSTOMERS c ON l.customer_id=c.customer_id "
                     + "WHERE l.status='Approved' ORDER BY l.loan_id";
                cols = new String[]{"Loan ID","Customer","Type","Amount (\u20B9)","Status"};
                break;
        }

        model.setRowCount(0);
        model.setColumnCount(0);
        for (String col : cols) model.addColumn(col);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                Object[] row = new Object[cols.length];
                for (int i = 0; i < cols.length; i++)
                    row[i] = rs.getObject(i + 1);
                model.addRow(row);
                count++;
            }
            JOptionPane.showMessageDialog(this,
                    "\u2705  Report generated: " + count + " record(s) found.",
                    "Report Ready", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════
    // EXPORT TO CSV
    // ══════════════════════════════════════════════════════════
    private void exportToCSV() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Generate a report first before exporting!",
                    "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("BMS_Report.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (java.io.PrintWriter pw =
                new java.io.PrintWriter(new java.io.FileWriter(fc.getSelectedFile()))) {

            // Header row
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < model.getColumnCount(); c++) {
                if (c > 0) sb.append(",");
                sb.append(model.getColumnName(c));
            }
            pw.println(sb);

            // Data rows
            for (int r = 0; r < model.getRowCount(); r++) {
                sb = new StringBuilder();
                for (int c = 0; c < model.getColumnCount(); c++) {
                    if (c > 0) sb.append(",");
                    Object val = model.getValueAt(r, c);
                    sb.append(val == null ? "" : val.toString());
                }
                pw.println(sb);
            }

            JOptionPane.showMessageDialog(this,
                    "\u2705  CSV exported successfully!\n" + fc.getSelectedFile().getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);

        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── HELPERS ───────────────────────────────────────────────
    private void styleTable() {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(C_WHITE);
        table.setBackground(C_CARD_BG);
        table.setGridColor(new Color(70, 35, 120));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JTableHeader h = table.getTableHeader();
        h.setBackground(C_THEAD);
        h.setForeground(Color.BLACK);
        h.setFont(new Font("Arial", Font.BOLD, 12));
        h.setPreferredSize(new Dimension(0, 38));
    }

    private JButton makeBtn(String text, Color bg, Color hover) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(C_WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(130, 36));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
