/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import database.CategoryDB;
import database.ConsumptionDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import java.awt.Dimension;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.UtilityFuncs;
/**
 *
 * @author akshos
 */
public class ACustomers extends javax.swing.JInternalFrame implements RefreshOption{

    DBConnection dbConnection;
    Main mainFrame;
    int level;
    RefreshOption prevFrame;
    String[][] branchData;
    String editId;
    int mode;
    /**
     * Creates new form MasterEntry
     */
    public ACustomers() {
        initComponents();
    }
    
    public ACustomers(DBConnection db, int mode, String id, Main frame, int level){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        initComponents();
        refreshContents(Codes.REFRESH_BRANCHES);
    }
    
    public ACustomers(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame, String code, String name){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.prevFrame = prevFrame;
        this.editId=id;
        this.mode=mode;
        initComponents();
        refreshContents(Codes.REFRESH_BRANCHES);
        if (mode==Codes.EDIT){
            loadContents();
        }
        else{
            this.codeTbox.setText(code);
            this.nameTbox.setText(name);
        }
        this.codeTbox.setEditable(false);
        this.nameTbox.setEditable(false);
        this.addressTarea.requestFocus();
    }
    
    private void loadBranchData(){
        System.out.println("loading branch data");
        branchData = BranchDB.getBranch(this.dbConnection.getStatement());
    }
    
    private void chooseBranch(){
        int index = UtilityFuncs.selectOption(this, "BRANCH", branchData);
        if(index != -1){
            this.branchTbox.setText(branchData[0][index]);
            this.branchNameLabel.setText(branchData[1][index]);
        }
    }
    
    private boolean validateBranch(){
        String branchCode = this.branchTbox.getText();
        String branchName = BranchDB.getBranchName(dbConnection.getStatement(), branchCode);
        if(branchName != null){
            this.branchNameLabel.setText(branchName);
            return true;
        }else{
            this.branchNameLabel.setText("NOT FOUND");
            return false;
        }
    }
    
    private void loadContents(){
        String[] data = CustomerDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        
        loadBranchData();
        this.branchTbox.setText(data[3]);
        this.validateBranch();
        
        this.codeTbox.setText(data[0]);
        this.nameTbox.setText(data[1]);
        this.addressTarea.setText(data[2]);
        this.kgstTbox.setText(data[4]);
        this.rbregnoTbox.setText(data[5]);
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        String code     =codeTbox.getText();
        String name     =nameTbox.getText();
        String address  =addressTarea.getText();

       String branch = branchTbox.getText();
        if(!validateBranch()){
            JOptionPane.showMessageDialog(this, "Please enter a valid BRANCH", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String kgst = kgstTbox.getText();
        String rbregno = rbregnoTbox.getText();
        
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = CustomerDB.update(stmt, code, name, address, branch, kgst, rbregno);
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else{
            ret = CustomerDB.insert(stmt, code, name, address, branch, kgst, rbregno);
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_CUSTOMERS);
            this.doDefaultCloseAction();
        }
    }
    
    @Override
    public void refreshContents(int type) {
        if(type == Codes.REFRESH_BRANCHES){
            loadBranchData();
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
        accountCodeLabel = new javax.swing.JLabel();
        accountHeadLabel = new javax.swing.JLabel();
        yopBalLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        codeTbox = new javax.swing.JTextField();
        nameTbox = new javax.swing.JTextField();
        addressTarea = new javax.swing.JTextField();
        branchPanel = new javax.swing.JPanel();
        branchTbox = new javax.swing.JTextField();
        branchNameLabel = new javax.swing.JLabel();
        kgstTbox = new javax.swing.JTextField();
        rbregnoTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Customer");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
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

        labelsPanel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

        accountCodeLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        accountCodeLabel.setText("Code");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        accountHeadLabel.setText("Name");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        yopBalLabel.setText("Address");
        labelsPanel.add(yopBalLabel);

        categoryLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        categoryLabel.setText("Branch");
        labelsPanel.add(categoryLabel);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setText("KGST");
        labelsPanel.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel3.setText("RB Registration Number");
        labelsPanel.add(jLabel3);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

        codeTbox.setBackground(java.awt.Color.white);
        codeTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        codeTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(codeTbox);

        nameTbox.setBackground(java.awt.Color.white);
        nameTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        nameTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(nameTbox);

        addressTarea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(addressTarea);

        branchPanel.setLayout(new java.awt.BorderLayout());

        branchTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        branchTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        branchTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                branchTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                branchTboxKeyReleased(evt);
            }
        });
        branchPanel.add(branchTbox, java.awt.BorderLayout.LINE_START);

        branchNameLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        branchPanel.add(branchNameLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(branchPanel);

        kgstTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        kgstTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                kgstTboxFocusGained(evt);
            }
        });
        kgstTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(kgstTbox);

        rbregnoTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        rbregnoTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rbregnoTboxFocusGained(evt);
            }
        });
        rbregnoTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                enterButtonKeyPressed(evt);
            }
        });
        rightInerPannel.add(rbregnoTbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        enterButton.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        enterButton.setText("ENTER");
        enterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterButtonActionPerformed(evt);
            }
        });
        enterButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                enterButtonKeyPressed(evt);
            }
        });
        buttonPanel.add(enterButton, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(buttonPanel);

        outerPanel.add(rightInerPannel);

        getContentPane().add(outerPanel, java.awt.BorderLayout.CENTER);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("CUSTOMER");
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
    // TODO add your handling code here:
        insertData();
        }//GEN-LAST:event_enterButtonActionPerformed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.insertData();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocusBackward();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost

    }//GEN-LAST:event_formFocusLost

    private void keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedHandler
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocusBackward();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_keyPressedHandler

    private void kgstTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kgstTboxFocusGained
        this.kgstTbox.selectAll();
    }//GEN-LAST:event_kgstTboxFocusGained

    private void rbregnoTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbregnoTboxFocusGained
        this.rbregnoTbox.selectAll();
    }//GEN-LAST:event_rbregnoTboxFocusGained

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosing

    private void branchTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            this.chooseBranch();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocusBackward();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_branchTboxKeyPressed

    private void branchTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchTboxKeyReleased
        this.validateBranch();
    }//GEN-LAST:event_branchTboxKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountCodeLabel;
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JTextField addressTarea;
    private javax.swing.JLabel branchNameLabel;
    private javax.swing.JPanel branchPanel;
    private javax.swing.JTextField branchTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JTextField codeTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField kgstTbox;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JTextField nameTbox;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JTextField rbregnoTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel yopBalLabel;
    // End of variables declaration//GEN-END:variables

}
