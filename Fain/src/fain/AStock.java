/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;
import database.DBConnection;
import database.MasterDB;
import database.StockDB;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
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
        initComponents();
        refreshContents(Codes.REFRESH_ALL);
        this.prevFrame = null;
    }
    
    public AStock(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        initComponents();
        refreshContents(Codes.REFRESH_ALL);
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        //String icode        =itemCodeTbox.getText();
        String iname        =itemNameTbox.getText();
        int currentStock    =Integer.parseInt(currentStockTbox.getText());
        double rate         =Double.parseDouble(rateTbox.getText());
        String purchase     ="";
        Object selectedItem = purchasesCbox.getSelectedItem();
        if (selectedItem != null)
        {
            purchase = selectedItem.toString();
        }      
        String sales     ="";
         selectedItem = salesCbox.getSelectedItem();
        if (selectedItem != null)
        {
            sales = selectedItem.toString();
        }
        String stock     ="";
         selectedItem = stockCbox.getSelectedItem();
        if (selectedItem != null)
        {
            stock = selectedItem.toString();
        }
        StockDB.insert(stmt, iname, currentStock, rate, purchase, sales, stock);
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_STOCK);
        }
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

        setClosable(true);
        setTitle("Data Entry (Stock)");
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

        labelsPanel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

        itemNameLabel.setText("Item Name");
        labelsPanel.add(itemNameLabel);

        currentStockLabel.setText("Current Stock");
        labelsPanel.add(currentStockLabel);

        rateLabel.setText("Rate");
        labelsPanel.add(rateLabel);

        purchasesLabel.setText("Purchases A/c");
        labelsPanel.add(purchasesLabel);

        SalesLabel.setText("Sales A/c");
        labelsPanel.add(SalesLabel);

        stockLabel.setText("Stock A/c");
        labelsPanel.add(stockLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));
        rightInerPannel.add(itemNameTbox);

        currentStockTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(""))));
        currentStockTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentStockTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(currentStockTbox);

        rateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        rateTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rateTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(rateTbox);

        purchasesCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        rightInerPannel.add(purchasesCbox);

        salesCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        rightInerPannel.add(salesCbox);

        stockCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        rightInerPannel.add(stockCbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        enterButton.setText("ENTER");
        enterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(enterButton, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(buttonPanel);

        outerPanel.add(rightInerPannel);

        getContentPane().add(outerPanel, java.awt.BorderLayout.CENTER);

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
    // End of variables declaration//GEN-END:variables

    
}
