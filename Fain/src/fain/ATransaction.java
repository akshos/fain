/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import database.DBConnection;
import database.MasterDB;
import database.TransactionDB;
import java.awt.Dimension;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
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
        refreshContents(Codes.REFRESH_ALL);
        this.prevFrame = null;
        this.editId=id;
    }
    
    public ATransaction(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.editId=id;
        initComponents();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    @Override
    public void refreshContents(int type){
        if(type == Codes.REFRESH_ALL){
            loadCreditDebit();
            loadBranch();
        }
        else if(type == Codes.REFRESH_MASTER){
            loadCreditDebit();
        }
        else if(type == Codes.REFRESH_BRANCHES){
            loadBranch();
        }
    }
    
    private void loadBranch(){
        System.out.println("loading categorycbox");
        branchData = BranchDB.getBranch(this.dbConnection.getStatement());
        int len;
        if(branchData  == null){
            len =  0;
        }else{
            len = branchData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = branchData[0][i] + " : " + branchData[1][i];
        }
        cboxData[len] = "Add New";
        this.branchCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    private void loadContents(){
        String[] data = TransactionDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        this.dateTbox.setText(data[1]);
        loadBranch();
        loadCreditDebit();
        int indexValB=Arrays.asList(branchData[0]).indexOf(data[2]);
        this.branchCbox.setSelectedIndex(indexValB);
        int indexValD=Arrays.asList(accountData[0]).indexOf(data[3]);
        this.debitCbox.setSelectedIndex(indexValD);
        int indexValC=Arrays.asList(accountData[0]).indexOf(data[4]);
        this.creditCbox.setSelectedIndex(indexValC);
        this.amountTbox.setText(data[5]);
        this.narrationTbox.setText(data[6]);
    }
    
    private void loadCreditDebit(){
        int creditSelected = this.creditCbox.getSelectedIndex();
        int debitSelected = this.debitCbox.getSelectedIndex();
        System.out.println("loading credit and debit cbox");
        accountData = MasterDB.getAccountHead(this.dbConnection.getStatement());
        int len;
        if(accountData == null){
            len = 0;
        }else{
            len = accountData[0].length;
        }
        String[] cboxData = new String[len+1];
        for(int i = 0; i < len; i++){
            cboxData[i] = accountData[1][i] + " (" + accountData[0][i] + ")";
        }
        cboxData[len] = "Add New";
        this.creditCbox.setModel(new DefaultComboBoxModel(cboxData));
        this.debitCbox.setModel(new DefaultComboBoxModel(cboxData));
        this.creditCbox.setSelectedIndex(creditSelected);
        this.debitCbox.setSelectedIndex(debitSelected);
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        String date         =dateTbox.getText();
        
        String branch     ="";
        String item = branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a valid branch", "No branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int index = this.branchCbox.getSelectedIndex();
        branch = this.branchData[0][index];
        
        String debit = "";
        item = debitCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a debit Account", "No debit account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        index = this.debitCbox.getSelectedIndex();
        debit = this.accountData[0][index];
        
        String credit = "";
        item = creditCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a credit Account", "No credit account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        index = this.creditCbox.getSelectedIndex();
        credit = this.accountData[0][index];
        
        double amount         =Double.parseDouble(amountTbox.getText());
        String narration      =narrationTbox.getText();
        String tid = TransactionDB.generateTid();
        TransactionDB.insert(stmt, date, branch, debit, credit, amount, narration, tid);
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
    
    private void checkDebitChangedItem(){
        String item = this.debitCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewMasterAccount();
        }else{
            this.debitCbox.transferFocus();
        }
    }
    
    private void checkCreditChangedItem(){
        String item = this.debitCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewMasterAccount();
        }else{
            this.creditCbox.transferFocus();
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
        dateLabel = new javax.swing.JLabel();
        branchLabel = new javax.swing.JLabel();
        debitLabel = new javax.swing.JLabel();
        creditLabel = new javax.swing.JLabel();
        amountLabel = new javax.swing.JLabel();
        narrationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        dateTbox = new javax.swing.JFormattedTextField();
        branchCbox = new javax.swing.JComboBox<>();
        debitCbox = new javax.swing.JComboBox<>();
        creditCbox = new javax.swing.JComboBox<>();
        amountTbox = new javax.swing.JFormattedTextField();
        narrationTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Data Entry (Transaction)");
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

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        debitLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        debitLabel.setText("Debit ( R )");
        labelsPanel.add(debitLabel);

        creditLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        creditLabel.setText("Credit ( P )");
        labelsPanel.add(creditLabel);

        amountLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        amountLabel.setText("Amount");
        labelsPanel.add(amountLabel);

        narrationLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        narrationLabel.setText("Narration");
        labelsPanel.add(narrationLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

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

        branchCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        branchCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                branchCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(branchCbox);

        debitCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        debitCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                debitCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(debitCbox);

        creditCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        creditCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                creditCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(creditCbox);

        amountTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0.00"))));
        amountTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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

        narrationTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        narrationTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                narrationTboxFocusGained(evt);
            }
        });
        narrationTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
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

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
       // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void dateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxActionPerformed

    private void branchCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkBranchChangedItem();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_branchCboxKeyPressed

    private void debitCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_debitCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkDebitChangedItem();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_debitCboxKeyPressed

    private void creditCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_creditCboxKeyPressed
       if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkCreditChangedItem();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_creditCboxKeyPressed

    private void buttonPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buttonPanelKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_buttonPanelKeyPressed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_enterButtonKeyPressed

    private void keyPressedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedHandler
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amountLabel;
    private javax.swing.JFormattedTextField amountTbox;
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox<String> creditCbox;
    private javax.swing.JLabel creditLabel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JComboBox<String> debitCbox;
    private javax.swing.JLabel debitLabel;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel narrationLabel;
    private javax.swing.JTextField narrationTbox;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    // End of variables declaration//GEN-END:variables
}
