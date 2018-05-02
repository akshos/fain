/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import database.DBConnection;
import database.MasterDB;
import java.sql.Statement;
import utility.Codes;

/**
 *
 * @author akshos
 */
public class ABranches extends javax.swing.JInternalFrame implements RefreshOption {

    /**
     * Creates new form MasterEntry
     */
    
    DBConnection dbConnection;
    int level;
    Main mainFrame;
    RefreshOption prevFrame;
    
    public ABranches() {
        initComponents();
    }
    
    public ABranches(DBConnection db, int mode, String id, Main frame, int level){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        this.prevFrame = null;
        initComponents();
        if(mode == Codes.EDIT){
            refreshContents(Codes.REFRESH_ALL);
        }
    }
    
    public ABranches(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        this.prevFrame = prevFrame;
        initComponents();
        if(mode == Codes.EDIT){
            refreshContents(Codes.REFRESH_ALL);
        }
    }
    
    private void insertData(){
        Statement stmt=dbConnection.getStatement();
        String code  =codeTbox.getText();//implement in database 
        String name  =nameTbox.getText();
        String addr  =addressTarea.getText();
        String kgst  =kgstTbox.getText();
        String rbno  =rbregnoTbox.getText();
        BranchDB.insert(stmt, name, addr, kgst, rbno);
        if(this.prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_BRANCHES);
            this.doDefaultCloseAction();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        codeTbox = new javax.swing.JTextField();
        nameTbox = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        addressTarea = new javax.swing.JTextArea();
        kgstTbox = new javax.swing.JTextField();
        rbregnoTbox = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setTitle("Branches");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
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

        labelsPanel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeLabel.setText("Code");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setText("Name");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setText("Address");
        labelsPanel.add(yopBalLabel);

        jLabel2.setText("KGST");
        labelsPanel.add(jLabel2);

        jLabel3.setText("RB Registration Number");
        labelsPanel.add(jLabel3);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));
        rightInerPannel.add(codeTbox);
        rightInerPannel.add(nameTbox);

        addressTarea.setColumns(20);
        addressTarea.setRows(5);
        addressTarea.setTabSize(0);
        jScrollPane1.setViewportView(addressTarea);

        rightInerPannel.add(jScrollPane1);
        rightInerPannel.add(kgstTbox);
        rightInerPannel.add(rbregnoTbox);

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

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_formKeyPressed

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountCodeLabel;
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JTextArea addressTarea;
    private javax.swing.JPanel buttonPanel;
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

    @Override
    public void refreshContents(int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
