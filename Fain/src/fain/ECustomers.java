/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.DBConnection;
import database.CustomerDB;
import database.MasterDB;
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
public class ECustomers extends javax.swing.JInternalFrame implements RefreshOption{
    DBConnection dbConnection;
    Main mainFrame;
    int level;
    /**
     * Creates new form EMaster
     */
    public ECustomers(DBConnection db, Main frame, int level) {
        this.level = level;
        this.dbConnection = db;
        this.mainFrame = frame;
        initComponents();
        updateTable();
    }
    
    private void setMinWidth(){
        TableColumnModel col = this.dataTable.getColumnModel();
        int n = this.dataTable.getColumnCount();
        for(int i = 0; i < n; i++){
            col.getColumn(i).setMinWidth(100);
        }
    }
    
    private void setColumnAlignment(){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        int[] centerIndex = {0, 4, 5};
        for( int i = 0; i < centerIndex.length; i++){
            this.dataTable.getColumnModel().getColumn(centerIndex[i]).setCellRenderer(centerRenderer);
        }
    }
    
    private void resizeColumns(){
        int screenWidth = this.getWidth();
        int colCount = this.dataTable.getColumnCount();
        if(colCount == 0) return;
        int colWidth  = (screenWidth / (colCount-1)) - ((100/(colCount))+7);
        if(colWidth > 100){
            TableColumnModel col = this.dataTable.getColumnModel();
            for(int i = 1; i < colCount; i++){
                col.getColumn(i).setPreferredWidth(colWidth);
            }
        }
        this.dataTable.setRowHeight(30);
    }
    
    public void updateTable(){
        TableModel table = CustomerDB.getTable(dbConnection.getStatement());
        this.dataTable.setModel(table);
        UtilityFuncs.setTableFont(dataTable);
        this.setMinWidth();
        resizeColumns();
        setColumnAlignment();
    }
    
    public void refreshContents(int code){
        if(code == Codes.REFRESH_CUSTOMERS){
            updateTable();
        }
    }
    
    private void addEntry(){
        AMaster item = new AMaster(dbConnection, Codes.NEW_ENTRY, null, this.mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            System.out.println("setting size");
            item.setSize(dim);
        }else{
            item.setSize(790, 300);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
    
    private void editEntry(){
        int index = this.dataTable.getSelectedRow();
        if(index == -1 ) return;
        String id = this.dataTable.getModel().getValueAt(index, 0).toString();
        ACustomers item = new ACustomers(dbConnection, Codes.EDIT, id, mainFrame, this.level+1, this, null,null);
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
        String accCode = this.dataTable.getModel().getValueAt(row, 0).toString();
        String accName = this.dataTable.getModel().getValueAt(row, 1).toString();
        int ret = TransactionDB.checkAccountIdPresent(dbConnection.getStatement(), accCode);
        if(ret == Codes.EXISTING_ENTRY){
            JOptionPane.showMessageDialog(this, "This account has transactions. Cannot delete.", "CANNOT DELETE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(ret == Codes.FAIL){
            JOptionPane.showMessageDialog(this, "Cannot check transactions. Cannot delete.", "CANNOT DELETE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ret = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + accName, "SURE?", JOptionPane.WARNING_MESSAGE);
        if(ret != JOptionPane.YES_OPTION){
            return;
        }
        CustomerDB.delete(dbConnection.getStatement(), accCode);
        MasterDB.delete(dbConnection.getStatement(), accCode);
        
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

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Customers");
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

        tableScrollPane.setAutoscrolls(true);

        dataTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dataTable.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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
        editButton.setText("ENT: Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        lowerPanel.add(editButton);

        addButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosing

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addEntry();
    }//GEN-LAST:event_addButtonActionPerformed

    private void dataTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.editEntry();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F2){
            addEntry();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F3){
            deleteEntry();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_R){
            this.updateTable();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F5){
        
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F6){
            this.dataTable.setRowSelectionInterval(0, 0);
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F7){
            int lastRowIndex = this.dataTable.getRowCount() - 1;
            this.dataTable.setRowSelectionInterval(lastRowIndex, lastRowIndex);
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_dataTableKeyPressed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        editEntry();        // TODO add your handling code here:
    }//GEN-LAST:event_editButtonActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        this.resizeColumns();
    }//GEN-LAST:event_formComponentResized

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        this.deleteEntry();
    }//GEN-LAST:event_deleteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton bottomButton;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton findButton;
    private javax.swing.JPanel lowerPanel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JButton topButton;
    private javax.swing.JPanel upperPanel;
    // End of variables declaration//GEN-END:variables
}
