/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fain;

import database.UsersDB;
import javax.swing.JOptionPane;
import utility.Codes;
import users.User;

/**
 *
 * @author akshos
 */
public class CreateUser extends javax.swing.JInternalFrame {
    Main mainFrame;
    
    /** Creates new form createUser */
    public CreateUser() {
        initComponents();
    }
    
    public CreateUser(Main frame, int action, User user){
        initComponents();
        this.mainFrame = frame;
        initDetails(action, user);
    }
    
    public CreateUser(Main frame, int action){
        initComponents();
        this.mainFrame = frame;
        initDetails(action, null);
    }
    
    private boolean validateFields(){
        System.out.println("Validating fields");
        if(!User.validUsername(this.usernameTbox.getText())){
            this.usernameLabel.setText("<html>Username : <span style=\"color:red\">invalid</span></html>");
            return false;
        }
        else{
            this.usernameLabel.setText("<html>Username : ");
        }
        if(!User.validPassword(this.passwordPbox.getText())){
            this.passwordLabel.setText("<html>Password : <span style=\"color:red\">invalid</span></html>");
            return false;
        }
        else{
            this.usernameLabel.setText("<html>Password : ");
        }
        return true;
    }
    
    private void addUser(){
        if(!validateFields()){
            return;
        }
        String username = this.usernameTbox.getText().toLowerCase();
        int ret = UsersDB.existingUser(username);
        if(ret == Codes.EXISTING_USER){
            JOptionPane.showMessageDialog(this, "Username already exists", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }else if(ret == Codes.FAIL){
            JOptionPane.showMessageDialog(this, "Failed to check username ("+ret+")", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        String password = this.passwordPbox.getText().toLowerCase();
        String rePassword = this.rePasswodPbox.getText().toLowerCase();
        if(password.compareTo(rePassword) != 0){
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        int type = Codes.STANDARD_USER;
        if(this.typeCbox.getSelectedItem().toString().compareTo("Administrator") == 0){
            type = Codes.ADMIN_USER;
        }
        ret = UsersDB.addUser(username, password, type);
        mainFrame.initLogin();
        if(ret == Codes.SUCCESS){
            JOptionPane.showMessageDialog(this, "Account " + username + " created.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(this, "Account creation Failed ("+ret+")", "FAILED", JOptionPane.INFORMATION_MESSAGE);
        }
        this.doDefaultCloseAction();
    }
    
    public void initDetails(int action, User user){
        if(action == Codes.CREARTE_ADMIN){
            this.headingLabel.setText("Create an Administrator Account");
            this.typeCbox.setSelectedIndex(0);
            this.typeCbox.setEditable(false);
            this.typeCbox.setEnabled(false);
            this.setClosable(false);
        }
        else if(action == Codes.CREATE_USER){
            this.headingLabel.setText("Create a new User Account");
            this.typeCbox.setSelectedIndex(1);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        headingLabel = new javax.swing.JLabel();
        detailsPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        iconPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        labelPanel = new javax.swing.JPanel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        rPasswordLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        usernameTbox = new javax.swing.JTextField();
        passwordPbox = new javax.swing.JPasswordField();
        rePasswodPbox = new javax.swing.JPasswordField();
        typeCbox = new javax.swing.JComboBox<>();
        createButton = new javax.swing.JButton();

        setClosable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(459, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());

        headingLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        headingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headingLabel.setText("Create User");
        headingLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(headingLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        detailsPanel.setLayout(new java.awt.GridLayout(1, 2));

        leftPanel.setLayout(new java.awt.GridLayout(1, 2));

        iconPanel.setLayout(new java.awt.BorderLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fain/user.png"))); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        iconPanel.add(jLabel2, java.awt.BorderLayout.CENTER);

        leftPanel.add(iconPanel);

        labelPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        labelPanel.setLayout(new java.awt.GridLayout(5, 0, 10, 0));

        usernameLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        usernameLabel.setText("Username :");
        labelPanel.add(usernameLabel);

        passwordLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        passwordLabel.setText("Password :");
        labelPanel.add(passwordLabel);

        rPasswordLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        rPasswordLabel.setText("Retype Password :");
        labelPanel.add(rPasswordLabel);

        typeLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        typeLabel.setText("Type :");
        labelPanel.add(typeLabel);

        leftPanel.add(labelPanel);

        detailsPanel.add(leftPanel);

        rightPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 10, 10));
        rightPanel.setLayout(new java.awt.GridLayout(5, 0, 0, 15));

        usernameTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        usernameTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameTboxActionPerformed(evt);
            }
        });
        rightPanel.add(usernameTbox);

        passwordPbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        passwordPbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordPboxFocusGained(evt);
            }
        });
        passwordPbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordPboxActionPerformed(evt);
            }
        });
        rightPanel.add(passwordPbox);

        rePasswodPbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        rePasswodPbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rePasswodPboxFocusGained(evt);
            }
        });
        rePasswodPbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rePasswodPboxActionPerformed(evt);
            }
        });
        rightPanel.add(rePasswodPbox);

        typeCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        typeCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrator", "Standard User" }));
        typeCbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeCboxActionPerformed(evt);
            }
        });
        rightPanel.add(typeCbox);

        createButton.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        createButton.setText("CREATE");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        createButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                createButtonKeyPressed(evt);
            }
        });
        rightPanel.add(createButton);

        detailsPanel.add(rightPanel);

        getContentPane().add(detailsPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void passwordPboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordPboxFocusGained
        this.passwordPbox.selectAll();
    }//GEN-LAST:event_passwordPboxFocusGained

    private void rePasswodPboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rePasswodPboxFocusGained
        this.rePasswodPbox.selectAll();
    }//GEN-LAST:event_rePasswodPboxFocusGained

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        addUser();
    }//GEN-LAST:event_createButtonActionPerformed

    private void usernameTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameTboxActionPerformed
        this.passwordPbox.requestFocus();
    }//GEN-LAST:event_usernameTboxActionPerformed

    private void passwordPboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordPboxActionPerformed
        this.rePasswodPbox.requestFocus();
    }//GEN-LAST:event_passwordPboxActionPerformed

    private void rePasswodPboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rePasswodPboxActionPerformed
        this.typeCbox.requestFocus();
    }//GEN-LAST:event_rePasswodPboxActionPerformed

    private void typeCboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeCboxActionPerformed
        this.createButton.requestFocus();
    }//GEN-LAST:event_typeCboxActionPerformed

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        this.requestFocusInWindow();
        this.usernameTbox.requestFocus();
    }//GEN-LAST:event_formInternalFrameActivated

    private void createButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_createButtonKeyPressed
        if( evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER ){
            addUser();
        }
    }//GEN-LAST:event_createButtonKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JLabel headingLabel;
    private javax.swing.JPanel iconPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordPbox;
    private javax.swing.JLabel rPasswordLabel;
    private javax.swing.JPasswordField rePasswodPbox;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JComboBox<String> typeCbox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField usernameTbox;
    // End of variables declaration//GEN-END:variables

}
