/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.CategoryDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.TransactionDB;
import java.awt.Dimension;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.ValidationChecks;
/**
 *
 * @author akshos
 */
public class AMaster extends javax.swing.JInternalFrame implements RefreshOption{
    
    DBConnection dbConnection;
    int level;
    RefreshOption prevFrame;
    Main mainFrame;
    int mode;
    String[][] categoryData;
    boolean customerAdded = false;
    boolean existing = false;
    String editId;
    
    String negativeCat="LI_IN_SL_SH_LN";
    String positiveCat="AS_EX_PR_SK_SE_DP";
    
    /**
     * Creates new form MasterEntry
     */
    public AMaster() {
        initComponents();
    }
    
    public AMaster(DBConnection db, int mode, String id, Main frame, int level){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        this.mode=mode;
        this.editId=id;
        initComponents();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
        prevFrame = null;
    }
    
    public AMaster(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.dbConnection = db;
        this.editId = id;
        this.level = level;
        this.mode=mode;
        this.prevFrame = prevFrame;
        this.mainFrame = frame;
        initComponents();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    private void loadPrevRecord(){
        
    }
        
    @Override
    public final void refreshContents(int code){
        if(code == Codes.REFRESH_ALL){
            loadCategory();
        }
        else if(code == Codes.REFRESH_CUSTOMERS){
            this.customerAdded = true;
            this.categoryCbox.transferFocus();
            this.insertData();
            this.nextEntry();
        }
    }
    
    private void loadContents(){
        String[] data = MasterDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        this.accountCodeTbox.setText(data[0]);
        int ret = TransactionDB.checkAccountIdPresent(dbConnection.getStatement(), data[0]);
        if(ret == Codes.EXISTING_ENTRY || ret == Codes.FAIL ){
            accountCodeTbox.setEditable(false);
        }
        this.accountHeadTbox.setText(data[1]);
        this.yopBalanceTbox.setText(data[2]);
        this.currentBalanceTbox.setText(data[3]);
        loadCategory();
        int indexValC=Arrays.asList(categoryData[0]).indexOf(data[4]);
        this.categoryCbox.setSelectedIndex(indexValC);
    }
    
    private void loadCategory(){
        System.out.println("loading categorycbox");
        categoryData = CategoryDB.getCategory(this.dbConnection.getStatement());
        if(categoryData  == null){
            return;
        }
        int len = categoryData[0].length;
        String[] cboxData = new String[len];
        for(int i = 0; i < len; i++){
            cboxData[i] = categoryData[0][i] + " : " + categoryData[1][i];
        }
        this.categoryCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    private void checkChangedItem(){
        int index = this.categoryCbox.getSelectedIndex();
        if(categoryData[0][index].compareTo("CR")==0 || categoryData[0][index].compareTo("DB")==0){
            String id = this.accountCodeTbox.getText();
            if(!CustomerDB.checkExisting(dbConnection.getStatement(), id)){
                addCustomer();
            }
        }
        else{
            this.categoryCbox.transferFocus();
        }
    }
    
    private void addCustomer(){
        String id = this.accountCodeTbox.getText();
        if(!CustomerDB.checkExisting(this.dbConnection.getStatement(), id)){
            String name = this.accountHeadTbox.getText();
            ACustomers item = new ACustomers(dbConnection, Codes.NEW_ENTRY, null, this.mainFrame, this.level+1, this, id, name);
            Dimension dim = Preferences.getInternalFrameDimension(item);
            if(dim != null){
                item.setSize(dim);
            }else{
                item.setSize(790, 470);
            }
            this.mainFrame.addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
        }
    }
    
    private boolean validateFields(String headName, int categoryIndex, String categoryItem){
        if(!checkCode()){
            return false;
        }
        if(headName.trim().isEmpty()){
            int ret = JOptionPane.showConfirmDialog(this, "Please enter a valid Account Head", "Invalid Entry", JOptionPane.WARNING_MESSAGE);
            if(ret == JOptionPane.OK_OPTION){
                return false;
            }
            else{
                this.doDefaultCloseAction();
            }
        }
        if(categoryItem.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please choose a valid category", "No category", JOptionPane.WARNING_MESSAGE);
                if(ret == JOptionPane.CANCEL_OPTION){
                    this.doDefaultCloseAction();
                }
                return false;
        }
        if( categoryData[0][categoryIndex].compareTo("CR")==0 || categoryData[0][categoryIndex].compareTo("DB")==0 ){
            if(!CustomerDB.checkExisting(dbConnection.getStatement(), this.accountCodeTbox.getText())){
                int ret = JOptionPane.showConfirmDialog(this, "Please add a customer first", "No customer", JOptionPane.WARNING_MESSAGE);
                if(ret == JOptionPane.CANCEL_OPTION){
                    this.doDefaultCloseAction();
                }
                return false;
            }
            
        }
        return true;
    }
    
    private void insertData(){        
        Statement stmt=dbConnection.getStatement();
        String accountCode  =accountCodeTbox.getText();
        String accountHead  =accountHeadTbox.getText();
        double currBalance = 0.0;
        if(!currentBalanceTbox.getText().isEmpty()){
             currBalance =Double.parseDouble(currentBalanceTbox.getText().replace(",", ""));
        }
        double yopBalance = 0.0;
        if(!yopBalanceTbox.getText().isEmpty()){
            yopBalance = Double.parseDouble(yopBalanceTbox.getText().replace(",", ""));
        }
        String category     ="";
        int index = this.categoryCbox.getSelectedIndex();
        String item = this.categoryCbox.getSelectedItem().toString();
        
        //Field validation
        if(!validateFields(accountHead, index, item)){
            System.out.println("Not validated.");
            cancelInsert();
            return;
        }
        category = categoryData[0][index];
        if(negativeCat.contains(category)){
            yopBalance=Math.abs(yopBalance)*-1;
        }
        else if(positiveCat.contains(category)){
            yopBalance=Math.abs(yopBalance);
        }
        boolean ret;
        
        if(mode==Codes.EDIT){
            dbConnection.startTransaction();
            ret = MasterDB.update(dbConnection.getStatement(), editId, accountHead, yopBalance, currBalance, category);
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                dbConnection.rollbackTransaction();
                return;
            }
            
            ret = CustomerDB.modifyName(dbConnection.getStatement(),editId,accountHead);
            if(!ret){
                JOptionPane.showMessageDialog(this, "Failed to change Customer Name", "Failed", JOptionPane.ERROR_MESSAGE);
                dbConnection.rollbackTransaction();
                return;
            }
            if(this.accountCodeTbox.isEditable() && accountCode.compareTo(editId) != 0){
                ret = MasterDB.modifyId(stmt, editId, accountCode);
                if(!ret){
                    JOptionPane.showMessageDialog(this, "Failed to change Account Code", "Failed", JOptionPane.ERROR_MESSAGE);
                    dbConnection.rollbackTransaction();
                    return;
                }
                
                ret = CustomerDB.modifyId(dbConnection.getStatement(), editId, accountCode);
                if(!ret){
                    JOptionPane.showMessageDialog(this, "Failed to change Customer Code", "Failed", JOptionPane.ERROR_MESSAGE);
                    dbConnection.rollbackTransaction();
                    return;
                }
            }
            dbConnection.endTransaction();
        }
        else{
            ret = MasterDB.insert(stmt, accountCode, accountHead, yopBalance, currBalance, category);
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                cancelInsert();
                return;
            }
        }
        
        if(prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_MASTER);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void cancelInsert(){
        int index = this.categoryCbox.getSelectedIndex();
        if( categoryData[0][index].compareTo("CR")==0 || categoryData[0][index].compareTo("DB")==0 ){
            if(CustomerDB.checkExisting(dbConnection.getStatement(), this.accountCodeTbox.getText())){
                CustomerDB.delete(dbConnection.getStatement(), this.accountCodeTbox.getText());
            }
        }
    }
    
    private void nextEntry(){
        this.accountCodeTbox.requestFocus();
        this.accountHeadTbox.setText("");
        this.yopBalanceTbox.setText("");
        this.currentBalanceTbox.setText("");
    }
    
    private boolean checkCode(){
        String code = this.accountCodeTbox.getText();
        if (mode == Codes.EDIT && code.compareTo(editId)==0)
            return true;
        if(ValidationChecks.validateCode(code.trim())){
            if(MasterDB.checkExisting(dbConnection.getStatement(), code)){
                this.existing = true;
                this.accountCodeLabel.setText("<html>Account Code <span style=\"color:red\">Existing Code</span></html>");
            }
            else{
                existing = false;
                this.accountCodeLabel.setText("<html>Account Code</html>");
            }
        }
        else{
            existing = true;
            this.accountCodeLabel.setText("<html>Account Code <span style=\"color:red\">Invalid</span></html>");
        }
        return !existing;
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
        accountCodeLabel = new javax.swing.JLabel();
        accountHeadLabel = new javax.swing.JLabel();
        yopBalLabel = new javax.swing.JLabel();
        currBalLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        accountCodeTbox = new javax.swing.JTextField();
        accountHeadTbox = new javax.swing.JTextField();
        yopBalanceTbox = new javax.swing.JFormattedTextField();
        currentBalanceTbox = new javax.swing.JFormattedTextField();
        categoryCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setTitle("Data Entry (Master)");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
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

        labelsPanel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        accountCodeLabel.setText("Account Code");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        accountHeadLabel.setText("Account Head");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        yopBalLabel.setText("YOP-Balance");
        labelsPanel.add(yopBalLabel);

        currBalLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        currBalLabel.setText("Current Balance");
        labelsPanel.add(currBalLabel);

        categoryLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        categoryLabel.setText("Category");
        labelsPanel.add(categoryLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        accountCodeTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                accountCodeTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                accountCodeTboxFocusLost(evt);
            }
        });
        accountCodeTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(accountCodeTbox);

        accountHeadTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        accountHeadTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                accountHeadTboxFocusGained(evt);
            }
        });
        accountHeadTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(accountHeadTbox);

        yopBalanceTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.00"))));
        yopBalanceTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        yopBalanceTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                yopBalanceTboxFocusGained(evt);
            }
        });
        yopBalanceTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(yopBalanceTbox);

        currentBalanceTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##,##,##0.00"))));
        currentBalanceTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        currentBalanceTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                currentBalanceTboxFocusGained(evt);
            }
        });
        currentBalanceTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(currentBalanceTbox);

        categoryCbox.setBackground(java.awt.Color.white);
        categoryCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        categoryCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                categoryCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(categoryCbox);

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

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void categoryCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkChangedItem();
        }
    }//GEN-LAST:event_categoryCboxKeyPressed

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void accountCodeTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountCodeTboxFocusLost
        checkCode();
    }//GEN-LAST:event_accountCodeTboxFocusLost

    private void accountCodeTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountCodeTboxFocusGained
        this.accountCodeTbox.selectAll();
    }//GEN-LAST:event_accountCodeTboxFocusGained

    private void accountHeadTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountHeadTboxFocusGained
        this.accountHeadTbox.selectAll();
    }//GEN-LAST:event_accountHeadTboxFocusGained

    private void yopBalanceTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_yopBalanceTboxFocusGained
        this.yopBalanceTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_yopBalanceTboxFocusGained

    private void currentBalanceTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_currentBalanceTboxFocusGained
        this.currentBalanceTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_currentBalanceTboxFocusGained

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.insertData();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusLost

    private void keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedHandler
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_keyPressedHandler
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountCodeLabel;
    private javax.swing.JTextField accountCodeTbox;
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JTextField accountHeadTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox<String> categoryCbox;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel currBalLabel;
    private javax.swing.JFormattedTextField currentBalanceTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel yopBalLabel;
    private javax.swing.JFormattedTextField yopBalanceTbox;
    // End of variables declaration//GEN-END:variables
}
