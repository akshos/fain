/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import database.ConsumptionDB;
import database.DBConnection;
import database.MasterDB;
import database.StockDB;
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
public class AConsumption extends javax.swing.JInternalFrame implements RefreshOption {

    DBConnection dbConnection;
    Main mainFrame;
    int level;
    RefreshOption prevFrame;
    String[][] branchData;
    String[][] itemData;
    String editId;
    /**
     * Creates new form MasterEntry
     */
    public AConsumption() {
        initComponents();
    }

    public AConsumption(DBConnection db, int mode, String id, Main frame, int level){
        this.mainFrame = frame;
        this.level = level;
        this.dbConnection = db;
        initComponents();
        refreshContents(Codes.REFRESH_ALL);
        prevFrame = null;
    }  
    
    public AConsumption(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.mainFrame = frame;
        this.level = level;
        this.dbConnection = db;
        this.editId=id;
        initComponents();
        if(mode == Codes.EDIT) this.loadContents();
        else refreshContents(Codes.REFRESH_ALL);
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
            cboxData[i] = branchData[0][i] + " : " + branchData[1][i];
        }
        cboxData[len] = "Add New";
        this.branchCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
        
    private void loadContents(){
        String[] data = ConsumptionDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        loadBranch();
        loadItem();
        int indexValB=Arrays.asList(branchData[0]).indexOf(data[1]);
        this.branchCbox.setSelectedIndex(indexValB);
        this.dateTbox.setText(data[2]);
        this.referenceNumberTbox.setText(data[3]);
        int indexValI=Arrays.asList(itemData[0]).indexOf(data[4]);
        this.itemCodeCbox.setSelectedIndex(indexValI);
        this.itemNameTbox.setText(data[5]);
        this.narrationTbox.setText(data[6]);
        this.quantityTbox.setText(data[7]);
    }
    
    private void loadItem(){
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
            cboxData[i] = itemData[0][i] + " : " + itemData[1][i];
        }
        cboxData[len] = "Add New";
        this.itemCodeCbox.setModel(new DefaultComboBoxModel(cboxData));
    }   
    
    @Override
    public void refreshContents(int type) {
        if(type == Codes.REFRESH_ALL){
            loadBranch();
            loadItem();
        }
        else if(type == Codes.REFRESH_STOCK){
            loadItem();
        }
        else if(type == Codes.REFRESH_BRANCHES){
            loadBranch();
        }
    }
    
    private boolean validateFields(String date, String refno, String narration){
        if(!ValidationChecks.isDateValid(date))
        {
            int ret = JOptionPane.showConfirmDialog(this, "Please enter a valid date dd/mm/yy", "Invalid date", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        String item;
        int index;
        String branch = "";
        index = this.branchCbox.getSelectedIndex();
        item = this.branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a valid Branch", "No Branch selected", JOptionPane.WARNING_MESSAGE);
            return;
        }  
        branch = this.branchData[0][index];
        
        String date  =dateTbox.getText();
        String refno =referenceNumberTbox.getText();
        
        String itemCode, itemName;
        index = this.itemCodeCbox.getSelectedIndex();
        item = this.itemCodeCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            int ret = JOptionPane.showConfirmDialog(this, "Please select a valid Item", "No Item selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        itemCode = this.itemData[0][index];
        itemName = this.itemData[1][index];
        String narration = narrationTbox.getText();
        double quantity  = Double.parseDouble(quantityTbox.getText().replace(",", ""));
        if(!validateFields(date, refno, narration)){
            return;
        }
        ConsumptionDB.insert(stmt, branch, date, refno, itemCode, itemName, narration, quantity);
        
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_CONSUMPTION);
        }
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
    
    private void addNewItem(){
        AStock item = new AStock(dbConnection, Codes.NEW_ENTRY, null, mainFrame, this.level+1, this);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 360);
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
    
    private void checkItemChangedItem(){
        String item = this.itemCodeCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewItem();
        }else{
            this.itemCodeCbox.transferFocus();
        }
    }
    
    private void changeItemName(){
        String selected = this.itemCodeCbox.getSelectedItem().toString();
        if(selected.compareTo("Add New") == 0){
            return;
        }
        int index = this.itemCodeCbox.getSelectedIndex();
        String code = this.itemData[0][index];
        String name = StockDB.getNameFromCode(this.dbConnection.getStatement(), code);
        this.itemNameTbox.setText(name);
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
        referencenumberLabel = new javax.swing.JLabel();
        itemcodeLabel = new javax.swing.JLabel();
        itemnameLabel = new javax.swing.JLabel();
        narrationLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchCbox = new javax.swing.JComboBox<>();
        dateTbox = new javax.swing.JFormattedTextField();
        referenceNumberTbox = new javax.swing.JFormattedTextField();
        itemCodeCbox = new javax.swing.JComboBox<>();
        itemNameTbox = new javax.swing.JTextField();
        narrationTbox = new javax.swing.JTextField();
        quantityTbox = new javax.swing.JFormattedTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Consumption");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
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

        branchLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        referencenumberLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        referencenumberLabel.setText("Reference Number");
        labelsPanel.add(referencenumberLabel);

        itemcodeLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        itemcodeLabel.setText("Item Code");
        labelsPanel.add(itemcodeLabel);

        itemnameLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        itemnameLabel.setText("Item Name");
        labelsPanel.add(itemnameLabel);

        narrationLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        narrationLabel.setText("Narration");
        labelsPanel.add(narrationLabel);

        quantityLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        quantityLabel.setText("Quantity");
        labelsPanel.add(quantityLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(8, 0, 0, 10));

        branchCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        branchCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                branchCboxKeyPressed(evt);
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
                dateTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(dateTbox);

        referenceNumberTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        referenceNumberTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        referenceNumberTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                referenceNumberTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(referenceNumberTbox);

        itemCodeCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        itemCodeCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
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

        itemNameTbox.setEditable(false);
        itemNameTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        itemNameTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNameTboxActionPerformed(evt);
            }
        });
        itemNameTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                itemNameTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(itemNameTbox);

        narrationTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        narrationTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                narrationTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(narrationTbox);

        quantityTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.000"))));
        quantityTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        quantityTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityTboxActionPerformed(evt);
            }
        });
        quantityTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                quantityTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(quantityTbox);

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

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
    // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void itemNameTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNameTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemNameTboxActionPerformed

    private void branchCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            checkBranchChangedItem();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_branchCboxKeyPressed

    private void itemCodeCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_itemCodeCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            checkItemChangedItem();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_itemCodeCboxKeyPressed

    private void dateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxActionPerformed

    private void quantityTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityTboxActionPerformed

    private void itemCodeCboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_itemCodeCboxItemStateChanged
        changeItemName();
    }//GEN-LAST:event_itemCodeCboxItemStateChanged

    private void dateTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.dateTbox.transferFocus();
        }
    }//GEN-LAST:event_dateTboxKeyPressed

    private void referenceNumberTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_referenceNumberTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.referenceNumberTbox.transferFocus();
        }
    }//GEN-LAST:event_referenceNumberTboxKeyPressed

    private void itemNameTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_itemNameTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.itemNameTbox.transferFocus();
        }
    }//GEN-LAST:event_itemNameTboxKeyPressed

    private void narrationTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_narrationTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.narrationTbox.transferFocus();
        }
    }//GEN-LAST:event_narrationTboxKeyPressed

    private void quantityTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_quantityTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.quantityTbox.transferFocus();
        }
    }//GEN-LAST:event_quantityTboxKeyPressed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            insertData();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost

    }//GEN-LAST:event_formFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JComboBox<String> itemCodeCbox;
    private javax.swing.JTextField itemNameTbox;
    private javax.swing.JLabel itemcodeLabel;
    private javax.swing.JLabel itemnameLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel narrationLabel;
    private javax.swing.JTextField narrationTbox;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JFormattedTextField quantityTbox;
    private javax.swing.JFormattedTextField referenceNumberTbox;
    private javax.swing.JLabel referencenumberLabel;
    private javax.swing.JPanel rightInerPannel;
    // End of variables declaration//GEN-END:variables

    
}
