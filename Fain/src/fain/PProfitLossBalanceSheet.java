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
import reports.ProfitLossBalanceSheet;
import utility.UtilityFuncs;
import utility.Wait;
/**
 *
 * @author akshos
 */
public class PProfitLossBalanceSheet extends javax.swing.JInternalFrame{
    
    DBConnection dbConnection;
    int level;
    Main mainFrame;
    String branchData[][];
    String accountData[][];
    /**
     * Creates new form MasterEntry
     */
    public PProfitLossBalanceSheet() {
        initComponents();
    }
    
    public PProfitLossBalanceSheet(DBConnection db, Main frame, int level){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        initComponents();
        loadCurrDate();
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.asOnTbox.setText(date);
    }
    
    private void generateReport(){    
        setBusy();
        
        String asOnDate=this.asOnTbox.getText();
        if(!ValidationChecks.isDateValid(asOnDate)){
            JOptionPane.showMessageDialog(this, "Please enter valid Date", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final String date = UtilityFuncs.dateUserToSql(asOnDate);
        
        final String paper = this.paperCbox.getSelectedItem().toString();
        final String orientation = this.orientationCbox.getSelectedItem().toString();
        
        
        Thread t;
        t = new Thread(new Runnable(){
            public void run(){
                Wait wait = new Wait();
                wait.setSize(new Dimension(700, 400));
                wait.setVisible(true);
                mainFrame.addToMainDesktopPane(wait, level+1, Codes.NO_DATABASE);
                boolean ret = ProfitLossBalanceSheet.createReport(dbConnection, paper, orientation, date);
                wait.closeWait();
            }
        });
        t.start();
        resetBusy();
    }
    
    private void setBusy(){
        this.enterButton.setText("Please Wait");
        this.enterButton.setEnabled(false);
        this.setEnabled(false);
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.repaint();
    }
    
    private void resetBusy(){
        this.enterButton.setEnabled(true);
        this.enterButton.setText("ENTER");
        this.setEnabled(true);
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
        paperLabel = new javax.swing.JLabel();
        orientationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        asOnTbox = new javax.swing.JFormattedTextField();
        paperCbox = new javax.swing.JComboBox<>();
        orientationCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("P&L and Balance Sheet");
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

        labelsPanel.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        asOnLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        asOnLabel.setText("As on");
        labelsPanel.add(asOnLabel);

        paperLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        paperLabel.setText("Paper");
        labelsPanel.add(paperLabel);

        orientationLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        orientationLabel.setText("Orientation");
        labelsPanel.add(orientationLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        try {
            asOnTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        asOnTbox.setFocusable(false);
        asOnTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        rightInerPannel.add(asOnTbox);

        paperCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        paperCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A4", "Legal" }));
        paperCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                paperCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(paperCbox);

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
        titleLabel.setText("P/L & BALANCE SHEET");
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
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel asOnLabel;
    private javax.swing.JFormattedTextField asOnTbox;
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
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
