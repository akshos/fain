/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fain;

import database.BarrelDB;
import database.BranchDB;
import database.CompanyBarrelDB;
import database.CustomerDB;
import database.DBConnection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.UtilityFuncs;
import utility.ValidationChecks;
/**
 *
 * @author akshos
 */
public class PSalesBill extends javax.swing.JInternalFrame implements RefreshOption{

     DBConnection dbConnection;
     Main mainFrame;
     int level;
     int mode;
     String editId;
     RefreshOption prevFrame;
     String branchData[][];
     String partyData[][];
    /**
     * Creates new form MasterEntry
     */
    public PSalesBill() {
        initComponents();
    }
    public PSalesBill(DBConnection db, int mode, String id, Main frame, int level){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        this.editId=id;
        initComponents();
        loadCurrDate();
        refreshContents(Codes.REFRESH_ALL);
        prevFrame = null;
    }
    
    public PSalesBill(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        this.editId=id;
        initComponents();
        loadCurrDate();
        if(mode == Codes.EDIT) {this.loadContents(); System.out.println("editmode");}
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.dateTbox.setText(date);
    }
    
    private void loadContents(){
        String[] data = CompanyBarrelDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
       
        this.dateTbox.setText(UtilityFuncs.dateSqlToUser(data[1]));
        this.invoiceNoTbox.setText(data[2]);
        this.challanNoTbox.setText(data[3]);
        this.transportationModeTbox.setText(data[4]);
    }

    
    private void setDifference(){
        int issued, lifted, difference;
        try{
            issued = Integer.parseInt(this.invoiceNoTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting issued to 0");
            issued = 0;
        }
        try{
            lifted = Integer.parseInt(this.challanNoTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting lifted to 0");
            lifted = 0;
        }
        difference = issued - lifted;
        this.transportationModeTbox.setText(String.valueOf(difference));
    }
    
    @Override
    public void refreshContents(int type) {

    }
    
    
    private void insertData(){
        Statement stmt = dbConnection.getStatement();
        
        
        String date= this.dateTbox.getText();

        if(!ValidationChecks.isDateValid(date)){
            JOptionPane.showMessageDialog(this, "Please enter a valid Date", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        date = UtilityFuncs.dateUserToSql(date);
        System.out.println("Date : " + date);
        
        int issued, lifted, difference;

        try{
            issued = Integer.parseInt(this.invoiceNoTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting issued to 0");
            issued = 0;
        }
        try{
            lifted = Integer.parseInt(this.challanNoTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting lifted to 0");
            lifted = 0;
        }
        difference = issued - lifted;
  
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = CompanyBarrelDB.update(stmt, this.editId, date, String.valueOf(issued), String.valueOf(lifted), "0", String.valueOf(difference));
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
        }
        else{
            ret = CompanyBarrelDB.insert(stmt, date, String.valueOf(issued), String.valueOf(lifted), "0", String.valueOf(difference));
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }    
        }
        
        if(prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_BARREL);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void nextEntry(){
        this.invoiceNoTbox.setText("");
        this.challanNoTbox.setText("");
        this.transportationModeTbox.setText("");
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
        invoiceNoLabel = new javax.swing.JLabel();
        challanNoLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        transportationModeLabel = new javax.swing.JLabel();
        vehicleNoLabel = new javax.swing.JLabel();
        timeOfSupplyLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        invoiceNoTbox = new javax.swing.JFormattedTextField();
        challanNoTbox = new javax.swing.JFormattedTextField();
        dateTbox = new javax.swing.JFormattedTextField();
        transportationModeTbox = new javax.swing.JFormattedTextField();
        vehicleNoTbox = new javax.swing.JFormattedTextField();
        timeOfSupplyTbox = new javax.swing.JFormattedTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Sales Bill");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
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

        invoiceNoLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        invoiceNoLabel.setText("Invoice No:");
        labelsPanel.add(invoiceNoLabel);

        challanNoLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        challanNoLabel.setText("Challan No:");
        labelsPanel.add(challanNoLabel);

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dateLabel.setText("Invoice Date:");
        labelsPanel.add(dateLabel);

        transportationModeLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        transportationModeLabel.setText("Transportation Mode:");
        labelsPanel.add(transportationModeLabel);

        vehicleNoLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        vehicleNoLabel.setText("Vehicle No:");
        labelsPanel.add(vehicleNoLabel);

        timeOfSupplyLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        timeOfSupplyLabel.setText("Time of Supply:");
        labelsPanel.add(timeOfSupplyLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

        invoiceNoTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        invoiceNoTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        invoiceNoTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                invoiceNoTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                invoiceNoTboxFocusLost(evt);
            }
        });
        invoiceNoTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceNoTboxActionPerformed(evt);
            }
        });
        invoiceNoTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(invoiceNoTbox);

        challanNoTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        challanNoTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        challanNoTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                challanNoTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                challanNoTboxFocusLost(evt);
            }
        });
        challanNoTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                challanNoTboxActionPerformed(evt);
            }
        });
        challanNoTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(challanNoTbox);

        try {
            dateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        dateTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        dateTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dateTboxFocusGained(evt);
            }
        });
        dateTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateTboxActionPerformed(evt);
            }
        });
        dateTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(dateTbox);

        transportationModeTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        transportationModeTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        transportationModeTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                transportationModeTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                transportationModeTboxFocusLost(evt);
            }
        });
        transportationModeTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transportationModeTboxActionPerformed(evt);
            }
        });
        transportationModeTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(transportationModeTbox);

        vehicleNoTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        vehicleNoTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        vehicleNoTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                vehicleNoTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                vehicleNoTboxFocusLost(evt);
            }
        });
        vehicleNoTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vehicleNoTboxActionPerformed(evt);
            }
        });
        vehicleNoTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                vehicleNoTboxkeyPressedHandler(evt);
            }
        });
        rightInerPannel.add(vehicleNoTbox);

        timeOfSupplyTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        timeOfSupplyTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        timeOfSupplyTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                timeOfSupplyTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeOfSupplyTboxFocusLost(evt);
            }
        });
        timeOfSupplyTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeOfSupplyTboxActionPerformed(evt);
            }
        });
        timeOfSupplyTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                timeOfSupplyTboxkeyPressedHandler(evt);
            }
        });
        rightInerPannel.add(timeOfSupplyTbox);

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
        titleLabel.setText("Sales Bill");
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void invoiceNoTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceNoTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_invoiceNoTboxActionPerformed

    private void challanNoTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_challanNoTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_challanNoTboxActionPerformed

    private void transportationModeTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transportationModeTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transportationModeTboxActionPerformed

    private void challanNoTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_challanNoTboxFocusGained
        this.challanNoTbox.selectAll();
    }//GEN-LAST:event_challanNoTboxFocusGained

    private void invoiceNoTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_invoiceNoTboxFocusGained
        this.invoiceNoTbox.selectAll();
    }//GEN-LAST:event_invoiceNoTboxFocusGained

    private void transportationModeTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_transportationModeTboxFocusGained
        this.setDifference();
        this.transportationModeTbox.selectAll();
    }//GEN-LAST:event_transportationModeTboxFocusGained

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

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosing

    private void dateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxActionPerformed

    private void dateTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dateTboxFocusGained
        this.dateTbox.setCaretPosition(0);        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxFocusGained

    private void transportationModeTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_transportationModeTboxFocusLost
                        // TODO add your handling code here:
    }//GEN-LAST:event_transportationModeTboxFocusLost

    private void invoiceNoTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_invoiceNoTboxFocusLost
                // TODO add your handling code here:
    }//GEN-LAST:event_invoiceNoTboxFocusLost

    private void challanNoTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_challanNoTboxFocusLost
        this.setDifference();
    }//GEN-LAST:event_challanNoTboxFocusLost

    private void timeOfSupplyTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_timeOfSupplyTboxFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_timeOfSupplyTboxFocusGained

    private void timeOfSupplyTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_timeOfSupplyTboxFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_timeOfSupplyTboxFocusLost

    private void timeOfSupplyTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeOfSupplyTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timeOfSupplyTboxActionPerformed

    private void timeOfSupplyTboxkeyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeOfSupplyTboxkeyPressedHandler
        // TODO add your handling code here:
    }//GEN-LAST:event_timeOfSupplyTboxkeyPressedHandler

    private void vehicleNoTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_vehicleNoTboxFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_vehicleNoTboxFocusGained

    private void vehicleNoTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_vehicleNoTboxFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_vehicleNoTboxFocusLost

    private void vehicleNoTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehicleNoTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_vehicleNoTboxActionPerformed

    private void vehicleNoTboxkeyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_vehicleNoTboxkeyPressedHandler
        // TODO add your handling code here:
    }//GEN-LAST:event_vehicleNoTboxkeyPressedHandler


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel challanNoLabel;
    private javax.swing.JFormattedTextField challanNoTbox;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel invoiceNoLabel;
    private javax.swing.JFormattedTextField invoiceNoTbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel timeOfSupplyLabel;
    private javax.swing.JFormattedTextField timeOfSupplyTbox;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel transportationModeLabel;
    private javax.swing.JFormattedTextField transportationModeTbox;
    private javax.swing.JLabel vehicleNoLabel;
    private javax.swing.JFormattedTextField vehicleNoTbox;
    // End of variables declaration//GEN-END:variables

    
}
