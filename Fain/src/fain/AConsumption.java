/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import database.ConsumptionDB;
import database.DBConnection;
import database.MasterDB;
import database.StockDB;
import java.awt.Dimension;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class AConsumption extends javax.swing.JInternalFrame implements RefreshOption {

    DBConnection dbConnection;
    Main mainFrame;
    int level;
    RefreshOption prevFrame;
    String[][] branchData;
    String[][] itemData;
    /**
     * Creates new form MasterEntry
     */
    public AConsumption() {
        initComponents();
    }

    public AConsumption(DBConnection db, int mode, String id, Main frame, int level){
        this.mainFrame = frame;
        this.level = level;
        this.dbConnection = db;
        initComponents();
        refreshContents(Codes.REFRESH_ALL);
        prevFrame = null;
    }  
    
    public AConsumption(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.mainFrame = frame;
        this.level = level;
        this.dbConnection = db;
        initComponents();
        refreshContents(Codes.REFRESH_ALL);
    }
    
    private void loadBranch(){
        System.out.println("loading branchcbox");
        branchData = BranchDB.getBranch(this.dbConnection.getStatement());
        int len;
        if(branchData  == null){
            len =  0;
        }else{
            len = branchData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = branchData[0][i] + " : " + branchData[1][i];
        }
        cboxData[len] = "Add New";
        this.branchCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    private void loadItem(){
        System.out.println("loading itemcbox");
        itemData = StockDB.getItems(this.dbConnection.getStatement());
        int len;
        if(itemData  == null){
            len =  0;
        }else{
            len = itemData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = itemData[0][i] + " : " + itemData[1][i];
        }
        cboxData[len] = "Add New";
        this.itemCodeCbox.setModel(new DefaultComboBoxModel(cboxData));
    }   
    
    @Override
    public void refreshContents(int type) {
        if(type == Codes.REFRESH_ALL){
            loadBranch();
            loadItem();
        }
        else if(type == Codes.REFRESH_ITEMS){
            loadItem();
        }
        else if(type == Codes.REFRESH_BRANCHES){
            loadBranch();
        }
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        
        String branch     ="";
        Object selectedItem = branchCbox.getSelectedItem();
        if (selectedItem != null)
        {
            branch = selectedItem.toString();
        }
        
        String date  =dateTbox.getText();
        String refno =referenceNumberTbox.getText();
        
        String itemCode     ="";
        selectedItem = itemCodeCbox.getSelectedItem();
        if (selectedItem != null)
        {
            itemCode = selectedItem.toString();
        }
        String itemname  = itemNameTbox.getText();
        String narration = narrationTbox.getText();
        int quantity  = Integer.parseInt(quantityTbox.getText());
        
        ConsumptionDB.insert(stmt, branch, date, refno, itemCode, itemname, narration, quantity);
        
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_CONSUMPTION);
        }
    }
    
    private void addNewBranch(){
        ABranches item = new ABranches(dbConnection, Codes.NEW_ENTRY, null, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
    
    private void addNewItem(){
        AStock item = new AStock(dbConnection, Codes.NEW_ENTRY, null, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 360);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
    
    private void checkBranchChangedItem(){
        String item = this.branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewBranch();
        }
    }
    
    private void checkItemChangedItem(){
        String item = this.itemCodeCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewItem();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        outerPanel = new javax.swing.JPanel();
        leftInerPannel = new javax.swing.JPanel();
        logoPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labelsPanel = new javax.swing.JPanel();
        branchLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        referencenumberLabel = new javax.swing.JLabel();
        itemcodeLabel = new javax.swing.JLabel();
        itemnameLabel = new javax.swing.JLabel();
        narrationLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchCbox = new javax.swing.JComboBox<>();
        dateTbox = new javax.swing.JTextField();
        referenceNumberTbox = new javax.swing.JTextField();
        itemCodeCbox = new javax.swing.JComboBox<>();
        itemNameTbox = new javax.swing.JTextField();
        narrationTbox = new javax.swing.JTextField();
        quantityTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Consumption");
        setPreferredSize(new java.awt.Dimension(450, 410));

        outerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        outerPanel.setLayout(new java.awt.GridLayout(1, 2));

        leftInerPannel.setLayout(new java.awt.GridLayout(1, 2));

        logoPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fain/note.png"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logoPanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        leftInerPannel.add(logoPanel);

        labelsPanel.setLayout(new java.awt.GridLayout(8, 0, 0, 10));

        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        referencenumberLabel.setText("Reference Number");
        labelsPanel.add(referencenumberLabel);

        itemcodeLabel.setText("Item Code");
        labelsPanel.add(itemcodeLabel);

        itemnameLabel.setText("Item Name");
        labelsPanel.add(itemnameLabel);

        narrationLabel.setText("Narration");
        labelsPanel.add(narrationLabel);

        quantityLabel.setText("Quantity");
        labelsPanel.add(quantityLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(8, 0, 0, 10));

        branchCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        branchCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                branchCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(branchCbox);
        rightInerPannel.add(dateTbox);
        rightInerPannel.add(referenceNumberTbox);

        itemCodeCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        itemCodeCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                itemCodeCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(itemCodeCbox);

        itemNameTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNameTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(itemNameTbox);
        rightInerPannel.add(narrationTbox);
        rightInerPannel.add(quantityTbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        enterButton.setText("ENTER");
        enterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(enterButton, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(buttonPanel);

        outerPanel.add(rightInerPannel);

        getContentPane().add(outerPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
    // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void itemNameTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNameTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemNameTboxActionPerformed

    private void branchCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchCboxKeyPressed
        checkBranchChangedItem();
    }//GEN-LAST:event_branchCboxKeyPressed

    private void itemCodeCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_itemCodeCboxKeyPressed
        checkItemChangedItem();
    }//GEN-LAST:event_itemCodeCboxKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextField dateTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JComboBox<String> itemCodeCbox;
    private javax.swing.JTextField itemNameTbox;
    private javax.swing.JLabel itemcodeLabel;
    private javax.swing.JLabel itemnameLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel narrationLabel;
    private javax.swing.JTextField narrationTbox;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JTextField quantityTbox;
    private javax.swing.JTextField referenceNumberTbox;
    private javax.swing.JLabel referencenumberLabel;
    private javax.swing.JPanel rightInerPannel;
    // End of variables declaration//GEN-END:variables

    
}
