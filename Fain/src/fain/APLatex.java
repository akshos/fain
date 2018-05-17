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
import database.StockDB;
import database.TransactionDB;
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
import reports.PurchaseBill;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class APLatex extends javax.swing.JInternalFrame implements RefreshOption{

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
    public APLatex() {
        initComponents();
    }
    public APLatex(DBConnection db, int mode, String id, Main frame, int level){
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
    
    public APLatex(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        this.editId=id;
        initComponents();
        loadCurrDate();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        this.dateTbox.setDate(currDate);
    }
    
    private void loadContents(){
        String[] data = PurchaseLatexDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        this.loadBranch();
        int indexValB=Arrays.asList(branchData[0]).indexOf(data[1]);
        this.branchCbox.setSelectedIndex(indexValB);
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        try {
            this.dateTbox.setDate(df.parse(data[2]));
        } catch (ParseException ex) {
            Logger.getLogger(APLatex.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.prBillTbox.setText(data[3]);
        this.loadParty();
        int indexValP=Arrays.asList(partyData[0]).indexOf(data[4]);
        this.partyCbox.setSelectedIndex(indexValP);
        
        this.quantityTbox.setText(data[5]);
        this.drcTbox.setText(data[6]);
        this.dryRubberTbox.setText(data[7]);
        this.rateTbox.setText(data[8]);
        this.valueTbox.setText(data[9]);
    }
    
    private void resetParty(){
        String resetData[] = new String[1];
        resetData[0] = "Select Branch";
        this.partyCbox.setModel(new DefaultComboBoxModel(resetData));
        this.partyCbox.setEnabled(false);
    }
    
    private void loadParty(){
        String item = this.branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            resetParty();
            return;
        }
        this.partyCbox.setEnabled(true);
        int index = this.branchCbox.getSelectedIndex();
        String branchCode = this.branchData[0][index];
        partyData = CustomerDB.getCustomersInBranch(this.dbConnection.getStatement(), branchCode);
        int len;
        String[] cboxData = null;
        if(partyData  == null){
            len =  0;
            cboxData = new String[1];
            cboxData[0] = "Add New";
            this.partyCbox.setToolTipText("No customers available for branch");
        }else{
            len = partyData[0].length;
            cboxData = new String[len+1];
            for(int i = 0; i < len; i++){
                cboxData[i] = partyData[1][i] + "  (" + partyData[0][i] +")"  ;
            }
            cboxData[len] = "Add New";
            String address = this.partyData[2][0];
            this.partyCbox.setToolTipText(address);
        }
        this.partyCbox.setModel(new DefaultComboBoxModel(cboxData));
        
    }
    
    private void showPartyAddress(){
       String item = this.partyCbox.getSelectedItem().toString();
       if(item.compareTo("Add New") == 0){
           this.partyCbox.setToolTipText("No customers available for branch");
           return;
       }
       int index = this.partyCbox.getSelectedIndex();
       String address = this.partyData[2][index];
       this.partyCbox.setToolTipText(address);
       System.out.println("Address : " + address);
       try
        {
            java.awt.event.KeyEvent ke = new java.awt.event.KeyEvent(partyCbox, java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(), java.awt.event.InputEvent.CTRL_MASK, java.awt.event.KeyEvent.VK_F1, java.awt.event.KeyEvent.CHAR_UNDEFINED);
            this.partyCbox.dispatchEvent(ke);
        }
        catch (Throwable e1)
        {e1.printStackTrace();}
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
        cboxData[len] = "Add New";
        this.branchCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    @Override
    public void refreshContents(int type) {
        if(type == Codes.REFRESH_ALL){
            loadBranch();
            loadParty();
        }
        else if(type == Codes.REFRESH_BRANCHES){
            loadBranch();
        }
        else if(type == Codes.REFRESH_MASTER){
            loadParty();
        }
    }
    
    private boolean chechPrBill(){
        String billNo = this.prBillTbox.getText();
        if(billNo.compareTo("") == 0){
            this.prbillLabel.setText("<html>Pr. Bill <span style=\"color:red\">Empty</span></html>");
            return false;
        }
        if(PurchaseLatexDB.checkExistingBillNo(dbConnection.getStatement(), billNo)&&mode!=Codes.EDIT) {
            this.prbillLabel.setText("<html>Pr. Bill <span style=\"color:red\">Duplicate</span></html>");
            return false;
        }
        this.prbillLabel.setText("<html>Pr. Bill</html>");
        return true;
    }
    
    private boolean validateFields(){
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
        return true;
    }
    
    private void insertData(){
        Statement stmt = dbConnection.getStatement();
        String branch = "";
        String item = branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            JOptionPane.showMessageDialog(this, "Please select a valid branch", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int index = this.branchCbox.getSelectedIndex();
        branch = this.branchData[0][index];
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        
        String date         =df.format(dateTbox.getDate());
        if(date == null){
            JOptionPane.showMessageDialog(this, "Please enter Date From", "NO DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String prBill = prBillTbox.getText();
        String party      ="";
        item = partyCbox.getSelectedItem().toString();
        if(item.compareTo("None") == 0){
            JOptionPane.showMessageDialog(this, "Please select a valid branch", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        index = partyCbox.getSelectedIndex();
        party = partyData[0][index];
        if( !validateFields() ){
            return;
        }
        double quantity=Double.parseDouble(quantityTbox.getText().replace(",",""));
        double drc      =Double.parseDouble(drcTbox.getText().replace(",",""));
        double dryrubber=Double.parseDouble(dryRubberTbox.getText().replace(",",""));
        double rate     =Double.parseDouble(rateTbox.getText().replace(",",""));
        double value    =Double.parseDouble(valueTbox.getText().replace(",",""));
        String tid = TransactionDB.generateTid();
        
        String purchaseAccount = StockDB.getLatexPurchaseAccount(dbConnection.getStatement());
        if(purchaseAccount.compareTo("none") == 0){
            JOptionPane.showMessageDialog(this, "Item 'Latex' was not found in stock", "No stock Latex", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = PurchaseLatexDB.update(stmt, this.editId, branch, date, prBill, party, quantity, drc, dryrubber, rate, value, tid);
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String narration = "PURCHASE OF LATEX (BILL #" + prBill +")";
            
            ret = TransactionDB.update(stmt, editId, date, branch, purchaseAccount, party, value, narration, tid);
            if(!ret){
                JOptionPane.showMessageDialog(this, "Failed to update Transaction", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else{
            ret = PurchaseLatexDB.insert(stmt, branch, date, prBill, party, quantity, drc, dryrubber, rate, value, tid);
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String narration = "PURCHASE OF LATEX (BILL #" + prBill +")";
        
            ret = TransactionDB.insert(stmt, date, branch, purchaseAccount, party, value, narration, tid);
            if(!ret){
                JOptionPane.showMessageDialog(this, "Failed to add Transaction", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            checkBillPrint(tid); //Print Bill
        }
        
        if(prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_PLATEX);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void nextEntry(){
        this.branchCbox.requestFocus();
        this.prBillTbox.setText("");
        this.quantityTbox.setText("");
        this.drcTbox.setText("");
        this.dryRubberTbox.setText("");
        this.rateTbox.setText("");
        this.valueTbox.setText("");
    }
    
    private void checkBillPrint(String tid){
        int ret = JOptionPane.showConfirmDialog(this, "Do you want to Print the Bill?", "PRINT?", JOptionPane.INFORMATION_MESSAGE);
        if(ret == JOptionPane.YES_OPTION){
            String pid = PurchaseLatexDB.getPidFromTid(dbConnection.getStatement(), tid);
            PurchaseBill.createBill(dbConnection, pid);
        }
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
        this.dryRubberTbox.setText(String.valueOf(dryRubber));
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
        this.valueTbox.setText(String.valueOf(value));
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
    
    private void checkBranchChangedItem(){
        String item = this.branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewBranch();
        }else{
            this.branchCbox.transferFocus();
        }
    }
    
    private void addNewMasterAccount(){
        AMaster item = new AMaster(dbConnection, Codes.NEW_ENTRY, null, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            System.out.println("setting size");
            item.setSize(dim);
        }else{
            item.setSize(790, 300);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
    
    private void checkPartyChangedItem(){
        String item = this.partyCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewMasterAccount();
        }else{
            this.partyCbox.transferFocus();
        }
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
        prbillLabel = new javax.swing.JLabel();
        partyLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        drcLabel = new javax.swing.JLabel();
        dryrubberLabel = new javax.swing.JLabel();
        rateLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchCbox = new javax.swing.JComboBox<>();
        dateTbox = new org.jdesktop.swingx.JXDatePicker();
        prBillTbox = new javax.swing.JFormattedTextField();
        partyCbox = new javax.swing.JComboBox<>();
        quantityTbox = new javax.swing.JFormattedTextField();
        drcTbox = new javax.swing.JFormattedTextField();
        dryRubberTbox = new javax.swing.JFormattedTextField();
        rateTbox = new javax.swing.JFormattedTextField();
        valueTbox = new javax.swing.JFormattedTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Purchase Latex");
        setPreferredSize(new java.awt.Dimension(450, 410));

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

        labelsPanel.setLayout(new java.awt.GridLayout(10, 0, 0, 10));

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        prbillLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        prbillLabel.setText("Pr. Bill");
        labelsPanel.add(prbillLabel);

        partyLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        partyLabel.setText("Party");
        labelsPanel.add(partyLabel);

        quantityLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        quantityLabel.setText("Quantity Kgs.");
        labelsPanel.add(quantityLabel);

        drcLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        drcLabel.setText("D R C");
        labelsPanel.add(drcLabel);

        dryrubberLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        dryrubberLabel.setText("Dry Rubber Kgs.");
        labelsPanel.add(dryrubberLabel);

        rateLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        rateLabel.setText("Rate");
        labelsPanel.add(rateLabel);

        valueLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valueLabel.setText("Value");
        labelsPanel.add(valueLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(10, 0, 0, 10));

        branchCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        branchCbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                branchCboxItemStateChanged(evt);
            }
        });
        branchCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
                branchCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(branchCbox);

        dateTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(dateTbox);

        prBillTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        prBillTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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

        partyCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        partyCbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                partyCboxItemStateChanged(evt);
            }
        });
        partyCbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                partyCboxFocusGained(evt);
            }
        });
        partyCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                partyCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(partyCbox);

        quantityTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        quantityTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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

        drcTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        drcTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        drcTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                drcTboxFocusGained(evt);
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

        dryRubberTbox.setEditable(false);
        dryRubberTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        dryRubberTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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

        rateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        rateTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rateTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rateTboxFocusGained(evt);
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

        valueTbox.setEditable(false);
        valueTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.000"))));
        valueTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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
                valueTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(valueTbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        enterButton.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedHandler
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_keyPressedHandler

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        // TODO add your handling code here:
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

    private void branchCboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_branchCboxItemStateChanged
        this.loadParty();
    }//GEN-LAST:event_branchCboxItemStateChanged

    private void partyCboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_partyCboxItemStateChanged
        showPartyAddress();
    }//GEN-LAST:event_partyCboxItemStateChanged

    private void partyCboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_partyCboxFocusGained
        showPartyAddress();
    }//GEN-LAST:event_partyCboxFocusGained

    private void dryRubberTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dryRubberTboxFocusGained
        calculateDryRubber();
    }//GEN-LAST:event_dryRubberTboxFocusGained

    private void valueTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueTboxFocusGained
        calculateValue();
    }//GEN-LAST:event_valueTboxFocusGained

    private void prBillTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_prBillTboxFocusLost
        chechPrBill();
    }//GEN-LAST:event_prBillTboxFocusLost

    private void branchCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkBranchChangedItem();
        }  
    }//GEN-LAST:event_branchCboxKeyPressed

    private void partyCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_partyCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            checkPartyChangedItem();    
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_partyCboxKeyPressed

    private void prBillTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_prBillTboxFocusGained
        this.prBillTbox.selectAll();
    }//GEN-LAST:event_prBillTboxFocusGained

    private void quantityTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quantityTboxFocusGained
        this.quantityTbox.selectAll();
    }//GEN-LAST:event_quantityTboxFocusGained

    private void drcTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drcTboxFocusGained
        this.drcTbox.selectAll();
    }//GEN-LAST:event_drcTboxFocusGained

    private void rateTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTboxFocusGained
        this.rateTbox.selectAll();
    }//GEN-LAST:event_rateTboxFocusGained

    private void valueTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valueTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.insertData();
        }
    }//GEN-LAST:event_valueTboxKeyPressed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.insertData();
        }
    }//GEN-LAST:event_enterButtonKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private org.jdesktop.swingx.JXDatePicker dateTbox;
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
    private javax.swing.JComboBox<String> partyCbox;
    private javax.swing.JLabel partyLabel;
    private javax.swing.JFormattedTextField prBillTbox;
    private javax.swing.JLabel prbillLabel;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JFormattedTextField quantityTbox;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JFormattedTextField rateTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JFormattedTextField valueTbox;
    // End of variables declaration//GEN-END:variables

    
}
