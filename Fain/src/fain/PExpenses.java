/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import database.BranchDB;
import javax.swing.JOptionPane;
import database.CategoryDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import java.awt.Cursor;
import java.awt.Dimension;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utility.Codes;
import reports.Expenses;
/**
 *
 * @author akshos
 */
public class PExpenses extends javax.swing.JInternalFrame{
    
    DBConnection dbConnection;
    int level;
    Main mainFrame;
    String branchData[][];
    /**
     * Creates new form MasterEntry
     */
    public PExpenses() {
        initComponents();
    }
    
    public PExpenses(DBConnection db, Main frame, int level){
        this.dbConnection = db;
        this.level = level;
        this.mainFrame = frame;
        initComponents();
        loadCurrDate();
        loadBranch();
    }
    
    private void loadCurrDate(){
        LocalDateTime now = LocalDateTime.now();
        Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date=df.format(currDate);
        this.fromDatePicker.setText(date);
        this.toDatePicker.setText(date);
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
            cboxData[i] = branchData[1][i] + " (" + branchData[0][i] + ")";
        }
        cboxData[len] = "All";
        this.branchCbox.setModel(new DefaultComboBoxModel(cboxData));
    }
    
    private void generateReport(){    
        setBusy();        
        
        if(branchData == null){
            JOptionPane.showMessageDialog(null, "No Available Branches", "No Branch", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String branch = "";
        
        String item = this.branchCbox.getSelectedItem().toString();
        if(item.compareToIgnoreCase("All") == 0){
            branch = item;
        }else{
            int index = this.branchCbox.getSelectedIndex();
            branch = this.branchData[0][index];
        }
        
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fromDate=null;
        Date selDate=null;
         try {
             selDate = df.parse(this.fromDatePicker.getText());
             System.out.println(selDate);
             DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
             String sqlDate=df1.format(selDate);
             System.out.println(sqlDate);
             fromDate=sqlDate.toString();
             fromDate=selDate.toString();
         } catch (ParseException ex) {
             Logger.getLogger(APLatex.class.getName()).log(Level.SEVERE, null, ex);
         }
        if(fromDate == null){
            JOptionPane.showMessageDialog(this, "Please enter Date From", "NO DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String toDate=null;
        selDate=null;
         try {
             selDate = df.parse(this.toDatePicker.getText());
             System.out.println(selDate);
             DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
             String sqlDate=df1.format(selDate);
             System.out.println(sqlDate);
             toDate=sqlDate.toString();
         } catch (ParseException ex) {
             Logger.getLogger(APLatex.class.getName()).log(Level.SEVERE, null, ex);
         }
        if(toDate == null){
            JOptionPane.showMessageDialog(this, "Please enter Date From", "NO DATE", JOptionPane.WARNING_MESSAGE);
            return;
        }  
        
        String paper = this.paperCbox.getSelectedItem().toString();
        String orientation = this.orientationCbox.getSelectedItem().toString();
        
        boolean ret = Expenses.createReport(dbConnection, paper, orientation, branch, fromDate, toDate);
        
        resetBusy();
    }
    
    private void setBusy(){
        this.enterButton.setEnabled(false);
        this.enterButton.setText("Please Wait");
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    
    private void resetBusy(){
        this.enterButton.setEnabled(true);
        this.enterButton.setText("ENTER");
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
        Branch = new javax.swing.JLabel();
        dateFromLabel = new javax.swing.JLabel();
        asOnLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        orientationLabel = new javax.swing.JLabel();
        rightInerPannel = new javax.swing.JPanel();
        branchCbox = new javax.swing.JComboBox<>();
        toDatePicker = new javax.swing.JFormattedTextField();
        fromDatePicker = new javax.swing.JFormattedTextField();
        paperCbox = new javax.swing.JComboBox<>();
        orientationCbox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        enterButton = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Statements");
        setPreferredSize(new java.awt.Dimension(450, 410));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
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

        Branch.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        Branch.setText("Branch");
        labelsPanel.add(Branch);

        dateFromLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dateFromLabel.setText("Date from");
        labelsPanel.add(dateFromLabel);

        asOnLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        asOnLabel.setText("Date To");
        labelsPanel.add(asOnLabel);

        paperLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        paperLabel.setText("Paper");
        labelsPanel.add(paperLabel);

        orientationLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        orientationLabel.setText("Orientation");
        labelsPanel.add(orientationLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        branchCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        branchCbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                branchCboxItemStateChanged(evt);
            }
        });
        rightInerPannel.add(branchCbox);

        try {
            toDatePicker.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        toDatePicker.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        rightInerPannel.add(toDatePicker);

        try {
            fromDatePicker.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        fromDatePicker.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        fromDatePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromDatePickerActionPerformed(evt);
            }
        });
        rightInerPannel.add(fromDatePicker);

        paperCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        paperCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A4", "Legal" }));
        paperCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                paperCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(paperCbox);

        orientationCbox.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        orientationCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Landscape", "Portrait" }));
        orientationCbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orientationCboxKeyPressed(evt);
            }
        });
        rightInerPannel.add(orientationCbox);

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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        Preferences.storeInternalFrameDimension(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        generateReport();
    }//GEN-LAST:event_enterButtonActionPerformed

    private void paperCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paperCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            paperCbox.transferFocus();
        }
    }//GEN-LAST:event_paperCboxKeyPressed

    private void orientationCboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orientationCboxKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            orientationCbox.transferFocus();
        }
    }//GEN-LAST:event_orientationCboxKeyPressed

    private void enterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterButtonKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE){
            this.doDefaultCloseAction();
        }
        else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.generateReport();
        }
    }//GEN-LAST:event_enterButtonKeyPressed

    private void branchCboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_branchCboxItemStateChanged
                // TODO add your handling code here:
    }//GEN-LAST:event_branchCboxItemStateChanged

    private void fromDatePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromDatePickerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fromDatePickerActionPerformed
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Branch;
    private javax.swing.JLabel asOnLabel;
    private javax.swing.JComboBox<String> branchCbox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel dateFromLabel;
    private javax.swing.JButton enterButton;
    private javax.swing.JFormattedTextField fromDatePicker;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JComboBox<String> orientationCbox;
    private javax.swing.JLabel orientationLabel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JComboBox<String> paperCbox;
    private javax.swing.JLabel paperLabel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JFormattedTextField toDatePicker;
    // End of variables declaration//GEN-END:variables
}
