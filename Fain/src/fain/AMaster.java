/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.DBConnection;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class AMaster extends javax.swing.JInternalFrame implements RefreshOption{
    
    DBConnection dbConnection;
    
    /**
     * Creates new form MasterEntry
     */
    public AMaster() {
        initComponents();
    }
    
    public AMaster(DBConnection db, int mode){
        this.dbConnection = db;
        initComponents();
        if(mode == Codes.EDIT){
            refreshContents(Codes.REFRESH_ALL);
        }
    }
        
    @Override
    public final void refreshContents(int code){
        
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
        yopBalanceTbox = new javax.swing.JTextField();
        currentBalanceTbox = new javax.swing.JTextField();
        categoryCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setTitle("Data Entry (Master)");
        setPreferredSize(new java.awt.Dimension(450, 410));
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

        accountCodeLabel.setText("Account Code");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setText("Account Head");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setText("YOP-Balance");
        labelsPanel.add(yopBalLabel);

        currBalLabel.setText("Current Balance");
        labelsPanel.add(currBalLabel);

        categoryLabel.setText("Category");
        labelsPanel.add(categoryLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                accountCodeTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(accountCodeTbox);

        accountHeadTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                accountHeadTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(accountHeadTbox);

        yopBalanceTbox.setText("0.00");
        yopBalanceTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                yopBalanceTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(yopBalanceTbox);

        currentBalanceTbox.setText("0.00");
        currentBalanceTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                currentBalanceTboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(currentBalanceTbox);

        categoryCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        categoryCbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryCboxActionPerformed(evt);
            }
        });
        categoryCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                categoryCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(categoryCbox);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 60, 2, 60));
        buttonPanel.setLayout(new java.awt.BorderLayout());

        enterButton.setText("ENTER");
        buttonPanel.add(enterButton, java.awt.BorderLayout.CENTER);

        rightInerPannel.add(buttonPanel);

        outerPanel.add(rightInerPannel);

        getContentPane().add(outerPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void categoryCboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryCboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryCboxActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void accountCodeTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountCodeTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_accountCodeTboxKeyPressed

    private void accountHeadTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accountHeadTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_accountHeadTboxKeyPressed

    private void yopBalanceTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_yopBalanceTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_yopBalanceTboxKeyPressed

    private void currentBalanceTboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_currentBalanceTboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_currentBalanceTboxKeyPressed

    private void categoryCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
    }//GEN-LAST:event_categoryCboxKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountCodeLabel;
    private javax.swing.JTextField accountCodeTbox;
    private javax.swing.JLabel accountHeadLabel;
    private javax.swing.JTextField accountHeadTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox<String> categoryCbox;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel currBalLabel;
    private javax.swing.JTextField currentBalanceTbox;
    private javax.swing.JButton enterButton;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel yopBalLabel;
    private javax.swing.JTextField yopBalanceTbox;
    // End of variables declaration//GEN-END:variables
}
