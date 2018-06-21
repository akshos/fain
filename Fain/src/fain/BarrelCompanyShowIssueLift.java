/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BarrelDB;
import database.DBConnection;
import database.BranchDB;
import database.CompanyBarrelDB;
import database.CustomerDB;
import database.TransactionDB;
import java.awt.Dimension;
import java.text.ParseException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import utility.Codes;
import utility.UtilityFuncs;
import utility.ValidationChecks;

/**
 *
 * @author akshos
 */
public class BarrelCompanyShowIssueLift extends javax.swing.JInternalFrame implements RefreshOption{
    DBConnection dbConnection;
    Main mainFrame;
    int level;
    /**
     * Creates new form EMaster
     */
    public BarrelCompanyShowIssueLift(DBConnection db, Main frame, int level) {
        this.dbConnection = db;
        this.mainFrame = frame;
        this.level = level;
        initComponents();
        updateTable();
        loadOpeningStock();
    }
    
    private void loadOpeningStock(){
        int opStock = CompanyBarrelDB.getCompanyOpStock(dbConnection.getStatement());
        this.opStockTbox.setText(String.valueOf(opStock));
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
        int[] centerIndex = {0, 1, 2, 4};
        for( int i = 0; i < centerIndex.length; i++){
            this.dataTable.getColumnModel().getColumn(centerIndex[i]).setCellRenderer(centerRenderer);
        }
    }
    
    private void resizeColumns(){
        /*
        int colCount = this.dataTable.getColumnCount();
        if(colCount == 0) return;
        TableColumnModel col = this.dataTable.getColumnModel();
        col.getColumn(1).setPreferredWidth(150);
        col.getColumn(2).setPreferredWidth(150);
        col.getColumn(3).setPreferredWidth(150);
        col.getColumn(4).setPreferredWidth(150);
        */
        int colCount = this.dataTable.getColumnCount();
        int screenWidth = this.getWidth();
        if(colCount == 0) return;
        int colWidth  = (screenWidth / (colCount-1)) - (100/(colCount)+5);
        TableColumnModel col = this.dataTable.getColumnModel();
        if(colWidth > 100){
            for(int i = 1; i < colCount; i++){
                col.getColumn(i).setPreferredWidth(colWidth);
            }
        }
    }
    
    private void setTableAppearance(){
        UtilityFuncs.setTableFont(dataTable);
        setMinWidth();
        resizeColumns();
        setColumnAlignment();
    }
    
    public void updateTable(){
        TableModel table = CompanyBarrelDB.getTable(dbConnection.getStatement());
        this.dataTable.setModel(table);
        UtilityFuncs.setTableFont(dataTable);
        setMinWidth();
        resizeColumns();
        setColumnAlignment();
    }
    
    private void addEntry(){
        BarrelCompanyAddIssueLift item = new BarrelCompanyAddIssueLift(dbConnection, Codes.NEW_ENTRY, null, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
      
    public void refreshContents(int code){
        if(code == Codes.REFRESH_BARREL){
            updateTable();
        }
    }
    
    private void setCompanyOpStock(){
        String opStockStr = JOptionPane.showInputDialog(this, "Enter Company Opening Stock: ", "Opening Stock", JOptionPane.QUESTION_MESSAGE);
        if(opStockStr.trim().compareTo("") == 0)
            return;
        try{
            int opStock = Integer.parseInt(opStockStr);
            CompanyBarrelDB.setCompanyOpStock(dbConnection.getStatement(), String.valueOf(opStock));
            this.opStockTbox.setText(String.valueOf(opStock));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void editEntry(){
        int index = this.dataTable.getSelectedRow();
        if(index == -1 ) return;
        String id = this.dataTable.getModel().getValueAt(index, 0).toString();
        BarrelCompanyAddIssueLift item = new BarrelCompanyAddIssueLift(dbConnection, Codes.EDIT, id, mainFrame, this.level+1, this);
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
        int ret;
        String barrelId = this.dataTable.getModel().getValueAt(row, 0).toString();
        ret = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + barrelId, "SURE?", JOptionPane.WARNING_MESSAGE);
        if(ret != JOptionPane.YES_OPTION){
            return;
        }
        CompanyBarrelDB.delete(dbConnection.getStatement(), barrelId);
        this.updateTable();
    }
    
    private void find(){
        String searchTerm = JOptionPane.showInputDialog(this, "Enter Search : ", "FILTER", JOptionPane.QUESTION_MESSAGE);
        if(searchTerm != null){
            System.out.println("Search " + searchTerm);
            if(ValidationChecks.isDateValid(searchTerm.trim())){
                filterTableDate(searchTerm.trim());
            }else{
                filterTableAccount(searchTerm.trim());
            }
         
        }
    }
    
    private void filterTableDate(String date){
        TableModel table = BarrelDB.getTableFilteredDate(dbConnection.getStatement(), UtilityFuncs.dateUserToSql(date));
        this.dataTable.setModel(table);
        setTableAppearance();
    }
    
    private void filterTableAccount(String account){
        TableModel table = BarrelDB.getTableFilteredAccount(dbConnection.getStatement(), account);
        this.dataTable.setModel(table);
        setTableAppearance();
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
        opStockPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        setOpStockButton = new javax.swing.JButton();
        opStockLabel = new javax.swing.JLabel();
        opStockTbox = new javax.swing.JTextField();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Barrel Issues and Lifts");
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
        findButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findButtonActionPerformed(evt);
            }
        });
        lowerPanel.add(findButton);

        topButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        topButton.setText("F6: Top");
        lowerPanel.add(topButton);

        bottomButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        bottomButton.setText("F7: Bottom");
        lowerPanel.add(bottomButton);

        getContentPane().add(lowerPanel, java.awt.BorderLayout.SOUTH);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("BARREL COMPANY ISSUES AND LIFTS");
        titlePanel.add(titleLabel, java.awt.BorderLayout.NORTH);

        opStockPanel.setPreferredSize(new java.awt.Dimension(100, 50));
        opStockPanel.setLayout(new java.awt.GridLayout(1, 3, 50, 20));

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.setLayout(new java.awt.BorderLayout(10, 10));

        setOpStockButton.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        setOpStockButton.setText("Set Opening Stock");
        setOpStockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOpStockButtonActionPerformed(evt);
            }
        });
        setOpStockButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                setOpStockButtonKeyPressed(evt);
            }
        });
        buttonPanel.add(setOpStockButton, java.awt.BorderLayout.CENTER);

        opStockPanel.add(buttonPanel);

        opStockLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        opStockLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        opStockLabel.setText("Company Op. Stock :");
        opStockPanel.add(opStockLabel);

        opStockTbox.setEditable(false);
        opStockTbox.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        opStockTbox.setFocusable(false);
        opStockTbox.setRequestFocusEnabled(false);
        opStockPanel.add(opStockTbox);

        titlePanel.add(opStockPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

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
            find();
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
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            if(this.dataTable.getSelectedRow() == 0){
                this.setOpStockButton.requestFocus();
            }
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

    private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
        find();        // TODO add your handling code here:
    }//GEN-LAST:event_findButtonActionPerformed

    private void setOpStockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setOpStockButtonActionPerformed
        setCompanyOpStock();        // TODO add your handling code here:
    }//GEN-LAST:event_setOpStockButtonActionPerformed

    private void setOpStockButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_setOpStockButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            setCompanyOpStock();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            this.dataTable.requestFocus();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            this.dataTable.requestFocus();
        }
    }//GEN-LAST:event_setOpStockButtonKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton bottomButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton findButton;
    private javax.swing.JPanel lowerPanel;
    private javax.swing.JLabel opStockLabel;
    private javax.swing.JPanel opStockPanel;
    private javax.swing.JTextField opStockTbox;
    private javax.swing.JButton setOpStockButton;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JButton topButton;
    private javax.swing.JPanel upperPanel;
    // End of variables declaration//GEN-END:variables
}
