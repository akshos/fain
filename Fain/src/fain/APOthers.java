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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class APOthers extends javax.swing.JInternalFrame implements RefreshOption {
    DBConnection dbConnection;
    Main mainFrame;
    int level;
    RefreshOption prevFrame;
    String branchData[][];
    String partyData[][];
    int mode;
    String editId;
    String itemData[][];
    /**
     * Creates new form MasterEntry
     */
    public APOthers() {
        initComponents();
    }

    public APOthers(DBConnection db, int mode, String id, Main frame, int level){
        this.level =  level;
        this.mainFrame  = frame;
        this.dbConnection = db;
        initComponents();
        this.mode=mode;
        this.editId=id;
        refreshContents(Codes.REFRESH_ALL);
    }
    
    public APOthers(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame) throws Exception{
        this.prevFrame = prevFrame;
        this.level =  level;
        this.mainFrame  = frame;
        this.dbConnection = db;
        this.mode=mode;
        this.editId=id;
        initComponents();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
    }
    private void loadContents() throws Exception{
        String[] data = PurchaseDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        this.loadBranch();
        int indexValB=Arrays.asList(branchData[0]).indexOf(data[1]);
        this.branchCbox.setSelectedIndex(indexValB);
        this.dateTbox.setText(data[2]);
        this.billNumberTbox.setText(data[3]);
        this.loadParty();
        int indexValP=Arrays.asList(partyData[0]).indexOf(data[4]);
        this.partyCbox.setSelectedIndex(indexValP);
        this.loadItems();
        int indexValI=Arrays.asList(itemData[0]).indexOf(data[5]);
        this.itemCodeCbox.setSelectedIndex(indexValI);
        this.itemnameTbox.setText(data[6]);
        this.quantityTbox.setText(data[7]);
        
        this.valueTbox.setText(data[8]);
    }
    private boolean validateFields(){
        if(this.quantityTbox.getText().trim().compareTo("") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please specify Quantity", "No Quantity", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(this.valueTbox.getText().trim().compareTo("") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please specify Value", "No Value", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        String branch = "";
        String item = branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a valid branch", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int index = this.branchCbox.getSelectedIndex();
        branch = this.branchData[0][index];
        String date = dateTbox.getText();
        String bill = billNumberTbox.getText();
        String party      ="";
        item = partyCbox.getSelectedItem().toString();
        if(item.compareTo("None") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a valid branch", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        index = partyCbox.getSelectedIndex();
        party = partyData[0][index];
        item = this.itemCodeCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a valid item", "No item selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        index = this.itemCodeCbox.getSelectedIndex();
        String itemCode = this.itemData[0][index];
        if( !validateFields() ){
            return;
        }
        String itemname= itemnameTbox.getText();
        double quantity=Double.parseDouble(quantityTbox.getText());
        double value    =Double.parseDouble(valueTbox.getText());
        String tid = TransactionDB.generateTid();
        
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = PurchaseDB.update(stmt, editId, branch, date, bill, party, itemCode, itemname, quantity, value, tid);
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            } 
        }
        else{
            ret = PurchaseDB.insert(stmt, branch, date, bill, party, itemCode, itemname, quantity, value, tid);
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_POTHERS);
            this.doDefaultCloseAction();
        }
    }
    
    private void nextEntry(){
        this.branchCbox.requestFocus();
        this.billNumberTbox.setText("");
    }
    
    private void setItemName(){
        int index = this.itemCodeCbox.getSelectedIndex();
        if (index==-1)
            return;
        String curr = this.itemCodeCbox.getSelectedItem().toString();
        String itemName = "";
        if(curr.compareTo("Add New") != 0){
            itemName = this.itemData[1][index];
        }
        this.itemnameTbox.setText(itemName);
    }
    
    private void loadItems() throws Exception{
        System.out.println("loading itemcbox");
        itemData = StockDB.getItems(this.dbConnection.getStatement());
        int len;
        if(itemData  == null){
            len =  0;
        }else{
            len = itemData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = itemData[1][i] + " (" + itemData[0][i] + ")";
        }
        cboxData[len] = "Add New";
        this.itemCodeCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    private void loadBranch() throws Exception{
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
    
    private void resetParty(){
        String resetData[] = new String[1];
        resetData[0] = "Select Branch";
        this.partyCbox.setModel(new DefaultComboBoxModel(resetData));
        this.partyCbox.setEnabled(false);
    }
    
    private void loadParty() throws Exception{
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
    
     @Override
    public void refreshContents(int code) {
        if(code == Codes.REFRESH_ALL){
            try {
                loadBranch();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                loadParty();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                loadItems();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(code == Codes.REFRESH_BRANCHES){
            try {
                loadBranch();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(code == Codes.REFRESH_MASTER){
            try {
                loadParty();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(code == Codes.REFRESH_STOCK){
            try {
                loadItems();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean chechPrBill(){
        String billNo = this.billNumberTbox.getText();
        if(billNo.compareTo("") == 0){
            this.billNumberTbox.setText("<html>Bill No. <span style=\"color:red\">Empty</span></html>");
            return false;
        }
        if(PurchaseDB.checkExistingBillNo(dbConnection.getStatement(), billNo)){
            this.billNumberTbox.setText("<html>Bill No. <span style=\"color:red\">Duplicate</span></html>");
            return false;
        }
        this.billNumberTbox.setText("<html>Bill No.</html>");
        return true;
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
        }
    }
    
    private void addNewMasterAccount() throws Exception{
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
    
    private void checkPartyChangedItem() throws Exception{
        String item = this.partyCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewMasterAccount();
        }else{
            partyCbox.transferFocus();
        }
    }
    
    private void addNewStock() throws Exception{
        AStock item = new AStock(dbConnection, Codes.NEW_ENTRY, null, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 360);
        }
        mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }
    
    private void checkItemChangedItem() throws Exception{
        String item = this.itemCodeCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewStock();
        }else{
            itemCodeCbox.transferFocus();
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
        billnumberLabel = new javax.swing.JLabel();
        partyLabel = new javax.swing.JLabel();
        itemcodeLabel = new javax.swing.JLabel();
        itemnameLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchCbox = new javax.swing.JComboBox<>();
        dateTbox = new javax.swing.JFormattedTextField();
        billNumberTbox = new javax.swing.JTextField();
        partyCbox = new javax.swing.JComboBox<>();
        itemCodeCbox = new javax.swing.JComboBox<>();
        itemnameTbox = new javax.swing.JTextField();
        quantityTbox = new javax.swing.JFormattedTextField();
        valueTbox = new javax.swing.JFormattedTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Purchase Others");
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

        labelsPanel.setLayout(new java.awt.GridLayout(9, 0, 0, 10));

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        billnumberLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        billnumberLabel.setText("Bill No.");
        labelsPanel.add(billnumberLabel);

        partyLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        partyLabel.setText("Party");
        labelsPanel.add(partyLabel);

        itemcodeLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        itemcodeLabel.setText("Item Code");
        labelsPanel.add(itemcodeLabel);

        itemnameLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        itemnameLabel.setText("Item Name");
        labelsPanel.add(itemnameLabel);

        quantityLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        quantityLabel.setText("Quantity");
        labelsPanel.add(quantityLabel);

        valueLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        valueLabel.setText("Value");
        labelsPanel.add(valueLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(9, 0, 0, 10));

        branchCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        branchCbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                branchCboxItemStateChanged(evt);
            }
        });
        branchCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(branchCbox);

        try {
            dateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        dateTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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

        billNumberTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        billNumberTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                billNumberTboxFocusGained(evt);
            }
        });
        billNumberTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billNumberTboxActionPerformed(evt);
            }
        });
        billNumberTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(billNumberTbox);

        partyCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        partyCbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                partyCboxItemStateChanged(evt);
            }
        });
        partyCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                partyCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(partyCbox);

        itemCodeCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        itemCodeCbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                itemCodeCboxItemStateChanged(evt);
            }
        });
        itemCodeCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                itemCodeCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(itemCodeCbox);

        itemnameTbox.setEditable(false);
        itemnameTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        itemnameTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(itemnameTbox);

        quantityTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##0.000"))));
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

        valueTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##0.00"))));
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
                keyPressedHandler(evt);
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
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void dateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxActionPerformed

    private void billNumberTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billNumberTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_billNumberTboxActionPerformed

    private void quantityTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityTboxActionPerformed

    private void valueTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_valueTboxActionPerformed

    private void partyCboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_partyCboxItemStateChanged
        this.showPartyAddress();
    }//GEN-LAST:event_partyCboxItemStateChanged

    private void branchCboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_branchCboxItemStateChanged
        try {
            this.loadParty();
        } catch (Exception ex) {
            Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_branchCboxItemStateChanged

    private void partyCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_partyCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            try {
                this.checkPartyChangedItem();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_partyCboxKeyPressed

    private void itemCodeCboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_itemCodeCboxItemStateChanged
        this.setItemName();
    }//GEN-LAST:event_itemCodeCboxItemStateChanged

    private void itemCodeCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_itemCodeCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            try {
                this.checkItemChangedItem();
            } catch (Exception ex) {
                Logger.getLogger(APOthers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_itemCodeCboxKeyPressed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            insertData();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void billNumberTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_billNumberTboxFocusGained
        this.billNumberTbox.selectAll();
    }//GEN-LAST:event_billNumberTboxFocusGained

    private void quantityTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quantityTboxFocusGained
        this.quantityTbox.selectAll();
    }//GEN-LAST:event_quantityTboxFocusGained

    private void valueTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueTboxFocusGained
        this.valueTbox.selectAll();
    }//GEN-LAST:event_valueTboxFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField billNumberTbox;
    private javax.swing.JLabel billnumberLabel;
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JComboBox<String> itemCodeCbox;
    private javax.swing.JLabel itemcodeLabel;
    private javax.swing.JLabel itemnameLabel;
    private javax.swing.JTextField itemnameTbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JComboBox<String> partyCbox;
    private javax.swing.JLabel partyLabel;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JFormattedTextField quantityTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JFormattedTextField valueTbox;
    // End of variables declaration//GEN-END:variables

   
}
