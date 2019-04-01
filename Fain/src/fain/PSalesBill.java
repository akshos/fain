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
import java.util.List;
import javax.swing.JOptionPane;
import reports.SalesBill;
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
     SalesBill.SalesHeader salesHeader;
     List<SalesBill.SalesEntry> salesEntries;
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
    
    public PSalesBill(Main frame, int level, SalesBill.SalesHeader header, List<SalesBill.SalesEntry> entries){
        this.level = level;
        this.mainFrame = frame;
        this.salesHeader = header;
        this.salesEntries = entries;
        initComponents();
        loadContents();
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.dateTbox.setText(date);
    }
    
    private void loadContents(){    
        this.dateTbox.setText(salesHeader.getInvoiceDate());
        this.invoiceNoTbox.setText(salesHeader.getInvoiceNumber());
        this.challanNoTbox.setText(salesHeader.getChallanNumber());
        this.transportationModeTbox.setText("");
    }

    

    @Override
    public void refreshContents(int type) {

    }
    
    
    private void printSalesBill(){
        this.salesHeader.setInvoiceNumber(this.invoiceNoTbox.getText());
        this.salesHeader.setChallanNumber(this.challanNoTbox.getText());
        this.salesHeader.setInvoiceDate(this.dateTbox.getText());
        this.salesHeader.setTimeOfSupply(this.timeOfSupplyTbox.getText());
        this.salesHeader.setTransportationMode(this.transportationModeTbox.getText());
        this.salesHeader.setVehicleNo(this.vehicleNoTbox.getText());
        
        SalesBill.createSalesBill(this.salesHeader, this.salesEntries);
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
        invoiceNoTbox = new javax.swing.JTextField();
        challanNoTbox = new javax.swing.JTextField();
        dateTbox = new javax.swing.JFormattedTextField();
        transportationModeTbox = new javax.swing.JTextField();
        vehicleNoTbox = new javax.swing.JTextField();
        timeOfSupplyTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Sales Bill");
        setPreferredSize(new java.awt.Dimension(450, 410));
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
        rightInerPannel.add(invoiceNoTbox);
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
        rightInerPannel.add(transportationModeTbox);
        rightInerPannel.add(vehicleNoTbox);
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
        printSalesBill();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            //ADD CODE HERE TO CONNECT FRONTEND AND BACKEND
            //CHECK AND DELETE insertData() if not required;
            //this.insertData();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel challanNoLabel;
    private javax.swing.JTextField challanNoTbox;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel invoiceNoLabel;
    private javax.swing.JTextField invoiceNoTbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel timeOfSupplyLabel;
    private javax.swing.JTextField timeOfSupplyTbox;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel transportationModeLabel;
    private javax.swing.JTextField transportationModeTbox;
    private javax.swing.JLabel vehicleNoLabel;
    private javax.swing.JTextField vehicleNoTbox;
    // End of variables declaration//GEN-END:variables

    
}