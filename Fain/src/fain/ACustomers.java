/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import database.CategoryDB;
import database.ConsumptionDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import java.awt.Dimension;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class ACustomers extends javax.swing.JInternalFrame implements RefreshOption{

    DBConnection dbConnection;
    Main mainFrame;
    int level;
    RefreshOption prevFrame;
    String[][] branchData;
    String editId;
    int mode;
    /**
     * Creates new form MasterEntry
     */
    public ACustomers() {
        initComponents();
    }
    
    public ACustomers(DBConnection db, int mode, String id, Main frame, int level){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        initComponents();
        refreshContents(Codes.REFRESH_BRANCHES);
    }
    
    public ACustomers(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame, String code, String name){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.prevFrame = prevFrame;
        this.editId=id;
        this.mode=mode;
        initComponents();
        refreshContents(Codes.REFRESH_BRANCHES);
        if (mode==Codes.EDIT){
            loadContents();
        }
        else{
            this.codeTbox.setText(code);
            this.nameTbox.setText(name);
        }
        this.codeTbox.setEditable(false);
        this.nameTbox.setEditable(false);
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
        String[] data = CustomerDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
        loadBranch();
        int indexValP=Arrays.asList(branchData[0]).indexOf(data[3]);
        this.branchCbox.setSelectedIndex(indexValP);
        //branchCbox.setEnabled(false);
        this.codeTbox.setText(data[0]);
        this.nameTbox.setText(data[1]);
        this.addressTarea.setText(data[2]);
        this.kgstTbox.setText(data[4]);
        this.rbregnoTbox.setText(data[5]);
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        String code     =codeTbox.getText();
        String name     =nameTbox.getText();
        String address  =addressTarea.getText();

        String branch     ="";
        int selectedIndex = branchCbox.getSelectedIndex();
        String selectedItem = branchCbox.getSelectedItem().toString();
        if(selectedItem.compareTo("Add New") == 0){
            JOptionPane.showConfirmDialog(this, "Please select a valid Branch", "No branch selected", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            return;
        }
        branch = branchData[0][selectedIndex];
        String kgst = kgstTbox.getText();
        String rbregno = rbregnoTbox.getText();
        if(mode==Codes.EDIT){
            CustomerDB.update(stmt, code, name, address, branch, kgst, rbregno);
        }
        else
            CustomerDB.insert(stmt, code, name, address, branch, kgst, rbregno);
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_CUSTOMERS);
            this.doDefaultCloseAction();
        }
    }
    
    @Override
    public void refreshContents(int type) {
        if(type == Codes.REFRESH_BRANCHES){
            loadBranch();
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
    
    private void checkChangedItem(){
        String item = this.branchCbox.getSelectedItem().toString();
        if(item.compareTo("Add New") == 0){
            addNewBranch();
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
        accountCodeLabel = new javax.swing.JLabel();
        accountHeadLabel = new javax.swing.JLabel();
        yopBalLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        codeTbox = new javax.swing.JTextField();
        nameTbox = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        addressTarea = new javax.swing.JTextArea();
        branchCbox = new javax.swing.JComboBox<>();
        kgstTbox = new javax.swing.JTextField();
        rbregnoTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setTitle("Customer");
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

        labelsPanel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

        accountCodeLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        accountCodeLabel.setText("Code");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        accountHeadLabel.setText("Name");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        yopBalLabel.setText("Address");
        labelsPanel.add(yopBalLabel);

        categoryLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        categoryLabel.setText("Branch");
        labelsPanel.add(categoryLabel);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("KGST");
        labelsPanel.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("RB Registration Number");
        labelsPanel.add(jLabel3);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(7, 0, 0, 10));

        codeTbox.setBackground(java.awt.Color.white);
        codeTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        codeTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(codeTbox);

        nameTbox.setBackground(java.awt.Color.white);
        nameTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        nameTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(nameTbox);

        jScrollPane1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane1KeyPressed(evt);
            }
        });

        addressTarea.setColumns(20);
        addressTarea.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        addressTarea.setRows(5);
        addressTarea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                addressTareaFocusGained(evt);
            }
        });
        addressTarea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addressTareaKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(addressTarea);

        rightInerPannel.add(jScrollPane1);

        branchCbox.setBackground(java.awt.Color.white);
        branchCbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        branchCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                branchCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(branchCbox);

        kgstTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        kgstTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                kgstTboxFocusGained(evt);
            }
        });
        kgstTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(kgstTbox);

        rbregnoTbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rbregnoTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rbregnoTboxFocusGained(evt);
            }
        });
        rbregnoTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                rbregnoTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(rbregnoTbox);

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

    private void branchCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_branchCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.checkChangedItem();
        }
                if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_branchCboxKeyPressed

    private void addressTareaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addressTareaKeyPressed
         if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
            System.out.println(evt.getModifiers());
            javax.swing.JTextArea input = (javax.swing.JTextArea)evt.getSource();
            if(evt.getModifiers() > 0) input.transferFocusBackward();
            else input.transferFocus(); 
            evt.consume();
        }
    }//GEN-LAST:event_addressTareaKeyPressed

    private void jScrollPane1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane1KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.jScrollPane1.transferFocus();
        }
    }//GEN-LAST:event_jScrollPane1KeyPressed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.insertData();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost

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

    private void addressTareaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressTareaFocusGained
        this.addressTarea.selectAll();
    }//GEN-LAST:event_addressTareaFocusGained

    private void kgstTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kgstTboxFocusGained
        this.kgstTbox.selectAll();
    }//GEN-LAST:event_kgstTboxFocusGained

    private void rbregnoTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbregnoTboxFocusGained
        this.rbregnoTbox.selectAll();
    }//GEN-LAST:event_rbregnoTboxFocusGained

    private void rbregnoTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rbregnoTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.insertData();
        }
    }//GEN-LAST:event_rbregnoTboxKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountCodeLabel;
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JTextArea addressTarea;
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JTextField codeTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField kgstTbox;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JTextField nameTbox;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JTextField rbregnoTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel yopBalLabel;
    // End of variables declaration//GEN-END:variables

}
