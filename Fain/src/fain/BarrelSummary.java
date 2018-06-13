/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.DBConnection;
import database.BranchDB;
import database.CustomerDB;
import database.TransactionDB;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import utility.Codes;
import utility.UtilityFuncs;

/**
 *
 * @author akshos
 */
public class BarrelSummary extends javax.swing.JInternalFrame implements RefreshOption{
    DBConnection dbConnection;
    Main mainFrame;
    int level;
    /**
     * Creates new form EMaster
     */
    public BarrelSummary(DBConnection db, Main frame, int level) {
        this.dbConnection = db;
        this.mainFrame = frame;
        this.level = level;
        initComponents();
        updateTable();
    }
    
    private void setMinWidth(){
        TableColumnModel col = this.dataTable.getColumnModel();
        int n = this.dataTable.getColumnCount();
        for(int i = 0; i < n; i++){
            col.getColumn(i).setMinWidth(100);
        }
        this.dataTable.setRowHeight(30);
    }
    
    private void setColumnAlignment(){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        int[] centerIndex = {0, 3, 4};
        for( int i = 0; i < centerIndex.length; i++){
            this.dataTable.getColumnModel().getColumn(centerIndex[i]).setCellRenderer(centerRenderer);
        }
    }
    
    private void resizeColumns(){
        int screenWidth = this.getWidth();
        int colCount = this.dataTable.getColumnCount();
        if(colCount == 0) return;
        int colWidth  = (screenWidth / (colCount-1)) - (100/(colCount)+10);
        if(colWidth > 100){
            TableColumnModel col = this.dataTable.getColumnModel();
            for(int i = 1; i < colCount; i++){
                col.getColumn(i).setPreferredWidth(colWidth);
            }
        }
    }
    
    public void updateTable(){
        TableModel table = BranchDB.getTable(dbConnection.getStatement());
        this.dataTable.setModel(table);
        UtilityFuncs.setTableFont(dataTable);
        setMinWidth();
        resizeColumns();
        setColumnAlignment();
    }
    
    private void addEntry(){
        ABranches item = new ABranches(dbConnection, Codes.NEW_ENTRY, null, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
      
    public void refreshContents(int code){
        if(code == Codes.REFRESH_BRANCHES){
            updateTable();
        }
    }
    
    private void editEntry(){
        int index = this.dataTable.getSelectedRow();
        if(index == -1 ) return;
        String id = this.dataTable.getModel().getValueAt(index, 0).toString();
        ABranches item = new ABranches(dbConnection, Codes.EDIT, id, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
    
    private void deleteEntry(){
        int row = this.dataTable.getSelectedRow();
        String branchCode = this.dataTable.getModel().getValueAt(row, 0).toString();
        String branchName = this.dataTable.getModel().getValueAt(row, 1).toString();
        int ret = TransactionDB.checkBranchCodePresent(dbConnection.getStatement(), branchCode);
        if(ret == Codes.EXISTING_ENTRY){
            JOptionPane.showMessageDialog(this, "This Branch Cannot be deleted", "CANNOT DELETE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(ret == Codes.FAIL){
            JOptionPane.showMessageDialog(this, "Cannot check transactions. Cannot delete.", "CANNOT DELETE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ret = CustomerDB.checkBranchCodePresent(dbConnection.getStatement(), branchCode);
        if(ret == Codes.EXISTING_ENTRY){
            JOptionPane.showMessageDialog(this, "This Branch Cannot be deleted", "CANNOT DELETE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(ret == Codes.FAIL){
            JOptionPane.showMessageDialog(this, "Cannot check transactions. Cannot delete.", "CANNOT DELETE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ret = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + branchName, "SURE?", JOptionPane.WARNING_MESSAGE);
        if(ret != JOptionPane.YES_OPTION){
            return;
        }
        BranchDB.delete(dbConnection.getStatement(), branchCode);
        this.updateTable();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        upperPanel = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        lowerPanel = new javax.swing.JPanel();
        editButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        findButton = new javax.swing.JButton();
        topButton = new javax.swing.JButton();
        bottomButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        summaryPanel = new javax.swing.JPanel();
        businessPanel = new javax.swing.JPanel();
        businessAccountLabel = new javax.swing.JLabel();
        businessSummaryPanel = new javax.swing.JPanel();
        businessTotelLabel = new javax.swing.JLabel();
        businessTotelTbox = new javax.swing.JTextField();
        businessShortageLabel = new javax.swing.JLabel();
        businessShortageTbox = new javax.swing.JTextField();
        businessTotalLabel = new javax.swing.JLabel();
        businessTotalTbox = new javax.swing.JTextField();
        customerPanel = new javax.swing.JPanel();
        customerAccountPanel = new javax.swing.JLabel();
        customerSummaryPanel = new javax.swing.JPanel();
        customerTotalIssuesLabel = new javax.swing.JLabel();
        customerTotalIssuedTbox = new javax.swing.JTextField();
        customerLatexBarrelLabel = new javax.swing.JLabel();
        customerLatexBarrelTbox = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        upperPanel1 = new javax.swing.JPanel();
        tableScrollPane1 = new javax.swing.JScrollPane();
        dataTable1 = new javax.swing.JTable();
        lowerPanel1 = new javax.swing.JPanel();
        editButton1 = new javax.swing.JButton();
        addButton1 = new javax.swing.JButton();
        deleteButton1 = new javax.swing.JButton();
        findButton1 = new javax.swing.JButton();
        topButton1 = new javax.swing.JButton();
        bottomButton1 = new javax.swing.JButton();
        titlePanel1 = new javax.swing.JPanel();
        titleLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Barrel sUMMARY");
        setMinimumSize(new java.awt.Dimension(785, 450));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        upperPanel.setLayout(new java.awt.BorderLayout());

        tableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tableScrollPane.setAutoscrolls(true);
        tableScrollPane.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        tableScrollPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });

        dataTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dataTable.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Code", "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        dataTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dataTableKeyPressed(evt);
            }
        });
        tableScrollPane.setViewportView(dataTable);

        upperPanel.add(tableScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(upperPanel, java.awt.BorderLayout.CENTER);

        lowerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lowerPanel.setPreferredSize(new java.awt.Dimension(639, 50));
        lowerPanel.setLayout(new java.awt.GridLayout(1, 6, 20, 0));

        editButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        editButton.setMnemonic(java.awt.event.KeyEvent.VK_F1);
        editButton.setText("ENT: Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        lowerPanel.add(editButton);

        addButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        addButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
        addButton.setText("F2: Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        lowerPanel.add(addButton);

        deleteButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        deleteButton.setText("F3: Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        lowerPanel.add(deleteButton);

        findButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        findButton.setText("F5: Find");
        lowerPanel.add(findButton);

        topButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        topButton.setText("F6: Top");
        lowerPanel.add(topButton);

        bottomButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        bottomButton.setText("F7: Bottom");
        lowerPanel.add(bottomButton);

        getContentPane().add(lowerPanel, java.awt.BorderLayout.SOUTH);

        titlePanel.setPreferredSize(new java.awt.Dimension(248, 200));
        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("BARREL SUMMARY");
        titlePanel.add(titleLabel, java.awt.BorderLayout.NORTH);

        summaryPanel.setLayout(new java.awt.GridLayout(1, 2));

        businessPanel.setLayout(new java.awt.BorderLayout());

        businessAccountLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        businessAccountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        businessAccountLabel.setText("BUSINESS ACCOUNT");
        businessAccountLabel.setPreferredSize(new java.awt.Dimension(137, 30));
        businessPanel.add(businessAccountLabel, java.awt.BorderLayout.PAGE_START);

        businessSummaryPanel.setLayout(new java.awt.GridLayout(3, 0, 0, 10));

        businessTotelLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        businessTotelLabel.setText("TOTEL :");
        businessTotelLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        businessSummaryPanel.add(businessTotelLabel);
        businessSummaryPanel.add(businessTotelTbox);

        businessShortageLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        businessShortageLabel.setText("SHORTAGE :");
        businessShortageLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        businessSummaryPanel.add(businessShortageLabel);
        businessSummaryPanel.add(businessShortageTbox);

        businessTotalLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        businessTotalLabel.setText("TOTAL :");
        businessTotalLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        businessSummaryPanel.add(businessTotalLabel);
        businessSummaryPanel.add(businessTotalTbox);

        businessPanel.add(businessSummaryPanel, java.awt.BorderLayout.CENTER);

        summaryPanel.add(businessPanel);

        customerPanel.setLayout(new java.awt.BorderLayout());

        customerAccountPanel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        customerAccountPanel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        customerAccountPanel.setText("CUSTOMER ACCOUNT");
        customerAccountPanel.setPreferredSize(new java.awt.Dimension(137, 30));
        customerPanel.add(customerAccountPanel, java.awt.BorderLayout.PAGE_START);

        customerSummaryPanel.setLayout(new java.awt.GridLayout(4, 0, 0, 4));

        customerTotalIssuesLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        customerTotalIssuesLabel.setText("TOTAL ISSUED :");
        customerTotalIssuesLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        customerSummaryPanel.add(customerTotalIssuesLabel);
        customerSummaryPanel.add(customerTotalIssuedTbox);

        customerLatexBarrelLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        customerLatexBarrelLabel.setText("LATEX BARREL :");
        customerLatexBarrelLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        customerSummaryPanel.add(customerLatexBarrelLabel);
        customerSummaryPanel.add(customerLatexBarrelTbox);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("EMPTY BARREL :");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        customerSummaryPanel.add(jLabel3);
        customerSummaryPanel.add(jTextField3);

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setText("TOTAL :");
        jLabel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        customerSummaryPanel.add(jLabel4);
        customerSummaryPanel.add(jTextField4);

        customerPanel.add(customerSummaryPanel, java.awt.BorderLayout.CENTER);

        summaryPanel.add(customerPanel);

        titlePanel.add(summaryPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        jInternalFrame1.setClosable(true);
        jInternalFrame1.setMaximizable(true);
        jInternalFrame1.setResizable(true);
        jInternalFrame1.setTitle("Branches");
        jInternalFrame1.setMinimumSize(new java.awt.Dimension(785, 450));
        jInternalFrame1.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                jInternalFrame1formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        jInternalFrame1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jInternalFrame1formComponentResized(evt);
            }
        });

        upperPanel1.setLayout(new java.awt.BorderLayout());

        tableScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tableScrollPane1.setAutoscrolls(true);
        tableScrollPane1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        tableScrollPane1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableScrollPane1keyPressedHandler(evt);
            }
        });

        dataTable1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dataTable1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        dataTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Code", "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        dataTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dataTable1KeyPressed(evt);
            }
        });
        tableScrollPane1.setViewportView(dataTable1);

        upperPanel1.add(tableScrollPane1, java.awt.BorderLayout.CENTER);

        jInternalFrame1.getContentPane().add(upperPanel1, java.awt.BorderLayout.CENTER);

        lowerPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lowerPanel1.setPreferredSize(new java.awt.Dimension(639, 50));
        lowerPanel1.setLayout(new java.awt.GridLayout(1, 6, 20, 0));

        editButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        editButton1.setMnemonic(java.awt.event.KeyEvent.VK_F1);
        editButton1.setText("ENT: Edit");
        editButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButton1ActionPerformed(evt);
            }
        });
        lowerPanel1.add(editButton1);

        addButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        addButton1.setMnemonic(java.awt.event.KeyEvent.VK_A);
        addButton1.setText("F2: Add");
        addButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButton1ActionPerformed(evt);
            }
        });
        lowerPanel1.add(addButton1);

        deleteButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        deleteButton1.setText("F3: Delete");
        deleteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButton1ActionPerformed(evt);
            }
        });
        lowerPanel1.add(deleteButton1);

        findButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        findButton1.setText("F5: Find");
        lowerPanel1.add(findButton1);

        topButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        topButton1.setText("F6: Top");
        lowerPanel1.add(topButton1);

        bottomButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        bottomButton1.setText("F7: Bottom");
        lowerPanel1.add(bottomButton1);

        jInternalFrame1.getContentPane().add(lowerPanel1, java.awt.BorderLayout.SOUTH);

        titlePanel1.setPreferredSize(new java.awt.Dimension(248, 200));
        titlePanel1.setLayout(new java.awt.BorderLayout());

        titleLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        titleLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel1.setText("BROWSE BRANCHES");
        titlePanel1.add(titleLabel1, java.awt.BorderLayout.CENTER);

        jInternalFrame1.getContentPane().add(titlePanel1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jInternalFrame1, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosing

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        System.out.println("Add Button Action Performed");
        addEntry();
    }//GEN-LAST:event_addButtonActionPerformed

    private void dataTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.editEntry();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F2){
            addEntry();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F2){
        
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_R){
            this.updateTable();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F3){
            this.deleteEntry();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F5){
        
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F6){
            this.dataTable.setRowSelectionInterval(0, 0);
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F7){
            int lastRowIndex = this.dataTable.getRowCount() - 1;
            this.dataTable.setRowSelectionInterval(lastRowIndex, lastRowIndex);
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_dataTableKeyPressed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        editEntry();
    }//GEN-LAST:event_editButtonActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        resizeColumns();
    }//GEN-LAST:event_formComponentResized

    private void keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedHandler

    }//GEN-LAST:event_keyPressedHandler

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        this.deleteEntry();        // TODO add your handling code here:
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void dataTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTable1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_dataTable1KeyPressed

    private void tableScrollPane1keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableScrollPane1keyPressedHandler
        // TODO add your handling code here:
    }//GEN-LAST:event_tableScrollPane1keyPressedHandler

    private void editButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editButton1ActionPerformed

    private void addButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addButton1ActionPerformed

    private void deleteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteButton1ActionPerformed

    private void jInternalFrame1formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_jInternalFrame1formInternalFrameClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_jInternalFrame1formInternalFrameClosing

    private void jInternalFrame1formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jInternalFrame1formComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jInternalFrame1formComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton addButton1;
    private javax.swing.JButton bottomButton;
    private javax.swing.JButton bottomButton1;
    private javax.swing.JLabel businessAccountLabel;
    private javax.swing.JPanel businessPanel;
    private javax.swing.JLabel businessShortageLabel;
    private javax.swing.JTextField businessShortageTbox;
    private javax.swing.JPanel businessSummaryPanel;
    private javax.swing.JLabel businessTotalLabel;
    private javax.swing.JTextField businessTotalTbox;
    private javax.swing.JLabel businessTotelLabel;
    private javax.swing.JTextField businessTotelTbox;
    private javax.swing.JLabel customerAccountPanel;
    private javax.swing.JLabel customerLatexBarrelLabel;
    private javax.swing.JTextField customerLatexBarrelTbox;
    private javax.swing.JPanel customerPanel;
    private javax.swing.JPanel customerSummaryPanel;
    private javax.swing.JTextField customerTotalIssuedTbox;
    private javax.swing.JLabel customerTotalIssuesLabel;
    private javax.swing.JTable dataTable;
    private javax.swing.JTable dataTable1;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteButton1;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editButton1;
    private javax.swing.JButton findButton;
    private javax.swing.JButton findButton1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JPanel lowerPanel;
    private javax.swing.JPanel lowerPanel1;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JScrollPane tableScrollPane1;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel titleLabel1;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel titlePanel1;
    private javax.swing.JButton topButton;
    private javax.swing.JButton topButton1;
    private javax.swing.JPanel upperPanel;
    private javax.swing.JPanel upperPanel1;
    // End of variables declaration//GEN-END:variables
}
