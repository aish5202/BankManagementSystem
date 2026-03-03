package bankmanagement;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * AccountForm.java
 * ──────────────────────────────────────────────────────────────
 * CRUD form for ACCOUNTS table (child of CUSTOMERS).
 * Teal / dark-green accent theme.
 */
public class AccountForm extends JFrame {

    private static final Color C_NAVY      = new Color(10,  22,  40);
    private static final Color C_PANEL     = new Color(18,  32,  52);
    private static final Color C_HEADER    = new Color( 0,  105,  92);
    private static final Color C_HEADER2   = new Color( 0,  121, 107);
    private static final Color C_FIELD_BG  = new Color(20,   40,  60);
    private static final Color C_FIELD_BOR = new Color(0,   130, 115);
    private static final Color C_FIELD_FOC = new Color(0,   200, 180);
    private static final Color C_THEAD     = new Color( 0,  121, 107);
    private static final Color C_SEL       = new Color( 0,   70,  65);
    private static final Color C_ALT       = new Color(15,   28,  45);
    private static final Color C_WHITE     = Color.WHITE;
    private static final Color C_MUTED     = new Color(120, 180, 175);
    private static final Color C_STATUS    = new Color(10,  20,  35);
    private static final Color C_BALANCE   = new Color( 0,  180, 160);

    private JTextField  txtAccNo, txtCustId, txtBalance;
    private JComboBox<String> cmbType;
    private JTable            table;
    private DefaultTableModel model;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;

    public AccountForm() {
        setTitle("Account Management — BMS");
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
        JLabel l = new JLabel("🏧   Account Management");
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
            BorderFactory.createLineBorder(new Color(0,130,115,100), 1),
            new EmptyBorder(16,20,16,20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,10,6,10);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel st = new JLabel("▸  Account Details");
        st.setFont(new Font("Arial",Font.BOLD,12));
        st.setForeground(new Color(128,203,196));
        g.gridx=0; g.gridy=0; g.gridwidth=6;
        form.add(st,g); g.gridwidth=1;

        txtAccNo  = addField(form,g,"Account No (Auto)",1,0,true);
        txtCustId = addField(form,g,"Customer ID ✱",    1,2,false);
        txtBalance= addField(form,g,"Balance (₹)",      1,4,false);

        // Account Type combo
        g.gridx=0; g.gridy=2; g.weightx=0;
        JLabel lt = new JLabel("Account Type ✱");
        lt.setFont(new Font("Arial",Font.BOLD,11));
        lt.setForeground(new Color(160,220,215));
        form.add(lt,g);
        g.gridx=1; g.weightx=1;
        cmbType = new JComboBox<>(new String[]{"Savings","Current","Fixed","Salary"});
        styleCombo(cmbType);
        form.add(cmbType,g);

        wrapper.add(form,   BorderLayout.CENTER);
        wrapper.add(buildButtonRow(), BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        row.setBackground(C_NAVY);
        btnAdd     = makeBtn("➕  Add",     new Color(0,121,107),  new Color(0,150,136));
        btnUpdate  = makeBtn("✏️  Update",  new Color(21,101,192), new Color(30,136,229));
        btnDelete  = makeBtn("🗑  Delete",  new Color(183,28,28),  new Color(211,47,47));
        btnClear   = makeBtn("🔄  Clear",   new Color(69,90,100),  new Color(96,125,139));
        btnRefresh = makeBtn("↻  Refresh", new Color(74,20,140),  new Color(106,27,154));
        row.add(btnAdd); row.add(btnUpdate); row.add(btnDelete);
        row.add(btnClear); row.add(btnRefresh);
        btnAdd.addActionListener(e->insertAccount());
        btnUpdate.addActionListener(e->updateAccount());
        btnDelete.addActionListener(e->deleteAccount());
        btnClear.addActionListener(e->clearFields());
        btnRefresh.addActionListener(e->loadData());
        return row;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_NAVY);
        JLabel t = new JLabel("▸  Account List");
        t.setFont(new Font("Arial",Font.BOLD,12));
        t.setForeground(new Color(128,203,196));
        t.setBorder(new EmptyBorder(0,4,8,0));
        p.add(t, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"Account No","Customer ID","Type","Balance (₹)"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
                Component c=super.prepareRenderer(r,row,col);
                if(!isRowSelected(row)){
                    c.setBackground(row%2==0?C_PANEL:C_ALT);
                    c.setForeground(C_WHITE);
                } else { c.setBackground(C_SEL); c.setForeground(C_WHITE); }
                // colour balance column
                if(!isRowSelected(row)&&col==3) c.setForeground(C_BALANCE);
                return c;
            }
        };
        styleTable();
        table.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()&&table.getSelectedRow()>=0){
                int r=table.getSelectedRow();
                txtAccNo.setText(model.getValueAt(r,0).toString());
                txtCustId.setText(model.getValueAt(r,1).toString());
                cmbType.setSelectedItem(model.getValueAt(r,2).toString());
                String bal=model.getValueAt(r,3).toString().replace("₹ ","");
                txtBalance.setText(bal);
            }
        });
        JScrollPane sc=new JScrollPane(table);
        sc.getViewport().setBackground(C_PANEL);
        sc.setBorder(BorderFactory.createLineBorder(new Color(0,130,115,100),1));
        p.add(sc,BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStatusBar(){
        JPanel b=new JPanel(new FlowLayout(FlowLayout.LEFT,14,0));
        b.setPreferredSize(new Dimension(980,28));
        b.setBackground(C_STATUS);
        b.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(255,255,255,20)));
        JLabel l=new JLabel("🏧  Accounts loaded  |  FK: customer_id → CUSTOMERS");
        l.setFont(new Font("Arial",Font.PLAIN,11));
        l.setForeground(new Color(100,160,155));
        b.add(l); return b;
    }

    // CRUD ─────────────────────────────────────────────────────
    private void insertAccount(){
        if(txtCustId.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this,"Customer ID required!","Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(
                "INSERT INTO ACCOUNTS VALUES(accounts_seq.NEXTVAL,?,?,?)")){
            ps.setInt(1,Integer.parseInt(txtCustId.getText().trim()));
            ps.setString(2,cmbType.getSelectedItem().toString());
            ps.setDouble(3,txtBalance.getText().isEmpty()?0.0
                :Double.parseDouble(txtBalance.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"✅  Account created!","Success",
                JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAccount(){
        if(txtAccNo.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this,"Select an account first!","Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(
                "UPDATE ACCOUNTS SET account_type=?,balance=? WHERE account_no=?")){
            ps.setString(1,cmbType.getSelectedItem().toString());
            ps.setDouble(2,Double.parseDouble(txtBalance.getText().trim()));
            ps.setLong(3,Long.parseLong(txtAccNo.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"✅  Account updated!","Success",
                JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAccount(){
        if(txtAccNo.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this,"Select an account to delete!","Validation",
                JOptionPane.WARNING_MESSAGE); return;
        }
        int ok=JOptionPane.showConfirmDialog(this,
            "Delete account "+txtAccNo.getText()+"?\n⚠ All transactions will be deleted too!",
            "Confirm",JOptionPane.YES_NO_OPTION);
        if(ok!=JOptionPane.YES_OPTION) return;
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(
                "DELETE FROM ACCOUNTS WHERE account_no=?")){
            ps.setLong(1,Long.parseLong(txtAccNo.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"✅  Account deleted!","Success",
                JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData(){
        model.setRowCount(0);
        try(Connection c=DBConnection.getConnection();
            PreparedStatement ps=c.prepareStatement(
                "SELECT * FROM ACCOUNTS ORDER BY account_no");
            ResultSet rs=ps.executeQuery()){
            while(rs.next()) model.addRow(new Object[]{
                rs.getLong("account_no"), rs.getInt("customer_id"),
                rs.getString("account_type"),
                String.format("₹ %.2f",rs.getDouble("balance"))
            });
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields(){
        txtAccNo.setText(""); txtCustId.setText(""); txtBalance.setText("");
        cmbType.setSelectedIndex(0); table.clearSelection();
    }

    // Helpers ──────────────────────────────────────────────────
    private JTextField addField(JPanel p,GridBagConstraints g,
                                 String label,int row,int col,boolean ro){
        g.gridx=col; g.gridy=row; g.weightx=0;
        JLabel l=new JLabel(label);
        l.setFont(new Font("Arial",Font.BOLD,11));
        l.setForeground(new Color(160,220,215));
        p.add(l,g);
        g.gridx=col+1; g.weightx=1;
        JTextField t=new JTextField();
        t.setFont(new Font("Arial",Font.PLAIN,13));
        t.setBackground(ro?new Color(15,28,42):C_FIELD_BG);
        t.setForeground(ro?new Color(80,120,115):C_WHITE);
        t.setCaretColor(C_WHITE); t.setEditable(!ro);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ro?new Color(25,50,48):C_FIELD_BOR,1),
            new EmptyBorder(6,10,6,10)));
        if(!ro) t.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_FOC,2),new EmptyBorder(5,9,5,9)));}
            public void focusLost(FocusEvent e){
                t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_BOR,1),new EmptyBorder(6,10,6,10)));}
        });
        p.add(t,g); return t;
    }

    private void styleCombo(JComboBox<?> cb){
        cb.setFont(new Font("Arial",Font.PLAIN,13));
        cb.setBackground(C_FIELD_BG); cb.setForeground(C_WHITE);
        cb.setBorder(BorderFactory.createLineBorder(C_FIELD_BOR,1));
    }

    private void styleTable(){
        table.setRowHeight(32);
        table.setFont(new Font("Arial",Font.PLAIN,13));
        table.setForeground(C_WHITE); table.setBackground(C_PANEL);
        table.setGridColor(new Color(0,80,70));
        table.setShowGrid(true); table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader h=table.getTableHeader();
        h.setBackground(C_THEAD); h.setForeground(Color.BLACK);
        h.setFont(new Font("Arial",Font.BOLD,12));
        h.setPreferredSize(new Dimension(0,38));
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