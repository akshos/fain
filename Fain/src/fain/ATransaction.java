/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.PurchaseLatexDB;
import database.SalesDB;
import database.TransactionDB;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.UtilityFuncs;
/**
 *
 * @author akshos
 */
public class ATransaction extends javax.swing.JInternalFrame implements RefreshOption{
    int level;
    Main mainFrame;
    DBConnection dbConnection;
    RefreshOption prevFrame;
    String[][] accountData;
    String branchData[][];
    String editId;
    int mode;
    
    int selectedBranch;
    
    /**
     * Creates new form MasterEntry
     */
    public ATransaction() {
        initComponents();
    }
    
    public ATransaction(DBConnection db, int mode, String id, Main frame, int level){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        initComponents();
        loadCurrDate();
        refreshContents(Codes.REFRESH_ALL);
        this.prevFrame = null;
        this.editId=id;
        this.mode=mode;
    }
    
    public ATransaction(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.editId=id;
        this.mode=mode;
        initComponents();
        loadCurrDate();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.dateTbox.setText(date);
    }
    
    @Override
    public void refreshContents(int type){
        if(type == Codes.REFRESH_ALL){
            loadBranchData();
            loadAccountData();
        }
        else if(type == Codes.REFRESH_MASTER){
            loadAccountData();
        }
        else if(type == Codes.REFRESH_BRANCHES){
            loadBranchData();
        }
    }
    
    private void loadBranchData(){
        System.out.println("loading categorycbox");
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
        String[] data = TransactionDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        
        this.dateTbox.setText(UtilityFuncs.dateSqlToUser(data[1]));
        
        loadBranchData();
        
        this.branchTbox.setText(data[2]);
        this.validateBranch();
        
        loadAccountData();
        
        this.debitTbox.setText(data[3]);
        this.validateDebit();
        this.creditTbox.setText(data[4]);
        this.validateCredit();
        
        this.amountTbox.setText(data[5]);
        this.narrationTbox.setText(data[6]);
    }
    
    private void loadAccountData(){
        System.out.println("Loading Account Data");
        String branchCode = this.branchTbox.getText();

        String generalAccounts[][] = MasterDB.getGeneralAccountHeads(dbConnection.getStatement());
        String customerAccounts[][] = CustomerDB.getCustomersInBranch(dbConnection.getStatement(), branchCode);
        accountData = append(generalAccounts, customerAccounts);
        System.out.println("Account count : " + accountData[0].length);
    }
    
    private boolean validateCredit(){
        String accCode = this.creditTbox.getText();
        String accName = MasterDB.getAccountHead(dbConnection.getStatement(), accCode);
        if(accName != null){
            this.creditNameLabel.setText(accName);
            return true;
        }else{
            this.creditNameLabel.setText("NOT FOUND");
            return false;
        }
    }
    
    private void chooseDebit(){
        int index = UtilityFuncs.selectOption(this, " ACCOUNT", accountData);
        if(index != -1){
            this.debitTbox.setText(accountData[0][index]);
            this.debitNameLabel.setText(accountData[1][index]);
        }
    }
    
    private void chooseCredit(){
        int index = UtilityFuncs.selectOption(this, " ACCOUNT", accountData);
        if(index != -1){
            this.creditTbox.setText(accountData[0][index]);
            this.creditNameLabel.setText(accountData[1][index]);
        }
    }
    
    private boolean validateDebit(){
        String accCode = this.debitTbox.getText();
        String accName = MasterDB.getAccountHead(dbConnection.getStatement(), accCode);
        if(accName != null){
            this.debitNameLabel.setText(accName);
            return true;
        }else{
            this.debitNameLabel.setText("NOT FOUND");
            return false;
        }
    }
    
    public static String[][] append(String[][] a, String[][] b) {
        if(b == null){
            return a;
        }
        int len = a[0].length + b[0].length;
        int n = 0;
        String[][] c = new String[2][len];
        for(int i = 0; i < a[0].length; i++, n++){
            c[0][n] = a[0][i];
            c[1][n] = a[1][i];
        }
        for(int i = 0; i < b[0].length; i++, n++){
            c[0][n] = b[0][i];
            c[1][n] = b[1][i];
        }
        return c;
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=null;
        Date selDate=null;
        try {
            selDate = df.parse(dateTbox.getText());
            System.out.println(selDate);
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            String sqlDate=df1.format(selDate);
            System.out.println(sqlDate);
            date=sqlDate.toString();
        }catch (ParseException ex) {
            Logger.getLogger(APLatex.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(date == null){
            JOptionPane.showMessageDialog(this, "Please enter Date From", "NO DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String branchCode = this.branchTbox.getText();
        if(!validateBranch()){
            JOptionPane.showMessageDialog(this, "Please enter a valid branch", "No Branch", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String debit = this.debitTbox.getText();
        if(!validateDebit()){
            JOptionPane.showMessageDialog(this, "Please enter a valid DEBIT Account", "No debit account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String credit = this.creditTbox.getText();
        if(!validateCredit()){
            JOptionPane.showMessageDialog(this, "Please enter a valid CREDIT Account", "No credit account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount = Double.parseDouble(amountTbox.getText().replace(",",""));
        String narration = narrationTbox.getText();
        String tid = TransactionDB.generateTid();
        
        boolean ret;
        
        if(mode==Codes.EDIT){
            tid = TransactionDB.getTidFromTno(dbConnection.getStatement(), editId);
        
            int res = PurchaseLatexDB.checkTidCodePresent(dbConnection.getStatement(), tid);
            if(res == Codes.EXISTING_ENTRY){
                JOptionPane.showMessageDialog(this, "Please Edit from Purchase Latex", "CANNOT EDIT", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            res = SalesDB.checkTidCodePresent(dbConnection.getStatement(), tid);
            if(res == Codes.EXISTING_ENTRY){
                JOptionPane.showMessageDialog(this, "Please Edit from Sales Latex", "CANNOT EDIT", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            ret = TransactionDB.update(stmt, editId, date, branchCode, debit, credit, amount, narration);
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else{ 
            ret = TransactionDB.insert(stmt, date, branchCode, debit, credit, amount, narration, tid);
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_TRANSACTION);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void nextEntry(){
        this.dateTbox.requestFocus();
        this.amountTbox.setText("");
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
        dateLabel = new javax.swing.JLabel();
        branchLabel = new javax.swing.JLabel();
        debitLabel = new javax.swing.JLabel();
        creditLabel = new javax.swing.JLabel();
        amountLabel = new javax.swing.JLabel();
        narrationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        dateTbox = new javax.swing.JFormattedTextField();
        branchPanel = new javax.swing.JPanel();
        branchTbox = new javax.swing.JTextField();
        branchNameLabel = new javax.swing.JLabel();
        debitPanel = new javax.swing.JPanel();
        debitTbox = new javax.swing.JTextField();
        debitNameLabel = new javax.swing.JLabel();
        creditPanel = new javax.swing.JPanel();
        creditTbox = new javax.swing.JTextField();
        creditNameLabel = new javax.swing.JLabel();
        amountTbox = new javax.swing.JFormattedTextField();
        narrationTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Data Entry (Transaction)");
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

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        debitLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        debitLabel.setText("Debit ( R )");
        labelsPanel.add(debitLabel);

        creditLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        creditLabel.setText("Credit ( P )");
        labelsPanel.add(creditLabel);

        amountLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        amountLabel.setText("Amount");
        labelsPanel.add(amountLabel);

        narrationLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        narrationLabel.setText("Narration");
        labelsPanel.add(narrationLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

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
        dateTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(dateTbox);

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
        branchNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        branchPanel.add(branchNameLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(branchPanel);

        debitPanel.setLayout(new java.awt.BorderLayout());

        debitTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        debitTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        debitTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                debitTboxFocusGained(evt);
            }
        });
        debitTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                debitTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                debitTboxKeyReleased(evt);
            }
        });
        debitPanel.add(debitTbox, java.awt.BorderLayout.LINE_START);

        debitNameLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        debitPanel.add(debitNameLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(debitPanel);

        creditPanel.setLayout(new java.awt.BorderLayout());

        creditTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        creditTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        creditTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                creditTboxFocusGained(evt);
            }
        });
        creditTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                creditTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                creditTboxKeyReleased(evt);
            }
        });
        creditPanel.add(creditTbox, java.awt.BorderLayout.LINE_START);

        creditNameLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        creditPanel.add(creditNameLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(creditPanel);

        amountTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.00"))));
        amountTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        amountTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                amountTboxFocusGained(evt);
            }
        });
        amountTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(amountTbox);

        narrationTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        narrationTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                narrationTboxFocusGained(evt);
            }
        });
        narrationTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                enterButtonKeyPressed(evt);
            }
        });
        rightInerPannel.add(narrationTbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                buttonPanelKeyPressed(evt);
            }
        });
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
        titleLabel.setText("TRANSACTION");
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
       // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void buttonPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buttonPanelKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_buttonPanelKeyPressed

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

    private void keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedHandler
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
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
    }//GEN-LAST:event_keyPressedHandler

    private void amountTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_amountTboxFocusGained
        this.amountTbox.selectAll();
    }//GEN-LAST:event_amountTboxFocusGained

    private void narrationTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_narrationTboxFocusGained
        this.narrationTbox.selectAll();
    }//GEN-LAST:event_narrationTboxFocusGained

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosing

    private void dateTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dateTboxFocusGained
        this.dateTbox.setCaretPosition(0);        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxFocusGained

    private void branchTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchTboxKeyReleased
        this.validateBranch();
    }//GEN-LAST:event_branchTboxKeyReleased

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

    private void debitTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_debitTboxKeyReleased
        this.validateDebit();        // TODO add your handling code here:
    }//GEN-LAST:event_debitTboxKeyReleased

    private void creditTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_creditTboxKeyReleased
        this.validateCredit();        // TODO add your handling code here:
    }//GEN-LAST:event_creditTboxKeyReleased

    private void debitTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_debitTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            this.chooseDebit();
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
    }//GEN-LAST:event_debitTboxKeyPressed

    private void creditTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_creditTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            this.chooseCredit();
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
    }//GEN-LAST:event_creditTboxKeyPressed

    private void branchTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusLost
        this.loadAccountData();
    }//GEN-LAST:event_branchTboxFocusLost

    private void branchTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusGained
        this.branchTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_branchTboxFocusGained

    private void debitTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_debitTboxFocusGained
        this.debitTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_debitTboxFocusGained

    private void creditTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_creditTboxFocusGained
        this.creditTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_creditTboxFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amountLabel;
    private javax.swing.JFormattedTextField amountTbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JLabel branchNameLabel;
    private javax.swing.JPanel branchPanel;
    private javax.swing.JTextField branchTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel creditLabel;
    private javax.swing.JLabel creditNameLabel;
    private javax.swing.JPanel creditPanel;
    private javax.swing.JTextField creditTbox;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JLabel debitLabel;
    private javax.swing.JLabel debitNameLabel;
    private javax.swing.JPanel debitPanel;
    private javax.swing.JTextField debitTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel narrationLabel;
    private javax.swing.JTextField narrationTbox;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
