/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

/**
 *
 * @author akshos
 */
public class AMaster extends javax.swing.JInternalFrame {

    /**
     * Creates new form MasterEntry
     */
    public AMaster() {
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
        java.awt.GridBagConstraints gridBagConstraints;

        outerPanel = new javax.swing.JPanel();
        leftInerPannel = new javax.swing.JPanel();
        logoPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
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
        setTitle("Data Entry (Master)");
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

        labelsPanel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeLabel.setText("jLabel1");
        labelsPanel.add(accountCodeLabel);

        accountHeadLabel.setText("jLabel2");
        labelsPanel.add(accountHeadLabel);

        yopBalLabel.setText("jLabel3");
        labelsPanel.add(yopBalLabel);

        currBalLabel.setText("jLabel4");
        labelsPanel.add(currBalLabel);

        categoryLabel.setText("jLabel5");
        labelsPanel.add(categoryLabel);

        leftInerPannel.add(labelsPanel);

        outerPanel.add(leftInerPannel);

        rightInerPannel.setLayout(new java.awt.GridLayout(6, 0, 0, 10));

        accountCodeTbox.setText("jTextField1");
        rightInerPannel.add(accountCodeTbox);

        accountHeadTbox.setText("jTextField2");
        rightInerPannel.add(accountHeadTbox);

        yopBalanceTbox.setText("jTextField3");
        rightInerPannel.add(yopBalanceTbox);

        currentBalanceTbox.setText("jTextField4");
        rightInerPannel.add(currentBalanceTbox);

        categoryCbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel leftInerPannel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel outerPanel;
    private javax.swing.JPanel rightInerPannel;
    private javax.swing.JLabel yopBalLabel;
    private javax.swing.JTextField yopBalanceTbox;
    // End of variables declaration//GEN-END:variables
}