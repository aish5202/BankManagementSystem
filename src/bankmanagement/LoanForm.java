package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * LoanForm.java
 * ──────────────────────────────────────────────────────────────
 * CRUD form for LOANS table (child of CUSTOMERS).
 * Orange / amber accent theme.
 * Apply loans, update status, delete, view all records.
 */
public class LoanForm extends JFrame {

    private static final Color C_NAVY      = new Color(10,  22,  40);
    private static final Color C_PANEL     = new Color(28,  20,  12);
    private static final Color C_HEADER    = new Color(191,  54,  12);
    private static final Color C_HEADER2   = new Color(230,  81,   0);
    private static final Color C_FIELD_BG  = new Color(45,  32,  15);
    private static final Color C_FIELD_BOR = new Color(160,  90,  20);
    private static final Color C_FIELD_FOC = new Color(255, 165,  30);
    private static final Color C_THEAD     = new Color(191,  54,  12);
    private static final Color C_SEL       = new Color(100,  40,   5);
    private static final Color C_ALT       = new Color(22,  16,   8);
    private static final Color C_WHITE     = Color.WHITE;
    private static final Color C_STATUS    = new Color(14,  10,   5);
    private static final Color C_APPROVED  = new Color(0,  190, 130);
    private static final Color C_PENDING   = new Color(255, 183,  30);
    private static final Color C_REJECTED  = new Color(220,  60,  60);
    private static final Color C_CLOSED    = new Color(130, 150, 180);

    private JTextField    txtLoanId, txtCustId, txtAmount;
    private JComboBox<String> cmbLoanType, cmbStatus;
    private JTable        table;
    private DefaultTableModel model;
    private JButton btnApply, btnUpdate, btnDelete, btnClear, btnRefresh;

    public LoanForm() {
        setTitle("Loan Management — BMS");
        setSize(980, 660);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(C_NAVY);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        loadData();
    }

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
        JLabel l = new JLabel("🏦   Loan Management");
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setForeground(C_WHITE);
        h.add(l);
        return h;
    }

    private JPanel buildCenter() {
        JPanel c = new JPanel(new BorderLayout(0, 16));
        c.setBackground(C_NAVY);
        c.setBorder(new EmptyBorder(16, 20, 0, 20));
        c.add(buildFormPanel(),  BorderLayout.NORTH);
        c.add(buildTablePanel(), BorderLayout.CENTER);
        return c;
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(C_NAVY);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160,90,20,100), 1),
            new EmptyBorder(16, 20, 16, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel st = new JLabel("▸  Loan Application Details");
        st.setFont(new Font("Arial", Font.BOLD, 12));
        st.setForeground(new Color(255, 204, 128));
        g.gridx=0; g.gridy=0; g.gridwidth=6;
        form.add(st, g); g.gridwidth=1;

        // Row 1
        txtLoanId = addField(form, g, "Loan ID (Auto)",   1, 0, true);
        txtCustId = addField(form, g, "Customer ID ✱",    1, 2, false);
        txtAmount = addField(form, g, "Loan Amount (₹) ✱",1, 4, false);

        // Row 2: Loan Type combo
        g.gridx=0; g.gridy=2; g.weightx=0;
        JLabel lt = new JLabel("Loan Type ✱");
        lt.setFont(new Font("Arial", Font.BOLD, 11));
        lt.setForeground(new Color(255, 204, 128));
        form.add(lt, g);
        g.gridx=1; g.weightx=1;
        cmbLoanType = new JComboBox<>(new String[]{
            "Home","Car","Personal","Education","Business"});
        styleCombo(cmbLoanType);
        form.add(cmbLoanType, g);

        // Row 2: Status combo
        g.gridx=2; g.weightx=0;
        JLabel ls = new JLabel("Status");
        ls.setFont(new Font("Arial", Font.BOLD, 11));
        ls.setForeground(new Color(255, 204, 128));
        form.add(ls, g);
        g.gridx=3; g.weightx=1;
        cmbStatus = new JComboBox<>(new String[]{
            "Pending","Approved","Rejected","Closed"});
        styleCombo(cmbStatus);
        form.add(cmbStatus, g);

        wrapper.add(form,           BorderLayout.CENTER);
        wrapper.add(buildButtonRow(), BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row.setBackground(C_NAVY);
        btnApply   = makeBtn("📝  Apply Loan",   C_HEADER,             C_HEADER2);
        btnApply.setPreferredSize(new Dimension(148, 36));
        btnUpdate  = makeBtn("✏️  Update Status", new Color(21,101,192), new Color(30,136,229));
        btnUpdate.setPreferredSize(new Dimension(148, 36));
        btnDelete  = makeBtn("🗑  Delete",        new Color(183,28,28),  new Color(211,47,47));
        btnClear   = makeBtn("🔄  Clear",         new Color(69,90,100),  new Color(96,125,139));
        btnRefresh = makeBtn("↻  Refresh",        new Color(74,20,140),  new Color(106,27,154));

        row.add(btnApply); row.add(btnUpdate); row.add(btnDelete);
        row.add(btnClear); row.add(btnRefresh);

        btnApply.addActionListener(e -> applyLoan());
        btnUpdate.addActionListener(e -> updateLoan());
        btnDelete.addActionListener(e -> deleteLoan());
        btnClear.addActionListener(e -> clearFields());
        btnRefresh.addActionListener(e -> loadData());
        return row;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_NAVY);
        JLabel t = new JLabel("▸  Loan Records");
        t.setFont(new Font("Arial", Font.BOLD, 12));
        t.setForeground(new Color(255, 204, 128));
        t.setBorder(new EmptyBorder(0, 4, 8, 0));
        p.add(t, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"Loan ID","Customer ID","Type","Amount (₹)","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_PANEL : C_ALT);
                    c.setForeground(C_WHITE);
                    if (col == 4) {
                        String s = model.getValueAt(row, 4).toString();
                        switch (s) {
                            case "Approved": c.setForeground(C_APPROVED); break;
                            case "Pending":  c.setForeground(C_PENDING);  break;
                            case "Rejected": c.setForeground(C_REJECTED); break;
                            case "Closed":   c.setForeground(C_CLOSED);   break;
                        }
                    }
                    if (col == 3) c.setForeground(new Color(255, 200, 100));
                } else { c.setBackground(C_SEL); c.setForeground(C_WHITE); }
                return c;
            }
        };
        styleTable();

        // Row click → fill form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                txtLoanId.setText(model.getValueAt(r, 0).toString());
                txtCustId.setText(model.getValueAt(r, 1).toString());
                cmbLoanType.setSelectedItem(model.getValueAt(r, 2).toString());
                txtAmount.setText(model.getValueAt(r, 3).toString()
                    .replace("₹ ", "").replace(",", ""));
                cmbStatus.setSelectedItem(model.getValueAt(r, 4).toString());
            }
        });

        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(C_PANEL);
        sc.setBorder(BorderFactory.createLineBorder(new Color(160,90,20,100), 1));
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStatusBar() {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        b.setPreferredSize(new Dimension(980, 28));
        b.setBackground(C_STATUS);
        b.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(255,255,255,20)));
        JLabel l = new JLabel("🏦  Loan records loaded  |  FK: customer_id → CUSTOMERS");
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(new Color(180, 130, 80));
        b.add(l); return b;
    }

    // ══════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ══════════════════════════════════════════════════════════
    private void applyLoan() {
        if (txtCustId.getText().trim().isEmpty() || txtAmount.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Customer ID and Amount are required!", "Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO LOANS VALUES(loans_seq.NEXTVAL,?,?,?,?)")) {
            ps.setInt(1, Integer.parseInt(txtCustId.getText().trim()));
            ps.setString(2, cmbLoanType.getSelectedItem().toString());
            ps.setDouble(3, Double.parseDouble(txtAmount.getText().trim()));
            ps.setString(4, cmbStatus.getSelectedItem().toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅  Loan application submitted!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLoan() {
        if (txtLoanId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a loan to update!",
                "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE LOANS SET loan_type=?,loan_amount=?,status=? WHERE loan_id=?")) {
            ps.setString(1, cmbLoanType.getSelectedItem().toString());
            ps.setDouble(2, Double.parseDouble(txtAmount.getText().trim()));
            ps.setString(3, cmbStatus.getSelectedItem().toString());
            ps.setInt(4, Integer.parseInt(txtLoanId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅  Loan updated!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLoan() {
        if (txtLoanId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a loan to delete!",
                "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
            "Delete Loan ID: " + txtLoanId.getText() + "?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM LOANS WHERE loan_id=?")) {
            ps.setInt(1, Integer.parseInt(txtLoanId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅  Loan deleted!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT * FROM LOANS ORDER BY loan_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) model.addRow(new Object[]{
                rs.getInt("loan_id"), rs.getInt("customer_id"),
                rs.getString("loan_type"),
                String.format("₹ %,.2f", rs.getDouble("loan_amount")),
                rs.getString("status")
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtLoanId.setText(""); txtCustId.setText(""); txtAmount.setText("");
        cmbLoanType.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    // Helpers ──────────────────────────────────────────────────
    private JTextField addField(JPanel p, GridBagConstraints g,
                                 String label, int row, int col, boolean ro) {
        g.gridx=col; g.gridy=row; g.weightx=0;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(new Color(255, 204, 128));
        p.add(l, g);
        g.gridx=col+1; g.weightx=1;
        JTextField t = new JTextField();
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setBackground(ro ? new Color(25,18,8) : C_FIELD_BG);
        t.setForeground(ro ? new Color(120,90,50) : C_WHITE);
        t.setCaretColor(C_WHITE); t.setEditable(!ro);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ro?new Color(60,45,20):C_FIELD_BOR,1),
            new EmptyBorder(6,10,6,10)));
        if (!ro) t.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_FOC,2),new EmptyBorder(5,9,5,9)));}
            public void focusLost(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_BOR,1),new EmptyBorder(6,10,6,10)));}
        });
        p.add(t, g); return t;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Arial", Font.PLAIN, 13));
        cb.setBackground(C_FIELD_BG); cb.setForeground(C_WHITE);
        cb.setBorder(BorderFactory.createLineBorder(C_FIELD_BOR, 1));
    }

    private void styleTable() {
        table.setRowHeight(32); table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(C_WHITE); table.setBackground(C_PANEL);
        table.setGridColor(new Color(100,55,10)); table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader h = table.getTableHeader();
        h.setBackground(C_THEAD); h.setForeground(Color.BLACK);
        h.setFont(new Font("Arial", Font.BOLD, 12));
        h.setPreferredSize(new Dimension(0, 38));
    }

    private JButton makeBtn(String text, Color bg, Color hover) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
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
