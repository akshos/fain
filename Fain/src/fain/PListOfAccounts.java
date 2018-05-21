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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.ValidationChecks;
import reports.ListOfAccounts;
import utility.UtilityFuncs;
import utility.Wait;
/**
 *
 * @author akshos
 */
public class PListOfAccounts extends javax.swing.JInternalFrame{
    
    DBConnection dbConnection;
    int level;
    Main mainFrame;
    String branchData[][];
    String accountData[][];
    /**
     * Creates new form MasterEntry
     */
    public PListOfAccounts() {
        initComponents();
    }
    
    public PListOfAccounts(DBConnection db, Main frame, int level){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        initComponents();
        loadAccountData();
        loadCurrDate();
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.asOnTbox.setText(date);
    }
    
    private void loadAccountData(){
        accountData = MasterDB.getAccountHead(dbConnection.getStatement());
    }
    
    private boolean validateFromAccount(){
        String accCode = this.fromAccountTbox.getText();
        if(accCode.isEmpty())
            return true;
        String accName = MasterDB.getAccountHead(dbConnection.getStatement(), accCode);
        if(accName != null){
            this.fromAccountLabel.setText(accName);
            return true;
        }
        else{
            this.fromAccountLabel.setText("NOT FOUND");
            return false;
        }
    }
    
    private boolean validateToAccount(){
        String accCode = this.toAccountTbox.getText();
        if(accCode.isEmpty())
            return true;
        String accName = MasterDB.getAccountHead(dbConnection.getStatement(), accCode);
        if(accName != null){
            this.toAccountLabel.setText(accName);
            return true;
        }
        else{
            this.toAccountLabel.setText("NOT FOUND");
            return false;
        }
    }
    
    private void chooseFromAccount(){
       int index = UtilityFuncs.selectOption(this, " ACCOUNT", accountData);
       if(index != -1){
           this.fromAccountLabel.setText(accountData[1][index]);
           this.fromAccountTbox.setText(accountData[0][index]);
       }
    }
    
    private void chooseToAccount(){
       int index = UtilityFuncs.selectOption(this, " ACCOUNT", accountData);
       if(index != -1){
           this.toAccountLabel.setText(accountData[1][index]);
           this.toAccountTbox.setText(accountData[0][index]);
       }
    }
    
    private void generateReport(){    
        setBusy();
        
        String asOnDate = this.asOnTbox.getText();
        if(!ValidationChecks.isDateValid(asOnDate)){
            JOptionPane.showMessageDialog(this, "Enter a valid Date", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if(accountData == null){
            JOptionPane.showMessageDialog(this, "No Accounts Available", "No Accounts", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String accountFrom = this.fromAccountTbox.getText().trim();
        if(accountFrom.isEmpty()){
            accountFrom = accountData[0][0];
        }else if(!validateFromAccount()){
            JOptionPane.showMessageDialog(this, "Please enter a valid Account From", "No Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String accountTo = this.toAccountTbox.getText().trim();
        if(accountTo.isEmpty()){
            accountTo = accountData[0][accountData[0].length-1];
        }else if(!validateToAccount()){
            JOptionPane.showMessageDialog(this, "Please enter a valid Account To", "No Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        final String date = UtilityFuncs.dateUserToSql(asOnDate);
        final String toAccount = accountTo;
        final String fromAccount = accountFrom;
        final String paper = this.paperCbox.getSelectedItem().toString();
        final String orientation = this.orientationCbox.getSelectedItem().toString();
        Thread t;
        t = new Thread(new Runnable(){
            public void run(){
                Wait wait = new Wait();
                wait.setSize(new Dimension(700, 400));
                wait.setVisible(true);
                mainFrame.addToMainDesktopPane(wait, level+1, Codes.NO_DATABASE);
        
                ListOfAccounts.createReport(dbConnection, wait, paper, orientation, date, fromAccount, toAccount);
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
        asOnLabel = new javax.swing.JLabel();
        accountHeadLabel = new javax.swing.JLabel();
        yopBalLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        orientationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        asOnTbox = new javax.swing.JFormattedTextField();
        fromAccount = new javax.swing.JPanel();
        fromAccountTbox = new javax.swing.JTextField();
        fromAccountLabel = new javax.swing.JLabel();
        toAccount = new javax.swing.JPanel();
        toAccountTbox = new javax.swing.JTextField();
        toAccountLabel = new javax.swing.JLabel();
        paperCbox = new javax.swing.JComboBox<>();
        orientationCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("List Of Accounts");
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

        asOnLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        asOnLabel.setText("As On");
        labelsPanel.add(asOnLabel);

        accountHeadLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        accountHeadLabel.setText("Account From");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        yopBalLabel.setText("Account To");
        labelsPanel.add(yopBalLabel);

        paperLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        paperLabel.setText("Paper");
        labelsPanel.add(paperLabel);

        orientationLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        orientationLabel.setText("Orientation");
        labelsPanel.add(orientationLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        try {
            asOnTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        asOnTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        asOnTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                asOnTboxFocusGained(evt);
            }
        });
        asOnTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                asOnTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(asOnTbox);

        fromAccount.setLayout(new java.awt.BorderLayout());

        fromAccountTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        fromAccountTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        fromAccountTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fromAccountTboxFocusGained(evt);
            }
        });
        fromAccountTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fromAccountTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fromAccountTboxKeyReleased(evt);
            }
        });
        fromAccount.add(fromAccountTbox, java.awt.BorderLayout.LINE_START);

        fromAccountLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        fromAccount.add(fromAccountLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(fromAccount);

        toAccount.setLayout(new java.awt.BorderLayout());

        toAccountTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        toAccountTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        toAccountTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                toAccountTboxFocusGained(evt);
            }
        });
        toAccountTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                toAccountTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                toAccountTboxKeyReleased(evt);
            }
        });
        toAccount.add(toAccountTbox, java.awt.BorderLayout.LINE_START);

        toAccountLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        toAccount.add(toAccountLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(toAccount);

        paperCbox.setBackground(java.awt.Color.white);
        paperCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        paperCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A4", "Legal" }));
        paperCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                paperCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(paperCbox);

        orientationCbox.setBackground(java.awt.Color.white);
        orientationCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        orientationCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Landscape", "Portrait" }));
        orientationCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orientationCboxKeyPressed(evt);
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
        titleLabel.setText("LIST OF ACCOUNTS");
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

    private void paperCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paperCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            paperCbox.transferFocus();
        }
    }//GEN-LAST:event_paperCboxKeyPressed

    private void orientationCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orientationCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            orientationCbox.transferFocus();
        }
    }//GEN-LAST:event_orientationCboxKeyPressed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.generateReport();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void fromAccountTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fromAccountTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            chooseFromAccount();
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
    }//GEN-LAST:event_fromAccountTboxKeyPressed

    private void fromAccountTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fromAccountTboxKeyReleased
        this.validateFromAccount();        // TODO add your handling code here:
    }//GEN-LAST:event_fromAccountTboxKeyReleased

    private void toAccountTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_toAccountTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            chooseToAccount();
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
    }//GEN-LAST:event_toAccountTboxKeyPressed

    private void toAccountTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_toAccountTboxKeyReleased
        this.validateToAccount();        // TODO add your handling code here:
    }//GEN-LAST:event_toAccountTboxKeyReleased

    private void asOnTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_asOnTboxFocusGained
        this.asOnTbox.setCaretPosition(0);        // TODO add your handling code here:
    }//GEN-LAST:event_asOnTboxFocusGained

    private void asOnTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_asOnTboxKeyPressed
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
    }//GEN-LAST:event_asOnTboxKeyPressed

    private void fromAccountTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fromAccountTboxFocusGained
        this.fromAccountTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_fromAccountTboxFocusGained

    private void toAccountTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_toAccountTboxFocusGained
        this.toAccountTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_toAccountTboxFocusGained
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JLabel asOnLabel;
    private javax.swing.JFormattedTextField asOnTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton enterButton;
    private javax.swing.JPanel fromAccount;
    private javax.swing.JLabel fromAccountLabel;
    private javax.swing.JTextField fromAccountTbox;
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
    private javax.swing.JPanel toAccount;
    private javax.swing.JLabel toAccountLabel;
    private javax.swing.JTextField toAccountTbox;
    private javax.swing.JLabel yopBalLabel;
    // End of variables declaration//GEN-END:variables
}
