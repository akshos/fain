/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.ConsumptionDB;
import database.DBConnection;
import database.MasterDB;
import java.sql.Statement;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class AConsumption extends javax.swing.JInternalFrame implements RefreshOption {

    DBConnection dbConnection;
    /**
     * Creates new form MasterEntry
     */
    public AConsumption() {
        initComponents();
    }

    public AConsumption(DBConnection db, int mode, String id){
        this.dbConnection = db;
        initComponents();
        if(mode == Codes.EDIT){
            refreshContents(Codes.REFRESH_ALL);
        }
    }    
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        
        String branch     ="";
        Object selectedItem = branchCbox.getSelectedItem();
        if (selectedItem != null)
        {
            branch = selectedItem.toString();
        }
        
        String date  =dateTbox.getText();
        String refno =referencenumberTbox.getText();
        
        String itemCode     ="";
        selectedItem = itemcodeCbox.getSelectedItem();
        if (selectedItem != null)
        {
            itemCode = selectedItem.toString();
        }
        String itemname  = itemnameTbox.getText();
        String narration = narrationTbox.getText();
        int quantity  = Integer.parseInt(quantityTbox.getText());
        
        ConsumptionDB.insert(stmt, branch, date, refno, itemCode, itemname, narration, quantity);
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
        dateTbox = new javax.swing.JTextField();
        referencenumberTbox = new javax.swing.JTextField();
        itemcodeCbox = new javax.swing.JComboBox<>();
        itemnameTbox = new javax.swing.JTextField();
        narrationTbox = new javax.swing.JTextField();
        quantityTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Consumption");
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

        labelsPanel.setLayout(new java.awt.GridLayout(8, 0, 0, 10));

        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        referencenumberLabel.setText("Reference Number");
        labelsPanel.add(referencenumberLabel);

        itemcodeLabel.setText("Item Code");
        labelsPanel.add(itemcodeLabel);

        itemnameLabel.setText("Item Name");
        labelsPanel.add(itemnameLabel);

        narrationLabel.setText("Narration");
        labelsPanel.add(narrationLabel);

        quantityLabel.setText("Quantity");
        labelsPanel.add(quantityLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(8, 0, 0, 10));

        branchCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        rightInerPannel.add(branchCbox);

        dateTbox.setText("jTextField1");
        rightInerPannel.add(dateTbox);

        referencenumberTbox.setText("jTextField2");
        rightInerPannel.add(referencenumberTbox);

        itemcodeCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        rightInerPannel.add(itemcodeCbox);

        itemnameTbox.setText("jTextField4");
        rightInerPannel.add(itemnameTbox);

        narrationTbox.setText("jTextField1");
        rightInerPannel.add(narrationTbox);

        quantityTbox.setText("jTextField5");
        rightInerPannel.add(quantityTbox);

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextField dateTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JComboBox<String> itemcodeCbox;
    private javax.swing.JLabel itemcodeLabel;
    private javax.swing.JLabel itemnameLabel;
    private javax.swing.JTextField itemnameTbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel narrationLabel;
    private javax.swing.JTextField narrationTbox;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JTextField quantityTbox;
    private javax.swing.JLabel referencenumberLabel;
    private javax.swing.JTextField referencenumberTbox;
    private javax.swing.JPanel rightInerPannel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void refreshContents(int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
