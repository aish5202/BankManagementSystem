package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * CustomerForm.java
 * ──────────────────────────────────────────────────────────────
 * Full CRUD form for CUSTOMERS table.
 * Dark navy theme with blue header and styled JTable.
 * Click any table row to populate the form fields.
 */
public class CustomerForm extends JFrame {

    // ── Colours ───────────────────────────────────────────────
    private static final Color C_NAVY       = new Color(10,  22,  40);
    private static final Color C_PANEL      = new Color(20,  35,  60);
    private static final Color C_HEADER     = new Color(13,  71, 161);
    private static final Color C_HEADER2    = new Color(21, 101, 192);
    private static final Color C_FIELD_BG   = new Color(30,  45,  71);
    private static final Color C_FIELD_BOR  = new Color(50,  80, 130);
    private static final Color C_FIELD_FOC  = new Color(0,  180, 216);
    private static final Color C_TABLE_HEAD = new Color(21, 101, 192);
    private static final Color C_TABLE_SEL  = new Color(0,  70, 130);
    private static final Color C_TABLE_ALT  = new Color(18,  30,  50);
    private static final Color C_WHITE      = Color.WHITE;
    private static final Color C_MUTED      = new Color(130, 160, 200);
    private static final Color C_STATUS     = new Color(15,  25,  45);

    private JTextField txtId, txtName, txtPhone, txtEmail, txtAddress;
    private JTable            table;
    private DefaultTableModel model;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;

    public CustomerForm() {
        setTitle("Customer Management — BMS");
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

    // ══════════════════════════════════════════════════════════
    // HEADER
    // ══════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel h = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, C_HEADER,
                        getWidth(), 0, C_HEADER2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        h.setPreferredSize(new Dimension(980, 58));
        h.setLayout(new FlowLayout(FlowLayout.LEFT, 22, 0));
        JLabel lbl = new JLabel("👥   Customer Management");
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        lbl.setForeground(C_WHITE);
        h.add(lbl);
        return h;
    }

    // ══════════════════════════════════════════════════════════
    // CENTER
    // ══════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(C_NAVY);
        center.setBorder(new EmptyBorder(16, 20, 0, 20));

        center.add(buildFormPanel(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        return center;
    }

    // ── INPUT FORM ────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(C_NAVY);

        // Form box
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 100, 180, 120), 1),
            new EmptyBorder(18, 20, 18, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Section title
        JLabel sTitle = new JLabel("▸  Customer Details");
        sTitle.setFont(new Font("Arial", Font.BOLD, 12));
        sTitle.setForeground(new Color(144, 202, 249));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 6;
        form.add(sTitle, g);
        g.gridwidth = 1;

        // Row 1
        txtId      = addField(form, g, "Customer ID (Auto)", 1, 0, true);
        txtName    = addField(form, g, "Full Name ✱",         1, 2, false);
        txtPhone   = addField(form, g, "Phone ✱",             1, 4, false);
        // Row 2
        txtEmail   = addField(form, g, "Email",               2, 0, false);
        txtAddress = addField(form, g, "Address",             2, 2, false);

        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buildButtonRow(), BorderLayout.SOUTH);
        return wrapper;
    }

    // ── BUTTON ROW ────────────────────────────────────────────
    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row.setBackground(C_NAVY);

        btnAdd     = makeBtn("➕  Add",     new Color(21, 101, 192), new Color(30, 136, 229));
        btnUpdate  = makeBtn("✏️  Update",  new Color(0,  121, 107), new Color(0,  150, 136));
        btnDelete  = makeBtn("🗑  Delete",  new Color(183, 28,  28), new Color(211,  47,  47));
        btnClear   = makeBtn("🔄  Clear",   new Color(69,  90, 100), new Color(96, 125, 139));
        btnRefresh = makeBtn("↻  Refresh", new Color(74,  20, 140), new Color(106,  27, 154));

        row.add(btnAdd); row.add(btnUpdate); row.add(btnDelete);
        row.add(btnClear); row.add(btnRefresh);

        btnAdd.addActionListener(e -> insertCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearFields());
        btnRefresh.addActionListener(e -> loadData());
        return row;
    }

    // ── TABLE PANEL ───────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_NAVY);

        JLabel tTitle = new JLabel("▸  Customer List");
        tTitle.setFont(new Font("Arial", Font.BOLD, 12));
        tTitle.setForeground(new Color(144, 202, 249));
        tTitle.setBorder(new EmptyBorder(0, 4, 8, 0));
        panel.add(tTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Phone", "Email", "Address"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_PANEL : C_TABLE_ALT);
                    c.setForeground(C_WHITE);
                } else {
                    c.setBackground(C_TABLE_SEL);
                    c.setForeground(C_WHITE);
                }
                return c;
            }
        };
        styleTable();

        // Row click → fill form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                txtId.setText(model.getValueAt(r, 0).toString());
                txtName.setText(model.getValueAt(r, 1).toString());
                txtPhone.setText(model.getValueAt(r, 2).toString());
                txtEmail.setText(model.getValueAt(r, 3).toString());
                txtAddress.setText(model.getValueAt(r, 4).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(C_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(50, 100, 180, 120), 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════
    // STATUS BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        bar.setPreferredSize(new Dimension(980, 28));
        bar.setBackground(C_STATUS);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(255, 255, 255, 20)));
        JLabel lbl = new JLabel("👥  Customer records loaded  |  Click a row to select");
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(new Color(120, 150, 190));
        bar.add(lbl);
        return bar;
    }

    // ══════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ══════════════════════════════════════════════════════════
    private void insertCustomer() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!", "Validation",
                    JOptionPane.WARNING_MESSAGE); return;
        }
        String sql = "INSERT INTO CUSTOMERS VALUES (customers_seq.NEXTVAL,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtPhone.getText().trim());
            ps.setString(3, txtEmail.getText().trim());
            ps.setString(4, txtAddress.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅  Customer added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a customer to update!", "Validation",
                    JOptionPane.WARNING_MESSAGE); return;
        }
        String sql = "UPDATE CUSTOMERS SET name=?,phone=?,email=?,address=? WHERE customer_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtPhone.getText().trim());
            ps.setString(3, txtEmail.getText().trim());
            ps.setString(4, txtAddress.getText().trim());
            ps.setInt(5, Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅  Customer updated!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete!", "Validation",
                    JOptionPane.WARNING_MESSAGE); return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
            "Delete customer ID " + txtId.getText() + "?\n"
            + "⚠ All linked accounts, transactions and loans will also be deleted!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM CUSTOMERS WHERE customer_id=?")) {
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅  Customer deleted!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM CUSTOMERS ORDER BY customer_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("customer_id"), rs.getString("name"),
                    rs.getString("phone"),    rs.getString("email"),
                    rs.getString("address")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtId.setText(""); txtName.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtAddress.setText("");
        table.clearSelection();
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════
    private JTextField addField(JPanel p, GridBagConstraints g,
                                 String label, int row, int col, boolean ro) {
        g.gridx = col; g.gridy = row; g.weightx = 0;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(new Color(160, 190, 230));
        p.add(l, g);

        g.gridx = col + 1; g.weightx = 1;
        JTextField t = new JTextField();
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setBackground(ro ? new Color(20, 30, 48) : C_FIELD_BG);
        t.setForeground(ro ? new Color(100, 130, 170) : C_WHITE);
        t.setCaretColor(C_WHITE);
        t.setEditable(!ro);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ro ? new Color(35, 55, 85) : C_FIELD_BOR, 1),
            new EmptyBorder(6, 10, 6, 10)));
        if (!ro) {
            t.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    t.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_FIELD_FOC, 2),
                        new EmptyBorder(5, 9, 5, 9)));
                }
                public void focusLost(FocusEvent e) {
                    t.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_FIELD_BOR, 1),
                        new EmptyBorder(6, 10, 6, 10)));
                }
            });
        }
        p.add(t, g);
        return t;
    }

    private void styleTable() {
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(C_WHITE);
        table.setBackground(C_PANEL);
        table.setGridColor(new Color(35, 55, 90));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(C_TABLE_HEAD);
        hdr.setForeground(Color.BLACK);
        hdr.setFont(new Font("Arial", Font.BOLD, 12));
        hdr.setPreferredSize(new Dimension(0, 38));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                new Color(255, 255, 255, 40)));
    }

    private JButton makeBtn(String text, Color bg, Color hover) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
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
