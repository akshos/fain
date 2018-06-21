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
import utility.UtilityFuncs;
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
        
    @Override
    public final void refreshContents(int code){
        if(code == Codes.REFRESH_ALL){
            loadCategoryData();
        }
        else if(code == Codes.REFRESH_CUSTOMERS){
            this.customerAdded = true;
            this.categoryTbox.transferFocus();
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
        
        loadCategoryData();
        this.categoryTbox.setText(data[4]);
        this.validateCategory();
    }
    
    private void loadCategoryData(){
        System.out.println("loading cateogry data");
        categoryData = CategoryDB.getCategory(this.dbConnection.getStatement());
    }
    
    private boolean validateCategory(){
        String catCode = this.categoryTbox.getText().toUpperCase();
        String catName = CategoryDB.getCategoryName(dbConnection.getStatement(), catCode);
        if(catName != null){
            this.categoryNameLabel.setText(catName);
            return true;
        }else{
            this.categoryNameLabel.setText("NOT FOUND");
            return false;
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
        }else{
            this.insertData();
        }
    }
    
    private void checkCategory(){
        String catCode = this.categoryTbox.getText().toUpperCase();
        if( catCode.compareTo("CR")==0 || catCode.compareTo("DB")==0 ){
            addCustomer();
        }else{
            this.insertData();
        }
    }
    
    private void chooseCategory(){
        int index = UtilityFuncs.selectOption(this, " CATEGORY", categoryData);
        if(index != -1){
            this.categoryNameLabel.setText(categoryData[1][index]);
            this.categoryTbox.setText(categoryData[0][index]);
        }
    }
    
    private boolean validateFields(String headName){
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
        String cat = this.categoryTbox.getText().toUpperCase();
        if( cat.compareTo("CR")==0 || cat.compareTo("DB")==0 ){
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
        
        String category = this.categoryTbox.getText().toUpperCase();
        if(!validateCategory()){
            JOptionPane.showMessageDialog(this, "Please choose a valid category", "No category", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        
        //Field validation
        if(!validateFields(accountHead)){
            System.out.println("Not validated.");
            cancelInsert();
            return;
        }
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
        String cat = this.categoryTbox.getText();
        if( cat.compareTo("CR")==0 || cat.compareTo("DB")==0 ){
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
                this.accountCodeLabel.setText("<html>Account Code <span style=\"color:red\">Duplicate</span></html>");
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
        categoryPanel = new javax.swing.JPanel();
        categoryTbox = new javax.swing.JTextField();
        categoryNameLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

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

        accountCodeLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        accountCodeLabel.setText("Account Code");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        accountHeadLabel.setText("Account Head");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        yopBalLabel.setText("YOP-Balance");
        labelsPanel.add(yopBalLabel);

        currBalLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        currBalLabel.setText("Current Balance");
        labelsPanel.add(currBalLabel);

        categoryLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        categoryLabel.setText("Category");
        labelsPanel.add(categoryLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
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

        accountHeadTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
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
        yopBalanceTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
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
        currentBalanceTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
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

        categoryPanel.setLayout(new java.awt.BorderLayout());

        categoryTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        categoryTbox.setPreferredSize(new java.awt.Dimension(150, 23));
        categoryTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                categoryTboxFocusGained(evt);
            }
        });
        categoryTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                categoryTboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                categoryTboxKeyReleased(evt);
            }
        });
        categoryPanel.add(categoryTbox, java.awt.BorderLayout.LINE_START);

        categoryNameLabel.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        categoryPanel.add(categoryNameLabel, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(categoryPanel);

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
        titleLabel.setText("MASTER ACCOUNT");
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosed

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

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusLost

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

    private void categoryTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F10){
            this.chooseCategory();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkCategory();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocusBackward();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            javax.swing.JComponent cmp = (javax.swing.JComponent)evt.getSource();
            cmp.transferFocus();
        }
    }//GEN-LAST:event_categoryTboxKeyPressed

    private void categoryTboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryTboxKeyReleased
        this.validateCategory();
    }//GEN-LAST:event_categoryTboxKeyReleased

    private void categoryTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_categoryTboxFocusGained
        this.categoryTbox.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_categoryTboxFocusGained
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountCodeLabel;
    private javax.swing.JTextField accountCodeTbox;
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JTextField accountHeadTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel categoryNameLabel;
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JTextField categoryTbox;
    private javax.swing.JLabel currBalLabel;
    private javax.swing.JFormattedTextField currentBalanceTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel yopBalLabel;
    private javax.swing.JFormattedTextField yopBalanceTbox;
    // End of variables declaration//GEN-END:variables
}
