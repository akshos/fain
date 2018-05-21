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
import utility.ValidationChecks;
import reports.CashBankAccount;
import reports.DayBook;
import utility.UtilityFuncs;
import utility.Wait;
/**
 *
 * @author akshos
 */
public class PAccountBooks extends javax.swing.JInternalFrame{
    
    DBConnection dbConnection;
    int level;
    Main mainFrame;
    String accountData[][];
    String type;
    /**
     * Creates new form MasterEntry
     */
    public PAccountBooks() {
        initComponents();
    }
    
    public PAccountBooks(DBConnection db, Main frame, int level, String type){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        this.type = type;
        initComponents();
        loadCurrDate();
        loadAccountData();
        setType();
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.fromDatePicker.setText(date);
        this.toDatePicker.setText(date);
    }
    
    private void setType(){
        if(this.type.compareTo("DAY") == 0){
            this.setTitle("DAY BOOK");
            this.titleLabel.setText("DAY BOOK");
        }else if(this.type.compareTo("CH") == 0){
            this.setTitle("CASH BOOK");
            this.titleLabel.setText("CASH BOOK");
        }else if(this.type.compareTo("BK") == 0){
            this.setTitle("BANK BOOK");
            this.titleLabel.setText("BANK BOOK");
        }
    }
    
    private void loadAccountData(){
        String cat;
        if(this.type.compareTo("DAY") == 0){
            cat = "CH";
        }else{
            cat = type;
        }
        accountData = MasterDB.getAccountHeadByCat(this.dbConnection.getStatement(), cat);
    }
    
    private boolean validateAccount(){
        String accCode = this.accountTbox.getText();
        String accName = MasterDB.getAccountHead(dbConnection.getStatement(), accCode);
        if(accName != null){
            this.accountNameTbox.setText(accName);
            return true;
        }else{
            this.accountNameTbox.setText("NOT FOUND");
            return false;
        }
    }
    
    private void chooseAccount(){
        int index = UtilityFuncs.selectOption(this, " ACCOUNT", accountData);
        if(index != -1){
            this.accountTbox.setText(accountData[0][index]);
            this.accountNameTbox.setText(accountData[1][index]);
        }
    }
    
    private void generateReport(){    
        setBusy();
        
        final String account = this.accountTbox.getText();
        if(!validateAccount()){
            JOptionPane.showMessageDialog(this, "Please enter a valid Account", "No Account", JOptionPane.WARNING_MESSAGE);
            return;
        }        
        
        String fromDate=this.fromDatePicker.getText();
        if(!ValidationChecks.isDateValid(fromDate)){
            JOptionPane.showMessageDialog(this, "Please enter valid Date From", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        fromDate = UtilityFuncs.dateUserToSql(fromDate);
        System.out.println("From Date " + fromDate);
        
        String toDate=this.toDatePicker.getText();
        if(!ValidationChecks.isDateValid(toDate)){
            JOptionPane.showMessageDialog(this, "Please enter valid Date To", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }       
        toDate = UtilityFuncs.dateUserToSql(toDate);
        
        final String dateFrom = fromDate;
        final String dateTo = dateFrom;
        final String paper = this.paperCbox.getSelectedItem().toString();
        final String orientation = this.orientationCbox.getSelectedItem().toString();
        Thread t;
        t = new Thread(new Runnable(){
            public void run(){
                Wait wait = new Wait();
                wait.setSize(new Dimension(700, 400));
                wait.setVisible(true);
                mainFrame.addToMainDesktopPane(wait, level+1, Codes.NO_DATABASE);
                if(type.compareTo("DAY") == 0){
                    boolean ret = DayBook.createReport(dbConnection, paper, orientation, dateFrom, dateTo, account);
                }else{
                    boolean ret = CashBankAccount.createReport(dbConnection, paper, orientation, dateFrom, dateTo, account, type);
                }
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
        cashAccountLabel = new javax.swing.JLabel();
        dateFromLabel = new javax.swing.JLabel();
        asOnLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        orientationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        accountPanel = new javax.swing.JPanel();
        accountTbox = new javax.swing.JTextField();
        accountNameTbox = new javax.swing.JLabel();
        fromDatePicker = new javax.swing.JFormattedTextField();
        toDatePicker = new javax.swing.JFormattedTextField();
        paperCbox = new javax.swing.JComboBox<>();
        orientationCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Account Book");
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

        cashAccountLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        cashAccountLabel.setText("Account");
        labelsPanel.add(cashAccountLabel);

        dateFromLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dateFromLabel.setText("Date from");
        labelsPanel.add(dateFromLabel);

        asOnLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        asOnLabel.setText("Date To");
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

        accountNameTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        accountPanel.add(accountNameTbox, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(accountPanel);

        try {
            fromDatePicker.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        fromDatePicker.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        fromDatePicker.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fromDatePickerFocusGained(evt);
            }
        });
        fromDatePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromDatePickerActionPerformed(evt);
            }
        });
        fromDatePicker.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(fromDatePicker);

        try {
            toDatePicker.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        toDatePicker.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        toDatePicker.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                toDatePickerFocusGained(evt);
            }
        });
        toDatePicker.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(toDatePicker);

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
                enterButtonKeyPressed(evt);
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
        titleLabel.setText("ACCOUNT BOOK");
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

    private void fromDatePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromDatePickerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fromDatePickerActionPerformed

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
        }
    }//GEN-LAST:event_keyPressedHandler

    private void fromDatePickerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fromDatePickerFocusGained
        this.fromDatePicker.setCaretPosition(0);
    }//GEN-LAST:event_fromDatePickerFocusGained

    private void toDatePickerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_toDatePickerFocusGained
        this.toDatePicker.setCaretPosition(0);        // TODO add your handling code here:
    }//GEN-LAST:event_toDatePickerFocusGained

    private void accountTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountTboxFocusGained
        this.accountTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_accountTboxFocusGained
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountNameTbox;
    private javax.swing.JPanel accountPanel;
    private javax.swing.JTextField accountTbox;
    private javax.swing.JLabel asOnLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel cashAccountLabel;
    private javax.swing.JLabel dateFromLabel;
    private javax.swing.JButton enterButton;
    private javax.swing.JFormattedTextField fromDatePicker;
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
    private javax.swing.JFormattedTextField toDatePicker;
    // End of variables declaration//GEN-END:variables
}
