/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.DBConnection;
import database.MasterDB;
import database.SalesDB;
import java.sql.Statement;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class ASLatex extends javax.swing.JInternalFrame implements RefreshOption{

    DBConnection dbConnection;
    /**
     * Creates new form MasterEntry
     */
    public ASLatex() {
        initComponents();
    }
    public ASLatex(DBConnection db, int mode){
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
        String bill = prbillTbox.getText();
        String party      ="";
        selectedItem = partyCbox.getSelectedItem();
        if (selectedItem != null)
        {
            party = selectedItem.toString();
        }
        int bnto     =Integer.parseInt(barrelToTbox.getText());
        int bnfrom      =Integer.parseInt(barrelfromTbox.getText());
        double quantity =Double.parseDouble(quantityTbox.getText());
        double drc      =Double.parseDouble(drcTbox.getText());
        double dryrubber=Double.parseDouble(dryrubberTbox.getText());
        double rate     =Double.parseDouble(rateTbox.getText());
        double value    =Double.parseDouble(valueTbox.getText());

        SalesDB.insert(stmt, branch, date, bill, party, bnfrom, bnto, quantity, drc, dryrubber, rate, value);
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
        barrelfromLabel = new javax.swing.JLabel();
        barreltoLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        drcLabel = new javax.swing.JLabel();
        dryrubberLabel = new javax.swing.JLabel();
        rateLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchCbox = new javax.swing.JComboBox<>();
        dateTbox = new javax.swing.JTextField();
        prbillTbox = new javax.swing.JTextField();
        partyCbox = new javax.swing.JComboBox<>();
        barrelfromTbox = new javax.swing.JTextField();
        barrelToTbox = new javax.swing.JTextField();
        quantityTbox = new javax.swing.JTextField();
        drcTbox = new javax.swing.JTextField();
        dryrubberTbox = new javax.swing.JTextField();
        rateTbox = new javax.swing.JTextField();
        valueTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Sales Latex");
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

        labelsPanel.setLayout(new java.awt.GridLayout(12, 0, 0, 10));

        branchLabel.setText("Branch");
        labelsPanel.add(branchLabel);

        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        billnumberLabel.setText("Bill Number");
        labelsPanel.add(billnumberLabel);

        partyLabel.setText("Party");
        labelsPanel.add(partyLabel);

        barrelfromLabel.setText("Barrel # From");
        labelsPanel.add(barrelfromLabel);

        barreltoLabel.setText("Barrel # To");
        labelsPanel.add(barreltoLabel);

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

        rightInerPannel.setLayout(new java.awt.GridLayout(12, 0, 0, 10));

        branchCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        branchCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(branchCbox);

        dateTbox.setText("jTextField1");
        dateTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(dateTbox);

        prbillTbox.setText("jTextField2");
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

        barrelfromTbox.setText("jTextField1");
        barrelfromTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelfromTbox);

        barrelToTbox.setText("jTextField2");
        barrelToTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelToTbox);

        quantityTbox.setText("jTextField3");
        quantityTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(quantityTbox);

        drcTbox.setText("jTextField4");
        drcTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(drcTbox);

        dryrubberTbox.setText("jTextField5");
        dryrubberTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(dryrubberTbox);

        rateTbox.setText("jTextField6");
        rateTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(rateTbox);

        valueTbox.setText("jTextField7");
        valueTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
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
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField barrelToTbox;
    private javax.swing.JLabel barrelfromLabel;
    private javax.swing.JTextField barrelfromTbox;
    private javax.swing.JLabel barreltoLabel;
    private javax.swing.JLabel billnumberLabel;
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JLabel branchLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextField dateTbox;
    private javax.swing.JLabel drcLabel;
    private javax.swing.JTextField drcTbox;
    private javax.swing.JLabel dryrubberLabel;
    private javax.swing.JTextField dryrubberTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JComboBox<String> partyCbox;
    private javax.swing.JLabel partyLabel;
    private javax.swing.JTextField prbillTbox;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JTextField quantityTbox;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JTextField rateTbox;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JTextField valueTbox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void refreshContents(int REFRESH_ALL) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
