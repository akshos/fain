/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;
import database.BranchDB;
import database.DBConnection;
import database.MasterDB;
import database.StockDB;
import java.awt.Dimension;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
/**
/**
 *
 * @author akshos
 */
public class AStock extends javax.swing.JInternalFrame implements RefreshOption{
    DBConnection dbConnection;
    Main mainFrame;
    int level;
    RefreshOption prevFrame;
    String purchaseData[][];
    String salesData[][];
    String stockData[][];
    String editId;
    int mode;
    /**
     * Creates new form MasterEntry
     */
    public AStock() {
        initComponents();
    }
    
    public AStock(DBConnection db, int mode, String id, Main frame, int level){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.editId=id;
        this.mode=mode;
        initComponents();
        
        refreshContents(Codes.REFRESH_ALL);
        this.prevFrame = null;
    }
    
    public AStock(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.editId=id;
        this.mode=mode;
        initComponents();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        //String icode        =itemCodeTbox.getText();
        String iname        = itemNameTbox.getText();
        double currentStock = Double.parseDouble(currentStockTbox.getText().replace(",",""));
        double rate         = Double.parseDouble(rateTbox.getText().replace(",",""));
        String purchase     ="";
        String item = this.purchasesCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            JOptionPane.showMessageDialog(this, "Please select a purchase account", "No Purchase Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int index = this.purchasesCbox.getSelectedIndex();
        purchase = this.purchaseData[0][index];
        String sales     ="";
        item = this.salesCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            JOptionPane.showMessageDialog(this, "Please select a sales account", "No Purchase Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        index = this.salesCbox.getSelectedIndex();
        sales = this.salesData[0][index];
        String stock     ="";
        item = this.stockCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            JOptionPane.showMessageDialog(this, "Please select a stock account", "No Purchase Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        index = this.stockCbox.getSelectedIndex();
        stock = this.stockData[0][index];
        
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = StockDB.update(stmt, editId, iname, currentStock, rate, purchase, sales, stock);
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else{
            ret = StockDB.insert(stmt, iname, currentStock, rate, purchase, sales, stock);
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_STOCK);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void nextEntry(){
        this.itemNameTbox.setText("");
        this.itemNameTbox.requestFocus();
    }
    
    private void loadPurchaseAccounts(){
        System.out.println("loading purchaseCbox");
        purchaseData = MasterDB.getPurchaseAC(this.dbConnection.getStatement());
        int len;
        if(purchaseData == null){
            len = 0;
        }else{
            len = purchaseData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = purchaseData[1][i] + "(" + purchaseData[0][i] + ")";
        }
        cboxData[len] = "Add New";
        this.purchasesCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    private void loadSalesAccounts(){
        System.out.println("loading salesCbox");
        salesData = MasterDB.getSalesAC(this.dbConnection.getStatement());
        int len;
        if(salesData == null){
            len = 0;
        }else{
            len = salesData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = salesData[1][i] + "(" + salesData[0][i] + ")" ;
        }
        cboxData[len] = "Add New";
        this.salesCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    private void loadStockAccounts(){
        System.out.println("loading salesCbox");
        stockData = MasterDB.getStockAC(this.dbConnection.getStatement());
        int len;
        if(stockData == null){
            len = 0;
        }else{
            len = stockData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = stockData[1][i] + "(" + stockData[0][i] + ")";
        }
        cboxData[len] = "Add New";
        this.stockCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
        private void loadContents(){
        String[] data = StockDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        loadStockAccounts();
        loadPurchaseAccounts();
        loadSalesAccounts();
        
        this.itemNameTbox.setText(data[1]);
        this.currentStockTbox.setText(data[2]);
        this.rateTbox.setText(data[3]);
        int indexValP=Arrays.asList(purchaseData[0]).indexOf(data[4]);
        //System.out.println("purchase index"+indexValP+" "+purchaseData[0][0]);
        this.purchasesCbox.setSelectedIndex(indexValP);
        int indexValS=Arrays.asList(salesData[0]).indexOf(data[5]);
        this.salesCbox.setSelectedIndex(indexValS);
        int indexValC=Arrays.asList(stockData[0]).indexOf(data[6]);
        this.stockCbox.setSelectedIndex(indexValC);
    }
    
    public void refreshContents(int code) {
        if(code == Codes.REFRESH_ALL){
            this.loadPurchaseAccounts();
            this.loadSalesAccounts();
            this.loadStockAccounts();
        }
        else if(code == Codes.REFRESH_MASTER){
            if(this.purchasesCbox.getSelectedItem().toString().compareTo("Add New") == 0)
                this.loadPurchaseAccounts();
            if(this.salesCbox.getSelectedItem().toString().compareTo("Add New") == 0)
                this.loadSalesAccounts();
            if(this.stockCbox.getSelectedItem().toString().compareTo("Add New") == 0)
                this.loadStockAccounts();
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
    
    private void checkPurchasesChangedItem(){
        String item = this.purchasesCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewMasterAccount();
        }else{
            this.purchasesCbox.transferFocus();
        }
    }
    
    private void checkSalesChangedItem(){
        String item = this.salesCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewMasterAccount();
        }else{
            this.salesCbox.transferFocus();
        }
    }
    private void checkStockChangedItem(){
        String item = this.stockCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewMasterAccount();
        }else{
            this.stockCbox.transferFocus();
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
        itemNameLabel = new javax.swing.JLabel();
        currentStockLabel = new javax.swing.JLabel();
        rateLabel = new javax.swing.JLabel();
        purchasesLabel = new javax.swing.JLabel();
        SalesLabel = new javax.swing.JLabel();
        stockLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        itemNameTbox = new javax.swing.JTextField();
        currentStockTbox = new javax.swing.JFormattedTextField();
        rateTbox = new javax.swing.JFormattedTextField();
        purchasesCbox = new javax.swing.JComboBox<>();
        salesCbox = new javax.swing.JComboBox<>();
        stockCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Data Entry (Stock)");
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

        itemNameLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        itemNameLabel.setText("Item Name");
        labelsPanel.add(itemNameLabel);

        currentStockLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        currentStockLabel.setText("Current Stock");
        labelsPanel.add(currentStockLabel);

        rateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        rateLabel.setText("Rate");
        labelsPanel.add(rateLabel);

        purchasesLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        purchasesLabel.setText("Purchases A/c");
        labelsPanel.add(purchasesLabel);

        SalesLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        SalesLabel.setText("Sales A/c");
        labelsPanel.add(SalesLabel);

        stockLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        stockLabel.setText("Stock A/c");
        labelsPanel.add(stockLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

        itemNameTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        itemNameTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                itemNameTboxFocusGained(evt);
            }
        });
        itemNameTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(itemNameTbox);

        currentStockTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##0.000"))));
        currentStockTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        currentStockTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                currentStockTboxFocusGained(evt);
            }
        });
        currentStockTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentStockTboxActionPerformed(evt);
            }
        });
        currentStockTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(currentStockTbox);

        rateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##0.00"))));
        rateTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
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

        purchasesCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        purchasesCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                purchasesCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(purchasesCbox);

        salesCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        salesCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                salesCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(salesCbox);

        stockCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        stockCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stockCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(stockCbox);

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
        titleLabel.setText("STOCK");
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void rateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rateTboxActionPerformed

    private void currentStockTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentStockTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currentStockTboxActionPerformed

    private void purchasesCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_purchasesCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
       if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkPurchasesChangedItem();
        }
    }//GEN-LAST:event_purchasesCboxKeyPressed

    private void salesCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_salesCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
       if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkSalesChangedItem();
        }
    }//GEN-LAST:event_salesCboxKeyPressed

    private void stockCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
       if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkStockChangedItem();
        }
    }//GEN-LAST:event_stockCboxKeyPressed

    private void keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedHandler
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_keyPressedHandler

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            insertData();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void itemNameTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_itemNameTboxFocusGained
        this.itemNameTbox.selectAll();
    }//GEN-LAST:event_itemNameTboxFocusGained

    private void currentStockTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_currentStockTboxFocusGained
        this.currentStockTbox.selectAll();
    }//GEN-LAST:event_currentStockTboxFocusGained

    private void rateTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTboxFocusGained
        this.rateTbox.selectAll();
    }//GEN-LAST:event_rateTboxFocusGained

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel SalesLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel currentStockLabel;
    private javax.swing.JFormattedTextField currentStockTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel itemNameLabel;
    private javax.swing.JTextField itemNameTbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JComboBox<String> purchasesCbox;
    private javax.swing.JLabel purchasesLabel;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JFormattedTextField rateTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JComboBox<String> salesCbox;
    private javax.swing.JComboBox<String> stockCbox;
    private javax.swing.JLabel stockLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables

    
}
