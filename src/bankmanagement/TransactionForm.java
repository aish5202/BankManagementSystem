package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * TransactionForm.java
 * ──────────────────────────────────────────────────────────────
 * Deposit / Withdraw form with:
 *  • Live balance card (check balance button)
 *  • Atomic DB transaction (commit / rollback)
 *  • Transaction history JTable
 * Purple / magenta accent theme.
 */
public class TransactionForm extends JFrame {

    private static final Color C_NAVY      = new Color(10,  22,  40);
    private static final Color C_PANEL     = new Color(22,  20,  45);
    private static final Color C_HEADER    = new Color(136,  14,  79);
    private static final Color C_HEADER2   = new Color(173,  20, 87);
    private static final Color C_FIELD_BG  = new Color(35,  25,  60);
    private static final Color C_FIELD_BOR = new Color(120,  40, 100);
    private static final Color C_FIELD_FOC = new Color(206,  82, 175);
    private static final Color C_THEAD     = new Color(136,  14,  79);
    private static final Color C_SEL       = new Color( 80,  10,  55);
    private static final Color C_ALT       = new Color(18,  15,  38);
    private static final Color C_WHITE     = Color.WHITE;
    private static final Color C_STATUS    = new Color(12,  10,  28);
    private static final Color C_DEPOSIT   = new Color(0,  180, 130);
    private static final Color C_WITHDRAW  = new Color(220,  60,  60);
    private static final Color C_BALANCE   = new Color(0,  200, 170);
    private static final Color C_ACCENT    = new Color(0,  180, 216);

    private JTextField    txtAccNo, txtAmount;
    private JComboBox<String> cmbType;
    private JLabel        lblBalance, lblBalAmt;
    private JTable        table;
    private DefaultTableModel model;
    private JButton       btnCheck, btnSubmit, btnClear, btnRefresh;

    public TransactionForm() {
        setTitle("Transaction Management — BMS");
        setSize(980, 680);
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
        JLabel l = new JLabel("💸   Transaction Management");
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setForeground(C_WHITE);
        h.add(l);
        return h;
    }

    private JPanel buildCenter() {
        JPanel c = new JPanel(new BorderLayout(0, 14));
        c.setBackground(C_NAVY);
        c.setBorder(new EmptyBorder(16, 20, 0, 20));
        c.add(buildTopSection(),  BorderLayout.NORTH);
        c.add(buildTablePanel(),  BorderLayout.CENTER);
        return c;
    }

    // ── BALANCE CARD + FORM ───────────────────────────────────
    private JPanel buildTopSection() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setBackground(C_NAVY);

        // Balance card
        JPanel balCard = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(0,105,92),
                        getWidth(),getHeight(),new Color(0,77,64)));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
                g2.dispose();
            }
        };
        balCard.setOpaque(false);
        balCard.setLayout(new BorderLayout());
        balCard.setBorder(new EmptyBorder(16,20,16,20));
        balCard.setPreferredSize(new Dimension(0,80));

        JPanel balLeft = new JPanel(new GridLayout(2,1,0,4));
        balLeft.setOpaque(false);
        JLabel balTitle = new JLabel("Current Account Balance");
        balTitle.setFont(new Font("Arial",Font.BOLD,11));
        balTitle.setForeground(new Color(178,223,219));
        lblBalAmt = new JLabel("Enter account number →");
        lblBalAmt.setFont(new Font("Arial",Font.BOLD,22));
        lblBalAmt.setForeground(C_BALANCE);
        balLeft.add(balTitle); balLeft.add(lblBalAmt);

        JPanel balRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        balRight.setOpaque(false);
        JLabel accLbl = new JLabel("Account No:");
        accLbl.setFont(new Font("Arial",Font.BOLD,11));
        accLbl.setForeground(new Color(178,223,219));
        txtAccNo = new JTextField(12);
        styleField(txtAccNo);
        btnCheck = makeBtn("Check Balance",
                new Color(0,121,107), new Color(0,150,136));
        btnCheck.setPreferredSize(new Dimension(130,36));
        balRight.add(accLbl); balRight.add(txtAccNo); balRight.add(btnCheck);

        balCard.add(balLeft,  BorderLayout.CENTER);
        balCard.add(balRight, BorderLayout.EAST);

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120,40,100,100),1),
            new EmptyBorder(16,20,16,20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,10,6,10);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel st = new JLabel("▸  Transaction Details");
        st.setFont(new Font("Arial",Font.BOLD,12));
        st.setForeground(new Color(206,147,216));
        g.gridx=0; g.gridy=0; g.gridwidth=4;
        form.add(st,g); g.gridwidth=1;

        // Type row: two big toggle buttons
        g.gridx=0; g.gridy=1; g.weightx=0;
        JLabel lt = new JLabel("Type ✱");
        lt.setFont(new Font("Arial",Font.BOLD,11));
        lt.setForeground(new Color(200,170,215));
        form.add(lt,g);
        g.gridx=1; g.weightx=1;
        cmbType = new JComboBox<>(new String[]{"Deposit","Withdraw"});
        styleCombo(cmbType);
        form.add(cmbType,g);

        g.gridx=2; g.weightx=0;
        JLabel la = new JLabel("Amount (₹) ✱");
        la.setFont(new Font("Arial",Font.BOLD,11));
        la.setForeground(new Color(200,170,215));
        form.add(la,g);
        g.gridx=3; g.weightx=1;
        txtAmount = new JTextField();
        styleField(txtAmount);
        form.add(txtAmount,g);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        btnRow.setBackground(C_NAVY);
        btnSubmit  = makeBtn("✅  Submit Transaction", C_HEADER, C_HEADER2);
        btnSubmit.setPreferredSize(new Dimension(190,36));
        JButton btnClearLocal  = makeBtn("🔄  Clear",   new Color(69,90,100),  new Color(96,125,139));
        JButton btnRefreshLocal= makeBtn("↻  Refresh", new Color(74,20,140),  new Color(106,27,154));
        btnClear   = btnClearLocal;
        btnRefresh = btnRefreshLocal;
        btnRow.add(btnSubmit); btnRow.add(btnClearLocal); btnRow.add(btnRefreshLocal);

        wrap.add(balCard, BorderLayout.NORTH);
        wrap.add(form,    BorderLayout.CENTER);
        wrap.add(btnRow,  BorderLayout.SOUTH);

        btnCheck.addActionListener(e -> checkBalance());
        btnSubmit.addActionListener(e -> processTransaction());
        btnClear.addActionListener(e -> {
            txtAccNo.setText(""); txtAmount.setText("");
            lblBalAmt.setText("Enter account number →");
            lblBalAmt.setForeground(C_BALANCE);
        });
        btnRefresh.addActionListener(e -> loadData());
        return wrap;
    }

    // ── TABLE ─────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_NAVY);
        JLabel t = new JLabel("▸  Transaction History");
        t.setFont(new Font("Arial",Font.BOLD,12));
        t.setForeground(new Color(206,147,216));
        t.setBorder(new EmptyBorder(0,4,8,0));
        p.add(t, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"Txn ID","Account No","Type","Amount (₹)","Date"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model){
            @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
                Component c=super.prepareRenderer(r,row,col);
                if(!isRowSelected(row)){
                    c.setBackground(row%2==0?C_PANEL:C_ALT);
                    c.setForeground(C_WHITE);
                    // colour type column
                    if(col==2){
                        String val=model.getValueAt(row,col).toString();
                        c.setForeground(val.equals("Deposit")?C_DEPOSIT:C_WITHDRAW);
                    }
                    // colour amount column
                    if(col==3){
                        String type=model.getValueAt(row,2).toString();
                        c.setForeground(type.equals("Deposit")?C_DEPOSIT:C_WITHDRAW);
                    }
                } else { c.setBackground(C_SEL); c.setForeground(C_WHITE); }
                return c;
            }
        };
        styleTable();
        JScrollPane sc = new JScrollPane(table);
        sc.getViewport().setBackground(C_PANEL);
        sc.setBorder(BorderFactory.createLineBorder(new Color(120,40,100,100),1));
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStatusBar() {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT,14,0));
        b.setPreferredSize(new Dimension(980,28));
        b.setBackground(C_STATUS);
        b.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(255,255,255,20)));
        JLabel l = new JLabel("💸  Transactions loaded  |  FK: account_no → ACCOUNTS");
        l.setFont(new Font("Arial",Font.PLAIN,11));
        l.setForeground(new Color(160,100,150));
        b.add(l); return b;
    }

    // ══════════════════════════════════════════════════════════
    // BUSINESS LOGIC
    // ══════════════════════════════════════════════════════════
    private void checkBalance() {
        String acc = txtAccNo.getText().trim();
        if (acc.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Enter an account number first!","Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT balance FROM ACCOUNTS WHERE account_no=?")) {
            ps.setLong(1, Long.parseLong(acc));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double bal = rs.getDouble("balance");
                lblBalAmt.setText(String.format("₹ %,.2f", bal));
                lblBalAmt.setForeground(bal > 0 ? C_BALANCE : C_WITHDRAW);
            } else {
                JOptionPane.showMessageDialog(this,"Account not found!","Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processTransaction() {
        String accStr = txtAccNo.getText().trim();
        String amtStr = txtAmount.getText().trim();
        String type   = cmbType.getSelectedItem().toString();

        if (accStr.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Account No and Amount are required!","Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }
        double amount;
        try { amount = Double.parseDouble(amtStr); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Invalid amount!","Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this,"Amount must be greater than 0!","Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // ── BEGIN TRANSACTION ──

            // 1. Fetch current balance
            double currentBal;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT balance FROM ACCOUNTS WHERE account_no=?")) {
                ps.setLong(1, Long.parseLong(accStr));
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this,"Account not found!","Error",
                        JOptionPane.ERROR_MESSAGE);
                    conn.rollback(); return;
                }
                currentBal = rs.getDouble("balance");
            }

            // 2. Check sufficient balance for withdrawal
            if (type.equals("Withdraw") && currentBal < amount) {
                JOptionPane.showMessageDialog(this,
                    String.format("❌  Insufficient balance!\nAvailable: ₹ %,.2f",currentBal),
                    "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                conn.rollback(); return;
            }

            // 3. Update balance
            double newBal = type.equals("Deposit") ? currentBal+amount : currentBal-amount;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ACCOUNTS SET balance=? WHERE account_no=?")) {
                ps.setDouble(1, newBal);
                ps.setLong(2, Long.parseLong(accStr));
                ps.executeUpdate();
            }

            // 4. Insert transaction record
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO TRANSACTIONS VALUES(transactions_seq.NEXTVAL,?,?,?,SYSDATE)")) {
                ps.setLong(1, Long.parseLong(accStr));
                ps.setString(2, type);
                ps.setDouble(3, amount);
                ps.executeUpdate();
            }

            conn.commit(); // ── COMMIT ──
            lblBalAmt.setText(String.format("₹ %,.2f", newBal));
            lblBalAmt.setForeground(C_BALANCE);
            JOptionPane.showMessageDialog(this,
                String.format("✅  %s of ₹ %,.2f successful!\nNew Balance: ₹ %,.2f",
                    type, amount, newBal),
                "Transaction Complete", JOptionPane.INFORMATION_MESSAGE);
            txtAmount.setText(""); loadData();

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            JOptionPane.showMessageDialog(this,"Transaction failed: "+e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT * FROM TRANSACTIONS ORDER BY transaction_date DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) model.addRow(new Object[]{
                rs.getLong("transaction_id"), rs.getLong("account_no"),
                rs.getString("type"),
                String.format("₹ %,.2f", rs.getDouble("amount")),
                rs.getDate("transaction_date").toString()
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helpers ──────────────────────────────────────────────────
    private void styleField(JTextField t) {
        t.setFont(new Font("Arial",Font.PLAIN,13));
        t.setBackground(C_FIELD_BG); t.setForeground(C_WHITE); t.setCaretColor(C_WHITE);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_FIELD_BOR,1), new EmptyBorder(6,10,6,10)));
        t.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_FOC,2),new EmptyBorder(5,9,5,9)));}
            public void focusLost(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_BOR,1),new EmptyBorder(6,10,6,10)));}
        });
    }
    private void styleCombo(JComboBox<?> cb){
        cb.setFont(new Font("Arial",Font.PLAIN,13));
        cb.setBackground(C_FIELD_BG); cb.setForeground(C_WHITE);
        cb.setBorder(BorderFactory.createLineBorder(C_FIELD_BOR,1));
    }
    private void styleTable(){
        table.setRowHeight(30); table.setFont(new Font("Arial",Font.PLAIN,13));
        table.setForeground(C_WHITE); table.setBackground(C_PANEL);
        table.setGridColor(new Color(80,20,65)); table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader h=table.getTableHeader();
        h.setBackground(C_THEAD); h.setForeground(Color.BLACK);
        h.setFont(new Font("Arial",Font.BOLD,12));
        h.setPreferredSize(new Dimension(0,36));
    }
    private JButton makeBtn(String text,Color bg,Color hover){
        JButton b=new JButton(text){
            @Override protected void paintComponent(Graphics g){
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