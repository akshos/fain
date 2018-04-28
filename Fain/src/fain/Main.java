/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import java.awt.Dimension;
import javax.swing.Box;
import database.DBConnection;
import database.UsersDB;
import users.User;
import utility.Codes;
import javax.swing.JOptionPane;

/**
 *
 * @author akshos
 */
public class Main extends javax.swing.JFrame {
    javax.swing.JInternalFrame activeInternalFrame[];
    int level;
    DBConnection dbConnection;
    boolean dbFound;
    User user;
    
    javax.swing.JMenu logoutMenu;
    /**
     * Creates new form Main
     */
    public Main() {
        this.user = null;
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        initComponents();
        createLogoutMenu();
        this.activeInternalFrame = new javax.swing.JInternalFrame[5];
        this.level = 0;
        Preferences.loadAllProperties();
        initDatabase();
    }
    
    private void disableComponents(){
        this.fileMenu.setEnabled(false);
        this.editMenu.setEnabled(false);
        this.printingMenu.setEnabled(false);
        this.optionsMenu.setEnabled(false);
        this.logoutMenu.setEnabled(false);
    }
    
    private void enableComponents(){
        this.fileMenu.setEnabled(true);
        this.editMenu.setEnabled(true);
        this.printingMenu.setEnabled(true);
        this.optionsMenu.setEnabled(true);
        this.logoutMenu.setEnabled(true);
    }
    
    private void initLogin(){
        
        if( !UsersDB.chechUsersDB() ){
            CreateUser createUser = new CreateUser(Codes.CREARTE_ADMIN);
            this.addToMainDesktopPane(createUser, level);
        }
        else if(user == null){
            disableComponents();
            Login login = new Login(this, user, Codes.LOGIN);
            this.addToMainDesktopPane(login, level);
        }
    }
    
    public void loginSuccess(){
        this.enableComponents();
    }
    
    private void initDatabase(){
        dbConnection = new DBConnection();
        if(dbConnection.checkDatabaseAvailability()){
            dbConnection.connect();
            dbFound = true;
        }
        else{
            dbFound = false;
        }
    }

    private void createLogoutMenu(){
        logoutMenu = new javax.swing.JMenu();
        logoutMenu.setFont(new java.awt.Font("Dialog", 1, 14));
        logoutMenu.setText("Logout");
        logoutMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ((javax.swing.JMenu)evt.getSource()).setSelected(false);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ((javax.swing.JMenu)evt.getSource()).setSelected(true);
            }
        });
        fainMainMenu.add(Box.createHorizontalGlue());
        fainMainMenu.add(logoutMenu);
    }
    
    private void setDatabaseStatus(){
        if(this.dbFound == true){
            String dbName = dbConnection.getDatabaseName();
            String status = "<html>Database : <span style=\"color:blue\">" + dbName + "</span></html>";
            this.databaseNameStatus.setText(status);
        }
        else{
            String status = "<html>Database : <span style=\"color:red\">NOT FOUND</span></html>";
            this.databaseNameStatus.setText(status);
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

        mainDesktopPane = new javax.swing.JDesktopPane();
        statusPanel = new javax.swing.JPanel();
        databaseNameStatus = new javax.swing.JLabel();
        fainMainMenu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        aMasterMenuItem = new javax.swing.JMenuItem();
        aTransactionMenuItem = new javax.swing.JMenuItem();
        aStockMenuItem = new javax.swing.JMenuItem();
        aPurchaseLatexMenuItem = new javax.swing.JMenuItem();
        aPurchaseOthersMenuItem = new javax.swing.JMenuItem();
        aSalesLatexMenuItem = new javax.swing.JMenuItem();
        aConsumptionMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        eMasterMenuItem = new javax.swing.JMenuItem();
        eTransactionMenuItem = new javax.swing.JMenuItem();
        eStockMenuItem = new javax.swing.JMenuItem();
        ePurchaseLatexMenuItem = new javax.swing.JMenuItem();
        ePurchaseOthersMenuItem = new javax.swing.JMenuItem();
        eSalesLatexMenuItem = new javax.swing.JMenuItem();
        eBranchesMenuItem = new javax.swing.JMenuItem();
        eCustomersMenuItem = new javax.swing.JMenuItem();
        eConsumptionMenuItem = new javax.swing.JMenuItem();
        printingMenu = new javax.swing.JMenu();
        optionsMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        mainDesktopPane.setName("mainDesktopPane"); // NOI18N

        javax.swing.GroupLayout mainDesktopPaneLayout = new javax.swing.GroupLayout(mainDesktopPane);
        mainDesktopPane.setLayout(mainDesktopPaneLayout);
        mainDesktopPaneLayout.setHorizontalGroup(
            mainDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 699, Short.MAX_VALUE)
        );
        mainDesktopPaneLayout.setVerticalGroup(
            mainDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 441, Short.MAX_VALUE)
        );

        getContentPane().add(mainDesktopPane, java.awt.BorderLayout.CENTER);

        statusPanel.setMaximumSize(new java.awt.Dimension(32767, 30));
        statusPanel.setPreferredSize(new java.awt.Dimension(699, 25));
        statusPanel.setLayout(new java.awt.GridLayout());

        databaseNameStatus.setText("jLabel1");
        statusPanel.add(databaseNameStatus);

        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        fainMainMenu.setName("fainMenuBar"); // NOI18N
        fainMainMenu.setPreferredSize(new java.awt.Dimension(240, 30));

        fileMenu.setText("Append");
        fileMenu.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.setPreferredSize(new java.awt.Dimension(80, 21));
        fileMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fileMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fileMenuMouseEntered(evt);
            }
        });

        aMasterMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aMasterMenuItem.setText("Master");
        aMasterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aMasterMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aMasterMenuItem);

        aTransactionMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aTransactionMenuItem.setText("Transaction");
        aTransactionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aTransactionMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aTransactionMenuItem);

        aStockMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aStockMenuItem.setText("Stock");
        aStockMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aStockMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aStockMenuItem);

        aPurchaseLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aPurchaseLatexMenuItem.setText("Purchase Latex");
        aPurchaseLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aPurchaseLatexMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aPurchaseLatexMenuItem);

        aPurchaseOthersMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aPurchaseOthersMenuItem.setText("Purchase Others");
        aPurchaseOthersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aPurchaseOthersMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aPurchaseOthersMenuItem);

        aSalesLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aSalesLatexMenuItem.setText("Sales Latex");
        aSalesLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aSalesLatexMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aSalesLatexMenuItem);

        aConsumptionMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aConsumptionMenuItem.setText("Consumption");
        aConsumptionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aConsumptionMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aConsumptionMenuItem);

        fainMainMenu.add(fileMenu);

        editMenu.setText("Edit");
        editMenu.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        editMenu.setPreferredSize(new java.awt.Dimension(50, 21));
        editMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editMenuMouseEntered(evt);
            }
        });

        eMasterMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eMasterMenuItem.setText("Master");
        eMasterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eMasterMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eMasterMenuItem);

        eTransactionMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eTransactionMenuItem.setText("Transaction");
        eTransactionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eTransactionMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eTransactionMenuItem);

        eStockMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eStockMenuItem.setText("Stock");
        eStockMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eStockMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eStockMenuItem);

        ePurchaseLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        ePurchaseLatexMenuItem.setText("Purchase Latex");
        ePurchaseLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ePurchaseLatexMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(ePurchaseLatexMenuItem);

        ePurchaseOthersMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        ePurchaseOthersMenuItem.setText("Purchase Others");
        ePurchaseOthersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ePurchaseOthersMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(ePurchaseOthersMenuItem);

        eSalesLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eSalesLatexMenuItem.setText("Sales Latex");
        eSalesLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eSalesLatexMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eSalesLatexMenuItem);

        eBranchesMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eBranchesMenuItem.setText("Branches");
        eBranchesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eBranchesMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eBranchesMenuItem);

        eCustomersMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eCustomersMenuItem.setText("Customers");
        eCustomersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eCustomersMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eCustomersMenuItem);

        eConsumptionMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eConsumptionMenuItem.setText("Consumption");
        eConsumptionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eConsumptionMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eConsumptionMenuItem);

        fainMainMenu.add(editMenu);

        printingMenu.setText("Printing");
        printingMenu.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        printingMenu.setPreferredSize(new java.awt.Dimension(80, 21));
        printingMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                printingMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                printingMenuMouseEntered(evt);
            }
        });
        fainMainMenu.add(printingMenu);

        optionsMenu.setText("Options");
        optionsMenu.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        optionsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                optionsMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                optionsMenuMouseEntered(evt);
            }
        });
        fainMainMenu.add(optionsMenu);

        setJMenuBar(fainMainMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileMenuMouseEntered
        ((javax.swing.JMenu)evt.getSource()).setSelected(true);
    }//GEN-LAST:event_fileMenuMouseEntered

    private void fileMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileMenuMouseExited
        ((javax.swing.JMenu)evt.getSource()).setSelected(false);
    }//GEN-LAST:event_fileMenuMouseExited

    private void editMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editMenuMouseEntered
        ((javax.swing.JMenu)evt.getSource()).setSelected(true);
    }//GEN-LAST:event_editMenuMouseEntered

    private void editMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editMenuMouseExited
        ((javax.swing.JMenu)evt.getSource()).setSelected(false);
    }//GEN-LAST:event_editMenuMouseExited

    private void printingMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_printingMenuMouseEntered
        ((javax.swing.JMenu)evt.getSource()).setSelected(true);
    }//GEN-LAST:event_printingMenuMouseEntered

    private void printingMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_printingMenuMouseExited
        ((javax.swing.JMenu)evt.getSource()).setSelected(false);
    }//GEN-LAST:event_printingMenuMouseExited

    private void optionsMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionsMenuMouseEntered
        ((javax.swing.JMenu)evt.getSource()).setSelected(true);
    }//GEN-LAST:event_optionsMenuMouseEntered

    private void optionsMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionsMenuMouseExited
        ((javax.swing.JMenu)evt.getSource()).setSelected(false);
    }//GEN-LAST:event_optionsMenuMouseExited

    private void aMasterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aMasterMenuItemActionPerformed
        AMaster item = new AMaster();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            System.out.println("setting size");
            item.setSize(dim);
        }else{
            item.setSize(790, 300);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_aMasterMenuItemActionPerformed

    private void eMasterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eMasterMenuItemActionPerformed
        EMaster item = new EMaster();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(785, 470);
        }
        item.setSize(785, 470);
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_eMasterMenuItemActionPerformed

    private void aTransactionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aTransactionMenuItemActionPerformed
        ATransaction item = new ATransaction();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 310);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_aTransactionMenuItemActionPerformed

    private void aStockMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aStockMenuItemActionPerformed
        AStock item = new AStock();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 360);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_aStockMenuItemActionPerformed

    private void aPurchaseLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aPurchaseLatexMenuItemActionPerformed
        APLatex item = new APLatex();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 450);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_aPurchaseLatexMenuItemActionPerformed

    private void aPurchaseOthersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aPurchaseOthersMenuItemActionPerformed
        APOthers item = new APOthers();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 410);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_aPurchaseOthersMenuItemActionPerformed

    private void aSalesLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aSalesLatexMenuItemActionPerformed
        ASLatex item = new ASLatex();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 530);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_aSalesLatexMenuItemActionPerformed

    private void aConsumptionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aConsumptionMenuItemActionPerformed
        AConsumption item = new AConsumption();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 380);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_aConsumptionMenuItemActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        setDatabaseStatus();
        initLogin();
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Preferences.storeAllProperties();
    }//GEN-LAST:event_formWindowClosing

    private void eTransactionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eTransactionMenuItemActionPerformed
        ETransaction item = new ETransaction();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_eTransactionMenuItemActionPerformed

    private void eStockMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eStockMenuItemActionPerformed
        EStock item = new EStock();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_eStockMenuItemActionPerformed

    private void ePurchaseLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ePurchaseLatexMenuItemActionPerformed
        EPLatex item = new EPLatex();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_ePurchaseLatexMenuItemActionPerformed

    private void ePurchaseOthersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ePurchaseOthersMenuItemActionPerformed
        EPOthers item = new EPOthers();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_ePurchaseOthersMenuItemActionPerformed

    private void eSalesLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eSalesLatexMenuItemActionPerformed
        ESLatex item = new ESLatex();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_eSalesLatexMenuItemActionPerformed

    private void eBranchesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eBranchesMenuItemActionPerformed
        EBranches item = new EBranches();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_eBranchesMenuItemActionPerformed

    private void eCustomersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eCustomersMenuItemActionPerformed
        ECustomers item = new ECustomers();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_eCustomersMenuItemActionPerformed

    private void eConsumptionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eConsumptionMenuItemActionPerformed
        EConsumption item = new EConsumption();
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level);
    }//GEN-LAST:event_eConsumptionMenuItemActionPerformed
    
    /**
     * @param item the internal frame to be added to desktop pane
     */
    private void addToMainDesktopPane(javax.swing.JInternalFrame item, int level){
        if(this.activeInternalFrame[level] != null){
            this.activeInternalFrame[level].doDefaultCloseAction();
        }
        this.activeInternalFrame[level] = item;
        item.setVisible(true);
        Dimension desktopSize = this.mainDesktopPane.getSize();
        Dimension internalFrameSize = item.getSize();
        item.setLocation((int)(desktopSize.getWidth() - internalFrameSize.getWidth())/2, 
                (int)(desktopSize.getHeight() - internalFrameSize.getHeight())/2);
        this.mainDesktopPane.add(item);
        this.mainDesktopPane.revalidate();
        this.mainDesktopPane.repaint();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aConsumptionMenuItem;
    private javax.swing.JMenuItem aMasterMenuItem;
    private javax.swing.JMenuItem aPurchaseLatexMenuItem;
    private javax.swing.JMenuItem aPurchaseOthersMenuItem;
    private javax.swing.JMenuItem aSalesLatexMenuItem;
    private javax.swing.JMenuItem aStockMenuItem;
    private javax.swing.JMenuItem aTransactionMenuItem;
    private javax.swing.JLabel databaseNameStatus;
    private javax.swing.JMenuItem eBranchesMenuItem;
    private javax.swing.JMenuItem eConsumptionMenuItem;
    private javax.swing.JMenuItem eCustomersMenuItem;
    private javax.swing.JMenuItem eMasterMenuItem;
    private javax.swing.JMenuItem ePurchaseLatexMenuItem;
    private javax.swing.JMenuItem ePurchaseOthersMenuItem;
    private javax.swing.JMenuItem eSalesLatexMenuItem;
    private javax.swing.JMenuItem eStockMenuItem;
    private javax.swing.JMenuItem eTransactionMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuBar fainMainMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JDesktopPane mainDesktopPane;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JMenu printingMenu;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
