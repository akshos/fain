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
import database.PurchaseDB;
import database.PurchaseLatexDB;
import database.SalesDB;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.UtilityFuncs;
import utility.ValidationChecks;
/**
 *
 * @author akshos
 */
public class ASLatex extends javax.swing.JInternalFrame implements RefreshOption{

    DBConnection dbConnection;
    Main mainFrame;
    int level;
    RefreshOption prevFrame;
    String[][] branchData;
    String[][] partyData;
    int mode;
    String editId;
    String prevBillNo;
    /**
     * Creates new form MasterEntry
     */
    public ASLatex() {
        initComponents();
    }
    
    public ASLatex(DBConnection db, int mode, String id, Main frame, int level){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        initComponents();
        this.mode=mode;
        this.editId=id;
        loadCurrDate();
        refreshContents(Codes.REFRESH_ALL);
        prevFrame = null;
    }
    
    public ASLatex(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame) throws Exception{
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        this.editId=id;
        initComponents();
        loadCurrDate();
        if(mode == Codes.EDIT)  this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.dateTbox.setText(date);
    }
    
    private void loadContents() throws Exception{
        String[] data = SalesDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        
        this.loadBranchData();
        
        this.branchTbox.setText(data[1]);
        this.validateBranch();
        
        this.dateTbox.setText(UtilityFuncs.dateSqlToUser(data[2]));
        
        this.prBillTbox.setText(data[3]);
        this.prevBillNo = data[3];
        
        this.loadPartyData();
        
        this.partyTbox.setText(data[4]);
        this.validateParty();
        
        this.barrelFromTbox.setText(data[5]);
        this.barrelToTbox.setText(data[6]);
        
        this.quantityTbox.setText(data[8]);
        this.drcTbox.setText(data[9]);
        this.dryRubberTbox.setText(data[10]);
        this.rateTbox.setText(data[11]);
        this.valueTbox.setText(data[12]);
    }
        
        
    private void loadPartyData() throws Exception{
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
        
    private void loadBranchData() throws Exception{
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
    public void refreshContents(int code) {
        if(code == Codes.REFRESH_ALL){
            try {
                loadBranchData();
            } catch (Exception ex) {
                Logger.getLogger(ASLatex.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                loadPartyData();
            } catch (Exception ex) {
                Logger.getLogger(ASLatex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(code == Codes.REFRESH_BRANCHES){
            try {
                loadBranchData();
            } catch (Exception ex) {
                Logger.getLogger(ASLatex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(code == Codes.REFRESH_MASTER){
            try {
                loadPartyData();
            } catch (Exception ex) {
                Logger.getLogger(ASLatex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean validateFields(){
        if(!this.chechPrBill()){
            return false;
        }
        if(this.quantityTbox.getText().trim().compareTo("") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please specify Quantity", "No Quantity", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(this.drcTbox.getText().trim().compareTo("") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please specify DRC", "No DRC", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(this.rateTbox.getText().trim().compareTo("") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please specify Rate", "No Rate", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(this.barrelToTbox.getText().trim().compareTo("") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please specify Barrel #To", "No Barrel #", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(this.barrelFromTbox.getText().trim().compareTo("") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please specify Barrel #From", "No Barrel #", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        
        String branch = branchTbox.getText();
        if(!validateBranch()){
            JOptionPane.showMessageDialog(this, "Please enter a valid BRANCH", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String date = this.dateTbox.getText();
        if(!ValidationChecks.isDateValid(date)){
            JOptionPane.showMessageDialog(this, "Please enter a valid DATE", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        date = UtilityFuncs.dateUserToSql(date);
        
        String bill = prBillTbox.getText();
        
        String party = partyTbox.getText();
        if(!validateParty()){
            JOptionPane.showMessageDialog(this, "Please enter a valid PARTY", "PARTY NOT FOUND", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if( !validateFields() ){
            return;
        }
        
        int bnto        = Integer.parseInt(barrelToTbox.getText().replace(",",""));
        int bnfrom      = Integer.parseInt(barrelFromTbox.getText().replace(",",""));
        double quantity = Double.parseDouble(quantityTbox.getText().replace(",",""));
        double drc      = Double.parseDouble(drcTbox.getText().replace(",",""));
        double dryrubber= Double.parseDouble(dryRubberTbox.getText().replace(",",""));
        double rate     = Double.parseDouble(rateTbox.getText().replace(",",""));
        double value    = Double.parseDouble(valueTbox.getText().replace(",",""));
        
        String tid = TransactionDB.generateTid();
        
        String salesAccount = StockDB.getLatexSalesAccount(dbConnection.getStatement());
        if(salesAccount.compareTo("none") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Item 'Latex' was not found in stock", "No stock Latex", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = SalesDB.update(stmt, editId, branch, date, bill, party, bnfrom, bnto, quantity, drc, dryrubber, rate, value);
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            tid = SalesDB.getTidFromSid(stmt, editId);
            String narration = "SALE OF LATEX (BILL #" + bill +")";
            
            ret = TransactionDB.updateByTid(stmt,date, branch, party, salesAccount, value, narration, tid);
            if(!ret){
                JOptionPane.showMessageDialog(this, "Failed to update Transaction", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else{
            ret = SalesDB.insert(stmt, branch, date, bill, party, bnfrom, bnto, quantity, drc, dryrubber, rate, value, tid);
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            String narration = "SALE OF LATEX (BILL #" + bill +")";
        
            ret = TransactionDB.insert(stmt, date, branch, party, salesAccount, value, narration, tid);
            if(!ret){
                JOptionPane.showMessageDialog(this, "Failed to add Transaction", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if(prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_SLATEX);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void nextEntry(){
        this.branchTbox.requestFocus();
        this.prBillTbox.setText("");
        this.barrelFromTbox.setText("");
        this.barrelToTbox.setText("");
        this.quantityTbox.setText("");
        this.drcTbox.setText("");
        this.dryRubberTbox.setText("");
        this.rateTbox.setText("");
        this.valueTbox.setText("");
    }
    
    private boolean chechPrBill(){
        /* remove only the comments to re enable pr bill duplication checks
        String billNo = this.prBillTbox.getText();
        if(this.mode == Codes.EDIT && billNo.compareTo(this.prevBillNo) == 0){
            return true;
        }
        if(billNo.compareTo("") == 0){
            this.billnumberLabel.setText("<html>Bill No. <span style=\"color:red\">Empty</span></html>");
            return false;
        }
        if(SalesDB.checkExistingBillNo(dbConnection.getStatement(), billNo)){
            this.billnumberLabel.setText("<html>Bill No.<span style=\"color:red\">Duplicate</span></html>");
            return false;
        }
        this.billnumberLabel.setText("<html>Bill No.</html>");
        */
        return true;
    }
    
    private void calculateDryRubber(){
        double drc, qnt;
        String item = this.quantityTbox.getText().replace(",","");
        if(item.compareTo("") == 0){
            return;
        }
        drc = Double.parseDouble(item);
        item = this.drcTbox.getText().replace(",","");
        if(item.compareTo("") == 0){
            return;
        }
        qnt = Double.parseDouble(item);
        Double dryRubber = (qnt * drc)/100;
        this.dryRubberTbox.setText(new DecimalFormat("##,##,##,####.000").format(dryRubber));
    }
    
    private void calculateValue(){
        String item = this.dryRubberTbox.getText().replace(",","");
        if(item.compareTo("") == 0){
            return;
        }
        double dryRubber = Double.parseDouble(item);
        item = this.rateTbox.getText().replace(",","");
        if(item.compareTo("") == 0){
            return;
        }
        double rate = Double.parseDouble(item);
        double value = rate * dryRubber;
        this.valueTbox.setText(new DecimalFormat("##,##,##,###0.00").format(value));
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
        dateLabel = new javax.swing.JLabel();
        billnumberLabel = new javax.swing.JLabel();
        partyLabel = new javax.swing.JLabel();
        barrelfromLabel = new javax.swing.JLabel();
        barreltoLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        drcLabel = new javax.swing.JLabel();
        dryrubberLabel = new javax.swing.JLabel();
        rateLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchPanel = new javax.swing.JPanel();
        branchTbox = new javax.swing.JTextField();
        branchNameLabel = new javax.swing.JLabel();
        dateTbox = new javax.swing.JFormattedTextField();
        prBillTbox = new javax.swing.JTextField();
        partyPanel = new javax.swing.JPanel();
        partyTbox = new javax.swing.JTextField();
        partyNameLabel = new javax.swing.JLabel();
        barrelFromTbox = new javax.swing.JTextField();
        barrelToTbox = new javax.swing.JTextField();
        quantityTbox = new javax.swing.JFormattedTextField();
        drcTbox = new javax.swing.JFormattedTextField();
        dryRubberTbox = new javax.swing.JFormattedTextField();
        rateTbox = new javax.swing.JFormattedTextField();
        valueTbox = new javax.swing.JFormattedTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Sales Latex");
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

        labelsPanel.setLayout(new java.awt.GridLayout(12, 0, 0, 10));

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        billnumberLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        billnumberLabel.setText("Bill No.");
        labelsPanel.add(billnumberLabel);

        partyLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        partyLabel.setText("Party");
        labelsPanel.add(partyLabel);

        barrelfromLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        barrelfromLabel.setText("Barrel # From");
        labelsPanel.add(barrelfromLabel);

        barreltoLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        barreltoLabel.setText("Barrel # To");
        labelsPanel.add(barreltoLabel);

        quantityLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        quantityLabel.setText("Quantity Kgs.");
        labelsPanel.add(quantityLabel);

        drcLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        drcLabel.setText("D R C");
        labelsPanel.add(drcLabel);

        dryrubberLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dryrubberLabel.setText("Dry Rubber Kgs.");
        labelsPanel.add(dryrubberLabel);

        rateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        rateLabel.setText("Rate");
        labelsPanel.add(rateLabel);

        valueLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        valueLabel.setText("Value");
        labelsPanel.add(valueLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(12, 0, 0, 10));

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

        prBillTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        prBillTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                prBillTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                prBillTboxFocusLost(evt);
            }
        });
        prBillTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(prBillTbox);

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

        barrelFromTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        barrelFromTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barrelFromTboxFocusGained(evt);
            }
        });
        barrelFromTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelFromTbox);

        barrelToTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        barrelToTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barrelToTboxFocusGained(evt);
            }
        });
        barrelToTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelToTbox);

        quantityTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,###.000"))));
        quantityTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        quantityTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                quantityTboxFocusGained(evt);
            }
        });
        quantityTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityTboxActionPerformed(evt);
            }
        });
        quantityTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(quantityTbox);

        drcTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,###.000"))));
        drcTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        drcTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                drcTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                drcTboxFocusLost(evt);
            }
        });
        drcTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drcTboxActionPerformed(evt);
            }
        });
        drcTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(drcTbox);

        dryRubberTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,###.000"))));
        dryRubberTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        dryRubberTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dryRubberTboxFocusGained(evt);
            }
        });
        dryRubberTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dryRubberTboxActionPerformed(evt);
            }
        });
        dryRubberTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(dryRubberTbox);

        rateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.00"))));
        rateTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        rateTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rateTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                rateTboxFocusLost(evt);
            }
        });
        rateTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rateTboxActionPerformed(evt);
            }
        });
        rateTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(rateTbox);

        valueTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.00"))));
        valueTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        valueTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                valueTboxFocusGained(evt);
            }
        });
        valueTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueTboxActionPerformed(evt);
            }
        });
        valueTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                enterButtonKeyPressed(evt);
            }
        });
        rightInerPannel.add(valueTbox);

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
        titleLabel.setText("SALES LATEX");
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
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void quantityTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityTboxActionPerformed

    private void drcTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drcTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drcTboxActionPerformed

    private void dryRubberTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dryRubberTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dryRubberTboxActionPerformed

    private void rateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rateTboxActionPerformed

    private void valueTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_valueTboxActionPerformed

    private void prBillTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_prBillTboxFocusLost
        this.chechPrBill();
    }//GEN-LAST:event_prBillTboxFocusLost

    private void drcTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drcTboxFocusLost
        this.calculateDryRubber();
    }//GEN-LAST:event_drcTboxFocusLost

    private void rateTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTboxFocusLost
        this.calculateValue();
    }//GEN-LAST:event_rateTboxFocusLost

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

    private void prBillTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_prBillTboxFocusGained
        this.prBillTbox.selectAll();
    }//GEN-LAST:event_prBillTboxFocusGained

    private void barrelFromTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelFromTboxFocusGained
        this.barrelFromTbox.selectAll();
    }//GEN-LAST:event_barrelFromTboxFocusGained

    private void barrelToTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelToTboxFocusGained
        this.barrelToTbox.selectAll();
    }//GEN-LAST:event_barrelToTboxFocusGained

    private void quantityTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quantityTboxFocusGained
        this.quantityTbox.selectAll();
    }//GEN-LAST:event_quantityTboxFocusGained

    private void drcTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drcTboxFocusGained
        this.drcTbox.selectAll();
    }//GEN-LAST:event_drcTboxFocusGained

    private void dryRubberTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dryRubberTboxFocusGained
        this.dryRubberTbox.selectAll();
    }//GEN-LAST:event_dryRubberTboxFocusGained

    private void rateTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTboxFocusGained
        this.rateTbox.selectAll();
    }//GEN-LAST:event_rateTboxFocusGained

    private void valueTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueTboxFocusGained
        this.valueTbox.selectAll();
    }//GEN-LAST:event_valueTboxFocusGained

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosing

    private void dateTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dateTboxFocusGained
        this.dateTbox.setCaretPosition(0);        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxFocusGained

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

    private void partyTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_partyTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            chooseParty();
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
    }//GEN-LAST:event_partyTboxKeyPressed

    private void partyTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_partyTboxKeyReleased
        this.validateParty();        // TODO add your handling code here:
    }//GEN-LAST:event_partyTboxKeyReleased

    private void branchTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusLost
        try {
            this.loadPartyData();        // TODO add your handling code here:
        } catch (Exception ex) {
            Logger.getLogger(ASLatex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_branchTboxFocusLost

    private void branchTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_branchTboxFocusGained
        this.branchTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_branchTboxFocusGained

    private void partyTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_partyTboxFocusGained
        this.partyTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_partyTboxFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField barrelFromTbox;
    private javax.swing.JTextField barrelToTbox;
    private javax.swing.JLabel barrelfromLabel;
    private javax.swing.JLabel barreltoLabel;
    private javax.swing.JLabel billnumberLabel;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JLabel branchNameLabel;
    private javax.swing.JPanel branchPanel;
    private javax.swing.JTextField branchTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JLabel drcLabel;
    private javax.swing.JFormattedTextField drcTbox;
    private javax.swing.JFormattedTextField dryRubberTbox;
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
    private javax.swing.JTextField prBillTbox;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JFormattedTextField quantityTbox;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JFormattedTextField rateTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JFormattedTextField valueTbox;
    // End of variables declaration//GEN-END:variables

}
