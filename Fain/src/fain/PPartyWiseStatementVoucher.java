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
import java.awt.Cursor;
import java.awt.Dimension;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import reports.PartyWiseStatement;
import reports.PartyWiseStatementVoucher;
import utility.UtilityFuncs;
import utility.ValidationChecks;
import utility.Wait;
/**
 *
 * @author akshos
 */
public class PPartyWiseStatementVoucher extends javax.swing.JInternalFrame{
    
    DBConnection dbConnection;
    int level;
    Main mainFrame;
    String accountData[][];
    String branchData[][];
    /**
     * Creates new form MasterEntry
     */
    public PPartyWiseStatementVoucher() {
        initComponents();
    }
    
    public PPartyWiseStatementVoucher(DBConnection db, Main frame, int level){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        initComponents();
        loadCurrDate();
        loadBranchData();
        loadAccountData();
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.datePicker.setText(date);
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
        if(branchCode.isEmpty())
            return true;
        String branchName = BranchDB.getBranchName(dbConnection.getStatement(), branchCode);
        if(branchName != null){
            this.branchNameLabel.setText(branchName);
            return true;
        }else{
            this.branchNameLabel.setText("NOT FOUND");
            return false;
        }
    }
    
   private void loadAccountData(){
        String branchCode = this.branchTbox.getText().trim();
        if(branchCode.isEmpty()){
            branchCode = "All";
        }
        accountData = CustomerDB.getCustomersInBranch(this.dbConnection.getStatement(), branchCode);
    }
    
    private boolean validateAccount(){
        String accCode = this.accountTbox.getText();
        if(accCode.isEmpty())
            return true;
        String accName = CustomerDB.getCustomerName(dbConnection.getStatement(), accCode);
        if(accName != null){
            this.accountNameLabel.setText(accName);
            return true;
        }
        else{
            this.accountNameLabel.setText("NOT FOUND");
            return false;
        }
    }
    
    private void chooseAccount(){
       int index = UtilityFuncs.selectOption(this, " ACCOUNT", accountData);
       if(index != -1){
           this.accountNameLabel.setText(accountData[1][index]);
           this.accountTbox.setText(accountData[0][index]);
       }
    }
    
    private void generateReport(){    
        setBusy();
        
        if(accountData == null){
            JOptionPane.showMessageDialog(this, "No Accounts Available", "No Accounts", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String branch = this.branchTbox.getText().trim();
        if(branch.isEmpty()){
            branch = "All";
        }else if(!validateBranch()){
            JOptionPane.showMessageDialog(this, "Please enter a valid Branch", "No Branch", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String account = this.accountTbox.getText().trim();
        if(account.isEmpty()){
            account = accountData[0][0];
        }else if(!validateAccount()){
            JOptionPane.showMessageDialog(this, "Please enter a valid Account From", "No Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        

        
        String date=this.datePicker.getText();
        if(!ValidationChecks.isDateValid(date)){
            JOptionPane.showMessageDialog(this, "Please enter valid Date To", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }       
        date = UtilityFuncs.dateUserToSql(date);  
        
        final String fbranch = branch;
        final String faccount = account;
        final String billDate = date;
        String paper = this.paperCbox.getSelectedItem().toString();
        String orientation = this.orientationCbox.getSelectedItem().toString();
        
        Thread t;
        t = new Thread(new Runnable(){
            public void run(){
                Wait wait = new Wait();
                wait.setSize(new Dimension(700, 400));
                wait.setVisible(true);
                mainFrame.addToMainDesktopPane(wait, level+1, Codes.NO_DATABASE);
                int ret = PartyWiseStatementVoucher.createReport(dbConnection, paper, orientation, billDate, faccount);
                wait.closeWait();
            }
        });
        t.start();
        resetBusy();
    }
    
    private void setBusy(){
        this.enterButton.setEnabled(false);
        this.enterButton.setText("Please Wait");
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    
    private void resetBusy(){
        this.enterButton.setEnabled(true);
        this.enterButton.setText("ENTER");
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
        branchLabel = new javax.swing.JLabel();
        cashAccountLabel = new javax.swing.JLabel();
        asOnLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        orientationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchPanel = new javax.swing.JPanel();
        branchTbox = new javax.swing.JTextField();
        branchNameLabel = new javax.swing.JLabel();
        accountPanel = new javax.swing.JPanel();
        accountTbox = new javax.swing.JTextField();
        accountNameLabel = new javax.swing.JLabel();
        datePicker = new javax.swing.JFormattedTextField();
        paperCbox = new javax.swing.JComboBox<>();
        orientationCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Party Wise Statements");
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

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        cashAccountLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        cashAccountLabel.setText("Account");
        labelsPanel.add(cashAccountLabel);

        asOnLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        asOnLabel.setText("Date");
        labelsPanel.add(asOnLabel);

        paperLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        paperLabel.setText("Paper");
        labelsPanel.add(paperLabel);

        orientationLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        orientationLabel.setText("Orientation");
        labelsPanel.add(orientationLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        branchPanel.setLayout(new java.awt.BorderLayout());

        branchTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        branchTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        branchTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                branchTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                branchTboxFocusLost(evt);
            }
        });
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

        accountPanel.setLayout(new java.awt.BorderLayout());

        accountTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        accountTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        accountTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                accountTboxFocusGained(evt);
            }
        });
        accountTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                accountTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                accountTboxKeyReleased(evt);
            }
        });
        accountPanel.add(accountTbox, java.awt.BorderLayout.LINE_START);

        accountNameLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        accountPanel.add(accountNameLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(accountPanel);

        try {
            datePicker.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        datePicker.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        datePicker.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                datePickerFocusGained(evt);
            }
        });
        datePicker.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(datePicker);

        paperCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        paperCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A4", "Legal" }));
        paperCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(paperCbox);

        orientationCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        orientationCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Landscape", "Portrait" }));
        orientationCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(orientationCbox);

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
        titleLabel.setText("PARTY WISE STATEMENT");
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        generateReport();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.generateReport();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocusBackward();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void branchTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusLost
        this.validateBranch();
        this.loadAccountData();
    }//GEN-LAST:event_branchTboxFocusLost

    private void branchTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            this.chooseBranch();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
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
    }//GEN-LAST:event_branchTboxKeyPressed

    private void branchTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchTboxKeyReleased
        this.validateBranch();
    }//GEN-LAST:event_branchTboxKeyReleased

    private void accountTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            chooseAccount();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
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
    }//GEN-LAST:event_accountTboxKeyPressed

    private void accountTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountTboxKeyReleased
        this.validateAccount();        // TODO add your handling code here:
    }//GEN-LAST:event_accountTboxKeyReleased

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
        }// TODO add your handling code here:
    }//GEN-LAST:event_keyPressedHandler

    private void datePickerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_datePickerFocusGained
        this.datePicker.setCaretPosition(0);        // TODO add your handling code here:
    }//GEN-LAST:event_datePickerFocusGained

    private void accountTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountTboxFocusGained
        this.accountTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_accountTboxFocusGained

    private void branchTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusGained
        this.branchTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_branchTboxFocusGained
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountNameLabel;
    private javax.swing.JPanel accountPanel;
    private javax.swing.JTextField accountTbox;
    private javax.swing.JLabel asOnLabel;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JLabel branchNameLabel;
    private javax.swing.JPanel branchPanel;
    private javax.swing.JTextField branchTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel cashAccountLabel;
    private javax.swing.JFormattedTextField datePicker;
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
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
