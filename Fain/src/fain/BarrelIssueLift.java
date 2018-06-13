/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fain;

import database.BarrelDB;
import database.BranchDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.PurchaseDB;
import database.PurchaseLatexDB;
import database.StockDB;
import database.TransactionDB;
import java.awt.Dimension;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
import reports.PurchaseBill;
import utility.Codes;
import utility.UtilityFuncs;
import utility.ValidationChecks;
/**
 *
 * @author akshos
 */
public class BarrelIssueLift extends javax.swing.JInternalFrame implements RefreshOption{

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
    public BarrelIssueLift() {
        initComponents();
    }
    public BarrelIssueLift(DBConnection db, int mode, String id, Main frame, int level){
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
    
    public BarrelIssueLift(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
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
        String[] data = BarrelDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        
        this.loadBranchData();
        this.branchTbox.setText(data[1]);
        this.validateBranch();

        this.dateTbox.setText(UtilityFuncs.dateSqlToUser(data[3]));
       
        this.loadPartyData();
        this.partyTbox.setText(data[2]);
        this.validateParty();
        
        this.stockTbox.setText(data[4]);
        this.barrelsIssuedTbox.setText(data[5]);
        this.barrelsLiftedTbox.setText(data[6]);
        this.differenceTbox.setText(data[7]);
    }
    
     private void loadPartyData(){
        String branchCode = this.branchTbox.getText();
        partyData = CustomerDB.getCustomersInBranch(this.dbConnection.getStatement(), branchCode);   
    }
    
    private boolean validateParty(){
        String accCode = this.partyTbox.getText();
        String accName = CustomerDB.getCustomerName(dbConnection.getStatement(), accCode);
        if(accName != null){
            this.partyNameLabel.setText(accName);
            return true;
        }else{
            this.partyNameLabel.setText("NOT FOUND");
            return false;
        }
    }
    
    private void chooseParty(){
        int index = UtilityFuncs.selectOption(this, " PARTY", partyData);
        if(index != -1){
            this.partyTbox.setText(partyData[0][index]);
            this.partyNameLabel.setText(partyData[1][index]);
        }
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
    
    @Override
    public void refreshContents(int type) {
        if(type == Codes.REFRESH_ALL){
            loadBranchData();
            loadPartyData();
        }
        else if(type == Codes.REFRESH_BRANCHES){
            loadBranchData();
        }
        else if(type == Codes.REFRESH_MASTER){
            loadPartyData();
        }
    }
    
    
    private void insertData(){
        Statement stmt = dbConnection.getStatement();
        
        String branch = branchTbox.getText();
        if(!validateBranch()){
            JOptionPane.showMessageDialog(this, "Please enter a valid BRANCH", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String date= this.dateTbox.getText();

        if(!ValidationChecks.isDateValid(date)){
            JOptionPane.showMessageDialog(this, "Please enter a valid Date", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        date = UtilityFuncs.dateUserToSql(date);
        System.out.println("Date : " + date);
        
        String party = partyTbox.getText();
        if(!validateParty()){
            JOptionPane.showMessageDialog(this, "Please enter a valid PARTY", "PARTY NOT FOUND", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double stock,barrelsIssued,barrelsLifted,difference;
        if(stockTbox.getText().compareTo("")==0)
            stock=0.0;
        else
            stock =Double.parseDouble(stockTbox.getText().replace(",",""));
        if(barrelsIssuedTbox.getText().compareTo("")==0)
            barrelsIssued=0.0;
        else
            barrelsIssued      =Double.parseDouble(barrelsIssuedTbox.getText().replace(",",""));
        if(barrelsLiftedTbox.getText().compareTo("")==0)
            barrelsLifted=0;
        else
            barrelsLifted=Double.parseDouble(barrelsLiftedTbox.getText().replace(",",""));
        if(differenceTbox.getText().compareTo("")==0)
            difference=0;
        else
             difference     =Double.parseDouble(differenceTbox.getText().replace(",",""));
  
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = BarrelDB.update(stmt, this.editId, branch, party, date,String.valueOf(stock),String.valueOf(barrelsIssued),String.valueOf(barrelsLifted),String.valueOf(difference));
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
        }
        else{
            ret = BarrelDB.insert(stmt, branch, party, date,String.valueOf(stock),String.valueOf(barrelsIssued),String.valueOf(barrelsLifted),String.valueOf(difference));
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
        }
        
        if(prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_PLATEX);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void nextEntry(){
        this.branchTbox.requestFocus();
        this.partyTbox.setText("");
        this.stockTbox.setText("");
        this.barrelsIssuedTbox.setText("");
        this.barrelsLiftedTbox.setText("");
        this.differenceTbox.setText("");
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
        partyLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        drcLabel = new javax.swing.JLabel();
        dryrubberLabel = new javax.swing.JLabel();
        rateLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchPanel = new javax.swing.JPanel();
        branchTbox = new javax.swing.JTextField();
        branchNameLabel = new javax.swing.JLabel();
        partyPanel = new javax.swing.JPanel();
        partyTbox = new javax.swing.JTextField();
        partyNameLabel = new javax.swing.JLabel();
        dateTbox = new javax.swing.JFormattedTextField();
        stockTbox = new javax.swing.JFormattedTextField();
        barrelsIssuedTbox = new javax.swing.JFormattedTextField();
        barrelsLiftedTbox = new javax.swing.JFormattedTextField();
        differenceTbox = new javax.swing.JFormattedTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Barrel Issue Lift");
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

        labelsPanel.setLayout(new java.awt.GridLayout(8, 0, 0, 10));

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        partyLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        partyLabel.setText("Party");
        labelsPanel.add(partyLabel);

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        quantityLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        quantityLabel.setText("Stock");
        labelsPanel.add(quantityLabel);

        drcLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        drcLabel.setText("Barrels Issued");
        labelsPanel.add(drcLabel);

        dryrubberLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dryrubberLabel.setText("Barrels Lifted");
        labelsPanel.add(dryrubberLabel);

        rateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        rateLabel.setText("Difference");
        labelsPanel.add(rateLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(8, 0, 0, 10));

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
        branchTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchTboxActionPerformed(evt);
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

        partyPanel.setLayout(new java.awt.BorderLayout());

        partyTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        partyTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        partyTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                partyTboxFocusGained(evt);
            }
        });
        partyTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                partyTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                partyTboxKeyReleased(evt);
            }
        });
        partyPanel.add(partyTbox, java.awt.BorderLayout.LINE_START);

        partyNameLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        partyPanel.add(partyNameLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(partyPanel);

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

        stockTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        stockTbox.setText("0");
        stockTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        stockTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                stockTboxFocusGained(evt);
            }
        });
        stockTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockTboxActionPerformed(evt);
            }
        });
        stockTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(stockTbox);

        barrelsIssuedTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        barrelsIssuedTbox.setText("0");
        barrelsIssuedTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        barrelsIssuedTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barrelsIssuedTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                barrelsIssuedTboxFocusLost(evt);
            }
        });
        barrelsIssuedTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barrelsIssuedTboxActionPerformed(evt);
            }
        });
        barrelsIssuedTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelsIssuedTbox);

        barrelsLiftedTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        barrelsLiftedTbox.setText("0");
        barrelsLiftedTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        barrelsLiftedTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barrelsLiftedTboxFocusGained(evt);
            }
        });
        barrelsLiftedTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barrelsLiftedTboxActionPerformed(evt);
            }
        });
        barrelsLiftedTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelsLiftedTbox);

        differenceTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        differenceTbox.setText("0");
        differenceTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        differenceTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                differenceTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                differenceTboxFocusLost(evt);
            }
        });
        differenceTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                differenceTboxActionPerformed(evt);
            }
        });
        differenceTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(differenceTbox);

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
        titleLabel.setText("BARREL ISSUE/LIFT");
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

    private void stockTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockTboxActionPerformed

    private void barrelsIssuedTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barrelsIssuedTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barrelsIssuedTboxActionPerformed

    private void barrelsLiftedTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barrelsLiftedTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barrelsLiftedTboxActionPerformed

    private void differenceTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_differenceTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_differenceTboxActionPerformed

    private void barrelsLiftedTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelsLiftedTboxFocusGained
        this.barrelsLiftedTbox.selectAll();
    }//GEN-LAST:event_barrelsLiftedTboxFocusGained

    private void stockTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stockTboxFocusGained
        this.stockTbox.selectAll();
    }//GEN-LAST:event_stockTboxFocusGained

    private void barrelsIssuedTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelsIssuedTboxFocusGained
        this.barrelsIssuedTbox.selectAll();
    }//GEN-LAST:event_barrelsIssuedTboxFocusGained

    private void differenceTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_differenceTboxFocusGained
        this.differenceTbox.selectAll();
    }//GEN-LAST:event_differenceTboxFocusGained

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

    private void differenceTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_differenceTboxFocusLost
                        // TODO add your handling code here:
    }//GEN-LAST:event_differenceTboxFocusLost

    private void barrelsIssuedTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelsIssuedTboxFocusLost
                // TODO add your handling code here:
    }//GEN-LAST:event_barrelsIssuedTboxFocusLost

    private void branchTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusLost
        this.loadPartyData();        // TODO add your handling code here:
    }//GEN-LAST:event_branchTboxFocusLost

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

    private void partyTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_partyTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            chooseParty();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
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
    }//GEN-LAST:event_partyTboxKeyPressed

    private void partyTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_partyTboxKeyReleased
        this.validateParty();        // TODO add your handling code here:
    }//GEN-LAST:event_partyTboxKeyReleased

    private void branchTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusGained
        this.branchTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_branchTboxFocusGained

    private void partyTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_partyTboxFocusGained
        this.partyTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_partyTboxFocusGained

    private void branchTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_branchTboxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField barrelsIssuedTbox;
    private javax.swing.JFormattedTextField barrelsLiftedTbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JLabel branchNameLabel;
    private javax.swing.JPanel branchPanel;
    private javax.swing.JTextField branchTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JFormattedTextField differenceTbox;
    private javax.swing.JLabel drcLabel;
    private javax.swing.JLabel dryrubberLabel;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JLabel partyLabel;
    private javax.swing.JLabel partyNameLabel;
    private javax.swing.JPanel partyPanel;
    private javax.swing.JTextField partyTbox;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JFormattedTextField stockTbox;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables

    
}
