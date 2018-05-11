/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import javax.swing.JOptionPane;
import database.CategoryDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import java.awt.Dimension;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.ValidationChecks;
import reports.Ledger;
/**
 *
 * @author akshos
 */
public class PLedger extends javax.swing.JInternalFrame{
    
    DBConnection dbConnection;
    int level;
    Main mainFrame;
    String branchData[][];
    String accountData[][];
    /**
     * Creates new form MasterEntry
     */
    public PLedger() {
        initComponents();
    }
    
    public PLedger(DBConnection db, Main frame, int level){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        initComponents();
        loadBranch();
        loadAccounts();
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
            cboxData[i] = branchData[1][i] + " (" + branchData[0][i] + ")";
        }
        cboxData[len] = "All";
        this.branchCbox.setModel(new DefaultComboBoxModel(cboxData));
        this.branchCbox.setSelectedIndex(len);
    }
    
    private void loadAccounts(){
        String item = this.branchCbox.getSelectedItem().toString();
        String branchCode = "All";
        if(item.compareTo("All") != 0){
            int index = this.branchCbox.getSelectedIndex();
            branchCode = this.branchData[0][index];
        }
        accountData = CustomerDB.getCustomersInBranch(this.dbConnection.getStatement(), branchCode);
        int len;
        String[] cboxData = null;
        if(accountData  == null){
            len =  0;
            cboxData = new String[1];
            cboxData[0] = "None";
            this.accountFromCbox.setToolTipText("No Customers Available");
            this.accountToCbox.setToolTipText("No Customers Available");
        }else{
            len = accountData[0].length;
            cboxData = new String[len+1];
            for(int i = 0; i < len; i++){
                cboxData[i] = accountData[1][i] + "  (" + accountData[0][i] +")"  ;
            }
        }
        this.accountFromCbox.setModel(new DefaultComboBoxModel(cboxData));
        cboxData[len] = "None";
        this.accountToCbox.setModel(new DefaultComboBoxModel(cboxData));
        this.accountToCbox.setSelectedIndex(len);
    }
    
    private void generateReport(){
        if(branchData == null){
            int ret = JOptionPane.showConfirmDialog(this, "No Available Branches", "No Branches", JOptionPane.WARNING_MESSAGE);
        }
        if(accountFromCbox.getSelectedItem().toString().compareTo("None") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select atleast one Account", "No Account selected", JOptionPane.WARNING_MESSAGE);
        }
        int index;
        String item = this.branchCbox.getSelectedItem().toString();
        String branchCode = "All";
        if(item.compareTo("All") != 0){
            index = this.branchCbox.getSelectedIndex();
            branchCode = this.branchData[0][index];
        }
        index = this.accountFromCbox.getSelectedIndex();
        String accFrom = this.accountData[0][index];
        item = this.accountToCbox.getSelectedItem().toString();
        String accTo = "";
        if(item.compareTo("None") != 0){
            index = this.accountToCbox.getSelectedIndex();
            accTo = this.accountData[0][index];
        }
        String paper = this.paperCbox.getSelectedItem().toString();
        String orientation = this.orientationCbox.getSelectedItem().toString();
        Ledger.createReport(dbConnection, paper, orientation, branchCode, accFrom, accTo);
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
        logoLabel = new javax.swing.JLabel();
        labelsPanel = new javax.swing.JPanel();
        accountCodeLabel = new javax.swing.JLabel();
        accountHeadLabel = new javax.swing.JLabel();
        yopBalLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        orientationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchCbox = new javax.swing.JComboBox<>();
        accountFromCbox = new javax.swing.JComboBox<>();
        accountToCbox = new javax.swing.JComboBox<>();
        paperCbox = new javax.swing.JComboBox<>();
        orientationCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Ledger");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
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

        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fain/note.png"))); // NOI18N
        logoLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        logoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logoPanel.add(logoLabel, java.awt.BorderLayout.CENTER);

        leftInerPannel.add(logoPanel);

        labelsPanel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        accountCodeLabel.setText("Branch");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        accountHeadLabel.setText("Account From");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        yopBalLabel.setText("Account To");
        labelsPanel.add(yopBalLabel);

        paperLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        paperLabel.setText("Paper");
        labelsPanel.add(paperLabel);

        orientationLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        orientationLabel.setText("Orientation");
        labelsPanel.add(orientationLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        branchCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        branchCbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                branchCboxFocusLost(evt);
            }
        });
        rightInerPannel.add(branchCbox);

        accountFromCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rightInerPannel.add(accountFromCbox);

        accountToCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rightInerPannel.add(accountToCbox);

        paperCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        paperCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A4", "Legal" }));
        rightInerPannel.add(paperCbox);

        orientationCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        orientationCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Landscape", "Portrait" }));
        rightInerPannel.add(orientationCbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        enterButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
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

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        generateReport();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void branchCboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchCboxFocusLost
        this.loadAccounts();
    }//GEN-LAST:event_branchCboxFocusLost
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountCodeLabel;
    private javax.swing.JComboBox<String> accountFromCbox;
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JComboBox<String> accountToCbox;
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton enterButton;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JComboBox<String> orientationCbox;
    private javax.swing.JLabel orientationLabel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JComboBox<String> paperCbox;
    private javax.swing.JLabel paperLabel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel yopBalLabel;
    // End of variables declaration//GEN-END:variables
}