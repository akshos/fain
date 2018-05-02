/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.DBConnection;
import database.MasterDB;
import database.PurchaseLatexDB;
import java.sql.Statement;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class APLatex extends javax.swing.JInternalFrame implements RefreshOption{

     DBConnection dbConnection;
     Main mainFrame;
     int level;
     RefreshOption prevFrame;
     
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
        initComponents();
        if(mode == Codes.EDIT){
            refreshContents(Codes.REFRESH_ALL);
        }
        prevFrame = null;
    }
    
    public APLatex(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
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
        String date = dateTbox.getText();
        String pbil = prbillTbox.getText();
        String party      ="";
        selectedItem = partyCbox.getSelectedItem();
        if (selectedItem != null)
        {
            party = selectedItem.toString();
        }
        double quantity=Double.parseDouble(quantityTbox.getText());
        double drc      =Double.parseDouble(drcTbox.getText());
        double dryrubber=Double.parseDouble(dryrubberTbox.getText());
        double rate     =Double.parseDouble(rateTbox.getText());
        double value    =Double.parseDouble(valueTbox.getText());

        PurchaseLatexDB.insert(stmt, branch, date, pbil, party, quantity, drc, dryrubber, rate, value);
        if(prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_PLATEX);
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
        dateTbox = new javax.swing.JFormattedTextField();
        prbillTbox = new javax.swing.JTextField();
        partyCbox = new javax.swing.JComboBox<>();
        quantityTbox = new javax.swing.JFormattedTextField();
        drcTbox = new javax.swing.JFormattedTextField();
        dryrubberTbox = new javax.swing.JFormattedTextField();
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

        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        prbillLabel.setText("Pr. Bill");
        labelsPanel.add(prbillLabel);

        partyLabel.setText("Party");
        labelsPanel.add(partyLabel);

        quantityLabel.setText("Quantity Kgs.");
        labelsPanel.add(quantityLabel);

        drcLabel.setText("D R C");
        labelsPanel.add(drcLabel);

        dryrubberLabel.setText("Dry Rubber Kgs.");
        labelsPanel.add(dryrubberLabel);

        rateLabel.setText("Rate");
        labelsPanel.add(rateLabel);

        valueLabel.setText("Value");
        labelsPanel.add(valueLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(10, 0, 0, 10));

        branchCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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
        dateTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(dateTbox);

        prbillTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(prbillTbox);

        partyCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        partyCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(partyCbox);

        quantityTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.000"))));
        quantityTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(quantityTbox);

        drcTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.000"))));
        drcTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drcTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(drcTbox);

        dryrubberTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.000"))));
        dryrubberTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dryrubberTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(dryrubberTbox);

        rateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        rateTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rateTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(rateTbox);

        valueTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        valueTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueTboxActionPerformed(evt);
            }
        });
        rightInerPannel.add(valueTbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        enterButton.setText("ENTER");
        enterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterButtonActionPerformed(evt);
            }
        });
        enterButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
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
    }//GEN-LAST:event_keyPressedHandler

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void dateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxActionPerformed

    private void quantityTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityTboxActionPerformed

    private void drcTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drcTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drcTboxActionPerformed

    private void dryrubberTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dryrubberTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dryrubberTboxActionPerformed

    private void rateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rateTboxActionPerformed

    private void valueTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_valueTboxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JLabel drcLabel;
    private javax.swing.JFormattedTextField drcTbox;
    private javax.swing.JLabel dryrubberLabel;
    private javax.swing.JFormattedTextField dryrubberTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JComboBox<String> partyCbox;
    private javax.swing.JLabel partyLabel;
    private javax.swing.JLabel prbillLabel;
    private javax.swing.JTextField prbillTbox;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JFormattedTextField quantityTbox;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JFormattedTextField rateTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JFormattedTextField valueTbox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void refreshContents(int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
