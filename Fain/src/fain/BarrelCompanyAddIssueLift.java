/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fain;

import database.BarrelDB;
import database.BranchDB;
import database.CompanyBarrelDB;
import database.CustomerDB;
import database.DBConnection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.UtilityFuncs;
import utility.ValidationChecks;
/**
 *
 * @author akshos
 */
public class BarrelCompanyAddIssueLift extends javax.swing.JInternalFrame implements RefreshOption{

     DBConnection dbConnection;
     Main mainFrame;
     int level;
     int mode;
     String editId;
     RefreshOption prevFrame;
     String branchData[][];
     String partyData[][];
    /**
     * Creates new form MasterEntry
     */
    public BarrelCompanyAddIssueLift() {
        initComponents();
    }
    public BarrelCompanyAddIssueLift(DBConnection db, int mode, String id, Main frame, int level){
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        this.editId=id;
        initComponents();
        loadCurrDate();
        refreshContents(Codes.REFRESH_ALL);
        prevFrame = null;
    }
    
    public BarrelCompanyAddIssueLift(DBConnection db, int mode, String id, Main frame, int level, RefreshOption prevFrame){
        this.prevFrame = prevFrame;
        this.level = level;
        this.mainFrame = frame;
        this.dbConnection = db;
        this.mode=mode;
        this.editId=id;
        initComponents();
        loadCurrDate();
        if(mode == Codes.EDIT) {this.loadContents(); System.out.println("editmode");}
        else refreshContents(Codes.REFRESH_ALL);
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.dateTbox.setText(date);
    }
    
    private void loadContents(){
        String[] data = CompanyBarrelDB.selectOneId(dbConnection.getStatement(), editId);
        if(data == null){
            System.out.println("Load Contents : selectedOneId has returned null");
            return;
        }
       
        this.dateTbox.setText(UtilityFuncs.dateSqlToUser(data[1]));
        this.barrelsIssuedTbox.setText(data[2]);
        this.barrelsLiftedTbox.setText(data[3]);
        this.differenceTbox.setText(data[4]);
    }

    
    private void setDifference(){
        int issued, lifted, difference;
        try{
            issued = Integer.parseInt(this.barrelsIssuedTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting issued to 0");
            issued = 0;
        }
        try{
            lifted = Integer.parseInt(this.barrelsLiftedTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting lifted to 0");
            lifted = 0;
        }
        difference = issued - lifted;
        this.differenceTbox.setText(String.valueOf(difference));
    }
    
    @Override
    public void refreshContents(int type) {

    }
    
    
    private void insertData(){
        Statement stmt = dbConnection.getStatement();
        
        
        String date= this.dateTbox.getText();

        if(!ValidationChecks.isDateValid(date)){
            JOptionPane.showMessageDialog(this, "Please enter a valid Date", "INVALID DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        date = UtilityFuncs.dateUserToSql(date);
        System.out.println("Date : " + date);
        
        int issued, lifted, difference;

        try{
            issued = Integer.parseInt(this.barrelsIssuedTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting issued to 0");
            issued = 0;
        }
        try{
            lifted = Integer.parseInt(this.barrelsLiftedTbox.getText());
        }catch(Exception e){
            System.out.println("Resetting lifted to 0");
            lifted = 0;
        }
        difference = issued - lifted;
  
        boolean ret;
        
        if(mode==Codes.EDIT){
            ret = CompanyBarrelDB.update(stmt, this.editId, date, String.valueOf(issued), String.valueOf(lifted), "0", String.valueOf(difference));
            if(ret){
                JOptionPane.showMessageDialog(this, "The entry has been updated", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to update", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
        }
        else{
            ret = CompanyBarrelDB.insert(stmt, date, String.valueOf(issued), String.valueOf(lifted), "0", String.valueOf(difference));
            if(ret){
                JOptionPane.showMessageDialog(this, "New entry has been successfully added", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Failed to add the new entry", "Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }    
        }
        
        if(prevFrame != null){
            prevFrame.refreshContents(Codes.REFRESH_BARREL);
            this.doDefaultCloseAction();
        }else{
            nextEntry();
        }
    }
    
    private void nextEntry(){
        this.barrelsIssuedTbox.setText("");
        this.barrelsLiftedTbox.setText("");
        this.differenceTbox.setText("");
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
        drcLabel = new javax.swing.JLabel();
        dryrubberLabel = new javax.swing.JLabel();
        rateLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        dateTbox = new javax.swing.JFormattedTextField();
        barrelsIssuedTbox = new javax.swing.JFormattedTextField();
        barrelsLiftedTbox = new javax.swing.JFormattedTextField();
        differenceTbox = new javax.swing.JFormattedTextField();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Barrel Issue Lift");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
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

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fain/note.png"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logoPanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        leftInerPannel.add(logoPanel);

        labelsPanel.setLayout(new java.awt.GridLayout(5, 0, 0, 10));

        dateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dateLabel.setText("Date");
        labelsPanel.add(dateLabel);

        drcLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        drcLabel.setText("Barrels Issued");
        labelsPanel.add(drcLabel);

        dryrubberLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dryrubberLabel.setText("Barrels Lifted");
        labelsPanel.add(dryrubberLabel);

        rateLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        rateLabel.setText("Difference");
        labelsPanel.add(rateLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(5, 0, 0, 10));

        try {
            dateTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        dateTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        dateTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dateTboxFocusGained(evt);
            }
        });
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

        barrelsIssuedTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        barrelsIssuedTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        barrelsIssuedTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barrelsIssuedTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                barrelsIssuedTboxFocusLost(evt);
            }
        });
        barrelsIssuedTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barrelsIssuedTboxActionPerformed(evt);
            }
        });
        barrelsIssuedTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelsIssuedTbox);

        barrelsLiftedTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        barrelsLiftedTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        barrelsLiftedTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barrelsLiftedTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                barrelsLiftedTboxFocusLost(evt);
            }
        });
        barrelsLiftedTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barrelsLiftedTboxActionPerformed(evt);
            }
        });
        barrelsLiftedTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(barrelsLiftedTbox);

        differenceTbox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        differenceTbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        differenceTbox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                differenceTboxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                differenceTboxFocusLost(evt);
            }
        });
        differenceTbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                differenceTboxActionPerformed(evt);
            }
        });
        differenceTbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedHandler(evt);
            }
        });
        rightInerPannel.add(differenceTbox);

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
        titleLabel.setText("BARREL ISSUE/LIFT");
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(titlePanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        // TODO add your handling code here:
        insertData();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void barrelsIssuedTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barrelsIssuedTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barrelsIssuedTboxActionPerformed

    private void barrelsLiftedTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barrelsLiftedTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barrelsLiftedTboxActionPerformed

    private void differenceTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_differenceTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_differenceTboxActionPerformed

    private void barrelsLiftedTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelsLiftedTboxFocusGained
        this.barrelsLiftedTbox.selectAll();
    }//GEN-LAST:event_barrelsLiftedTboxFocusGained

    private void barrelsIssuedTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelsIssuedTboxFocusGained
        this.barrelsIssuedTbox.selectAll();
    }//GEN-LAST:event_barrelsIssuedTboxFocusGained

    private void differenceTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_differenceTboxFocusGained
        this.setDifference();
        this.differenceTbox.selectAll();
    }//GEN-LAST:event_differenceTboxFocusGained

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

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Preferences.storeInternalFrameDimension(this);        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosing

    private void dateTboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateTboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxActionPerformed

    private void dateTboxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dateTboxFocusGained
        this.dateTbox.setCaretPosition(0);        // TODO add your handling code here:
    }//GEN-LAST:event_dateTboxFocusGained

    private void differenceTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_differenceTboxFocusLost
                        // TODO add your handling code here:
    }//GEN-LAST:event_differenceTboxFocusLost

    private void barrelsIssuedTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelsIssuedTboxFocusLost
                // TODO add your handling code here:
    }//GEN-LAST:event_barrelsIssuedTboxFocusLost

    private void barrelsLiftedTboxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barrelsLiftedTboxFocusLost
        this.setDifference();
    }//GEN-LAST:event_barrelsLiftedTboxFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField barrelsIssuedTbox;
    private javax.swing.JFormattedTextField barrelsLiftedTbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JFormattedTextField dateTbox;
    private javax.swing.JFormattedTextField differenceTbox;
    private javax.swing.JLabel drcLabel;
    private javax.swing.JLabel dryrubberLabel;
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables

    
}
