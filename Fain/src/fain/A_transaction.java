/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

/**
 *
 * @author Another
 */
public class A_transaction extends javax.swing.JInternalFrame {

    /**
     * Creates new form A_transaction
     */
    public A_transaction() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        date_tbox = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        branch_tbox = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        debit_tbox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        credit_tbox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        amount_tbox = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        narration_tbox = new javax.swing.JTextField();
        exit_button = new javax.swing.JButton();
        head_button = new javax.swing.JButton();

        setTitle("Transactions Data Entry");

        jPanel1.setLayout(new java.awt.GridLayout(7, 2, 20, 20));

        jLabel1.setText("Date");
        jPanel1.add(jLabel1);
        jPanel1.add(date_tbox);

        jLabel2.setText("Branch");
        jPanel1.add(jLabel2);
        jPanel1.add(branch_tbox);

        jLabel3.setText("Debit ( R )");
        jPanel1.add(jLabel3);
        jPanel1.add(debit_tbox);

        jLabel4.setText("Credit ( P )");
        jPanel1.add(jLabel4);
        jPanel1.add(credit_tbox);

        jLabel5.setText("Amount");
        jPanel1.add(jLabel5);
        jPanel1.add(amount_tbox);

        jLabel6.setText("Narration");
        jPanel1.add(jLabel6);
        jPanel1.add(narration_tbox);

        exit_button.setText("Esc : Exit");
        jPanel1.add(exit_button);

        head_button.setText("F10 : A/c Head");
        jPanel1.add(head_button);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField amount_tbox;
    private javax.swing.JTextField branch_tbox;
    private javax.swing.JTextField credit_tbox;
    private javax.swing.JTextField date_tbox;
    private javax.swing.JTextField debit_tbox;
    private javax.swing.JButton exit_button;
    private javax.swing.JButton head_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField narration_tbox;
    // End of variables declaration//GEN-END:variables
}
