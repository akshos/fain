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
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import users.User;
import utility.Codes;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import database.InfoDB;
import database.SessionInfoDB;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import utility.UtilityFuncs;

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
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 24));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 24));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 24));
        this.user = null;
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        initComponents();
        createLogoutMenu();
        this.activeInternalFrame = new javax.swing.JInternalFrame[10];
        this.level = 0;
        Preferences.loadAllProperties();  
        fainMainMenu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "none");
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
    
    public void initLogin(){
        if( !UsersDB.chechUsersDB() ){
            CreateUser createUser = new CreateUser(this, Codes.CREARTE_ADMIN);
            this.addToMainDesktopPane(createUser, level, Codes.OPTIONS);
        }
        else if(user == null){
            disableComponents();
            Login login = new Login(this, user, Codes.LOGIN);
            this.addToMainDesktopPane(login, level, Codes.OPTIONS);
        }
    }
    
    public void closeFain(){
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    
    public void setUser(User user){
        this.user = user;
    }
    
    public void loginSuccess(){
        this.enableComponents();
        initDatabase();
    }
    
    private boolean askForNewSession(){
        JTextField sessionNameTbox = new JTextField();
        sessionNameTbox.setFont(new java.awt.Font("Dialog", 1, 24));
        JLabel msg = new JLabel("Enter Session Name : ");
        msg.setFont(new java.awt.Font("Dialog", 1, 24));
        JLabel msg2 = new JLabel("(Alpabets, numbers, - , _ only)");
        msg2.setFont(new java.awt.Font("Dialog", 1, 18));
        final JComponent[] inputs = new JComponent[]{
            msg,
            new JLabel("(Alpabets, numbers, - , _ only)"),
            sessionNameTbox
        };
        while(true){
            int res = JOptionPane.showConfirmDialog(this, inputs, "Create a new Session", JOptionPane.OK_CANCEL_OPTION);
            if(res == JOptionPane.OK_OPTION){
                String sessionName = sessionNameTbox.getText();
                if(!InfoDB.validSessionName(sessionName)){
                    int ret2 = JOptionPane.showConfirmDialog(this, "Please enter a valid session name", "Invalid Session Name", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                InfoDB.addSessionName(sessionName);
                this.dbConnection.createNewDatabase(sessionName);
                this.dbConnection = new DBConnection(sessionName);
                this.dbConnection.connect();
                break;
            }
            else{
                return false;
            }
        }
        return true;
    }
    
    private boolean askForSessionChoice(){
        String[] sessionNames = InfoDB.getSessionNames();
        if(sessionNames == null){
            int ret = JOptionPane.showConfirmDialog(this, "The session list was not found", "ERROR", JOptionPane.ERROR_MESSAGE);
            closeFain();
        }
        JComboBox sessionNamesCbox = new JComboBox(sessionNames);
        String currSession = this.dbConnection.getDatabaseName();
        sessionNamesCbox.setSelectedItem(currSession);
        sessionNamesCbox.setFont(new java.awt.Font("Dialog", 1, 24));
        JLabel msg = new JLabel("Choose Session Name : ");
        msg.setFont(new java.awt.Font("Dialog", 1, 24));
        final JComponent[] inputs = new JComponent[]{
            msg,
            sessionNamesCbox
        };
        int res = JOptionPane.showConfirmDialog(this, inputs, "Choose a Session to continue", JOptionPane.PLAIN_MESSAGE);
        if(res == JOptionPane.OK_OPTION){
            String sessionName = sessionNamesCbox.getSelectedItem().toString();
            this.dbConnection = new DBConnection(sessionName);
            dbConnection.connect();
            return true;
        }
        return false;
    }
    
    private void initDatabase(){
        if(dbFound == true){
            return;
        }
        DBConnection.checkDBFolder();
        dbConnection = new DBConnection();
        int ret = dbConnection.checkDatabaseAvailability();
        if(ret == Codes.DATABASE_FOUND){
            askForSessionChoice();
            dbFound = true;
        }
        else if(ret == Codes.NO_DATABASE){
            if(askForNewSession()){
                dbFound = true;
            }else{
                dbFound = false;
            }
        }
        else if(ret == Codes.FAIL){
            
            dbFound = false;
        }
        setDatabaseStatus();
    }
    
    private void closeAllInternalFrames(){
        for( int i = 0; i < 5; i++ ){
            if(activeInternalFrame[i] != null){
                activeInternalFrame[i].doDefaultCloseAction();
            }
        }
    }
    
    private void addNewSession(){
        String status = "<html>Session : <span style=\"color:blue\">Updating</span></html>";
        this.databaseNameStatus.setText(status);
        if(askForNewSession()){
            dbFound = true;
            closeAllInternalFrames();
            setDatabaseStatus();
        }
        setDatabaseStatus();
    }
    
    private void loadSession(){
        String status = "<html>Session : <span style=\"color:blue\">Updating</span></html>";
        this.databaseNameStatus.setText(status);
        if(askForSessionChoice()){
            dbFound = true;
            closeAllInternalFrames();
        }
        setDatabaseStatus();
    }
    
    private void logout(){
        int ret = JOptionPane.showConfirmDialog(this, "Are you sure to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if(ret == JOptionPane.YES_OPTION){
            System.out.println("Logout");
            user = null;
            initLogin();
        }
    }
    
    private void createLogoutMenu(){
        logoutMenu = new javax.swing.JMenu();
        logoutMenu.setFont(new java.awt.Font("Dialog", 1, 24));
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
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt){
                logout();
            }
        });
        fainMainMenu.add(Box.createHorizontalGlue());
        fainMainMenu.add(logoutMenu);
    }
    
    private void setDatabaseStatus(){
        if(this.dbFound == true){
            String dbName = dbConnection.getDatabaseName();
            String status = "<html>Session : <span style=\"color:blue\">" + dbName + "</span></html>";
            this.databaseNameStatus.setText(status);
            SessionInfoDB.loadSessionDetails(dbConnection.getStatement()); //loading session details
        }
        else{
            String status = "<html>Session : <span style=\"color:red\">NOT FOUND</span></html>";
            this.databaseNameStatus.setText(status);
        }
        this.databaseNameStatus.repaint();
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
        aBranchesMenuItem = new javax.swing.JMenuItem();
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
        accountBooksMenu = new javax.swing.JMenu();
        dayBookMenuItem = new javax.swing.JMenuItem();
        cashBookMenuItem = new javax.swing.JMenuItem();
        bankBookMenuItem = new javax.swing.JMenuItem();
        statementsMenuItem = new javax.swing.JMenuItem();
        debtorsMenuItem = new javax.swing.JMenuItem();
        creditorsMenuItem = new javax.swing.JMenuItem();
        pLedgerMenuItem = new javax.swing.JMenuItem();
        pTrialBalanceMenuItem = new javax.swing.JMenuItem();
        pProfitLossBLMenuItem = new javax.swing.JMenuItem();
        pListOfAccountsMenuItem = new javax.swing.JMenuItem();
        pPartyWiseStatement = new javax.swing.JMenuItem();
        pPurchaseLatexMenuItem = new javax.swing.JMenuItem();
        expensesMenuItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();
        startNewSessionMenuItem = new javax.swing.JMenuItem();
        loadSessionMenuItem = new javax.swing.JMenuItem();
        sessionInfoMenuItem = new javax.swing.JMenuItem();
        addUserMenuItem = new javax.swing.JMenuItem();

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
        mainDesktopPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mainDesktopPaneKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout mainDesktopPaneLayout = new javax.swing.GroupLayout(mainDesktopPane);
        mainDesktopPane.setLayout(mainDesktopPaneLayout);
        mainDesktopPaneLayout.setHorizontalGroup(
            mainDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 699, Short.MAX_VALUE)
        );
        mainDesktopPaneLayout.setVerticalGroup(
            mainDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 426, Short.MAX_VALUE)
        );

        getContentPane().add(mainDesktopPane, java.awt.BorderLayout.CENTER);

        statusPanel.setMaximumSize(new java.awt.Dimension(32767, 30));
        statusPanel.setPreferredSize(new java.awt.Dimension(699, 30));
        statusPanel.setLayout(new java.awt.GridLayout(1, 0));

        databaseNameStatus.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        databaseNameStatus.setText("Session");
        statusPanel.add(databaseNameStatus);

        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        fainMainMenu.setName("fainMenuBar"); // NOI18N
        fainMainMenu.setPreferredSize(new java.awt.Dimension(240, 40));

        fileMenu.setMnemonic('a');
        fileMenu.setText("Append");
        fileMenu.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.setPreferredSize(new java.awt.Dimension(120, 30));
        fileMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fileMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fileMenuMouseEntered(evt);
            }
        });

        aMasterMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        aMasterMenuItem.setText("Master");
        aMasterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aMasterMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aMasterMenuItem);

        aTransactionMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        aTransactionMenuItem.setText("Transaction");
        aTransactionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aTransactionMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aTransactionMenuItem);

        aStockMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        aStockMenuItem.setText("Stock");
        aStockMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aStockMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aStockMenuItem);

        aPurchaseLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        aPurchaseLatexMenuItem.setText("Purchase Latex");
        aPurchaseLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aPurchaseLatexMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aPurchaseLatexMenuItem);

        aPurchaseOthersMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aPurchaseOthersMenuItem.setText("Purchase Others");
        aPurchaseOthersMenuItem.setEnabled(false);
        aPurchaseOthersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aPurchaseOthersMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aPurchaseOthersMenuItem);

        aSalesLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        aSalesLatexMenuItem.setText("Sales Latex");
        aSalesLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aSalesLatexMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aSalesLatexMenuItem);

        aConsumptionMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aConsumptionMenuItem.setText("Consumption");
        aConsumptionMenuItem.setEnabled(false);
        aConsumptionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aConsumptionMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aConsumptionMenuItem);

        aBranchesMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        aBranchesMenuItem.setText("Branches");
        aBranchesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aBranchesMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(aBranchesMenuItem);

        fainMainMenu.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");
        editMenu.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        editMenu.setPreferredSize(new java.awt.Dimension(70, 40));
        editMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editMenuMouseEntered(evt);
            }
        });

        eMasterMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        eMasterMenuItem.setText("Master");
        eMasterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eMasterMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eMasterMenuItem);

        eTransactionMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        eTransactionMenuItem.setText("Transaction");
        eTransactionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eTransactionMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eTransactionMenuItem);

        eStockMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        eStockMenuItem.setText("Stock");
        eStockMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eStockMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eStockMenuItem);

        ePurchaseLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        ePurchaseLatexMenuItem.setText("Purchase Latex");
        ePurchaseLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ePurchaseLatexMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(ePurchaseLatexMenuItem);

        ePurchaseOthersMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        ePurchaseOthersMenuItem.setText("Purchase Others");
        ePurchaseOthersMenuItem.setEnabled(false);
        ePurchaseOthersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ePurchaseOthersMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(ePurchaseOthersMenuItem);

        eSalesLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        eSalesLatexMenuItem.setText("Sales Latex");
        eSalesLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eSalesLatexMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eSalesLatexMenuItem);

        eBranchesMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        eBranchesMenuItem.setText("Branches");
        eBranchesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eBranchesMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eBranchesMenuItem);

        eCustomersMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        eCustomersMenuItem.setText("Customers");
        eCustomersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eCustomersMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eCustomersMenuItem);

        eConsumptionMenuItem.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        eConsumptionMenuItem.setText("Consumption");
        eConsumptionMenuItem.setEnabled(false);
        eConsumptionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eConsumptionMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(eConsumptionMenuItem);

        fainMainMenu.add(editMenu);

        printingMenu.setMnemonic('p');
        printingMenu.setText("Printing");
        printingMenu.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        printingMenu.setPreferredSize(new java.awt.Dimension(120, 40));
        printingMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                printingMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                printingMenuMouseEntered(evt);
            }
        });

        accountBooksMenu.setText("Account Books");
        accountBooksMenu.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N

        dayBookMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dayBookMenuItem.setText("Day Book");
        dayBookMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dayBookMenuItemActionPerformed(evt);
            }
        });
        accountBooksMenu.add(dayBookMenuItem);

        cashBookMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        cashBookMenuItem.setText("Cash Book");
        cashBookMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashBookMenuItemActionPerformed(evt);
            }
        });
        accountBooksMenu.add(cashBookMenuItem);

        bankBookMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        bankBookMenuItem.setText("Bank Book");
        bankBookMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bankBookMenuItemActionPerformed(evt);
            }
        });
        accountBooksMenu.add(bankBookMenuItem);

        printingMenu.add(accountBooksMenu);

        statementsMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        statementsMenuItem.setText("Statements");
        statementsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statementsMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(statementsMenuItem);

        debtorsMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        debtorsMenuItem.setText("Debtors");
        debtorsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debtorsMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(debtorsMenuItem);

        creditorsMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        creditorsMenuItem.setText("Creditors");
        creditorsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                creditorsMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(creditorsMenuItem);

        pLedgerMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        pLedgerMenuItem.setText("Ledgers");
        pLedgerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pLedgerMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(pLedgerMenuItem);

        pTrialBalanceMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        pTrialBalanceMenuItem.setText("Trial Balance");
        pTrialBalanceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pTrialBalanceMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(pTrialBalanceMenuItem);

        pProfitLossBLMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        pProfitLossBLMenuItem.setText("P&L and B/S");
        pProfitLossBLMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pProfitLossBLMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(pProfitLossBLMenuItem);

        pListOfAccountsMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        pListOfAccountsMenuItem.setText("List Of Accounts");
        pListOfAccountsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pListOfAccountsMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(pListOfAccountsMenuItem);

        pPartyWiseStatement.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        pPartyWiseStatement.setText("Party Wise Statement");
        pPartyWiseStatement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pPartyWiseStatementActionPerformed(evt);
            }
        });
        printingMenu.add(pPartyWiseStatement);

        pPurchaseLatexMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        pPurchaseLatexMenuItem.setText("Purchase Latex");
        pPurchaseLatexMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pPurchaseLatexMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(pPurchaseLatexMenuItem);

        expensesMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        expensesMenuItem.setText("Expenses");
        expensesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expensesMenuItemActionPerformed(evt);
            }
        });
        printingMenu.add(expensesMenuItem);

        fainMainMenu.add(printingMenu);

        optionsMenu.setMnemonic('o');
        optionsMenu.setText("Options");
        optionsMenu.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        optionsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                optionsMenuMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                optionsMenuMouseEntered(evt);
            }
        });

        startNewSessionMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        startNewSessionMenuItem.setText("Start New Session");
        startNewSessionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startNewSessionMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(startNewSessionMenuItem);

        loadSessionMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        loadSessionMenuItem.setText("Load Session");
        loadSessionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSessionMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(loadSessionMenuItem);

        sessionInfoMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        sessionInfoMenuItem.setText("Session Details");
        sessionInfoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sessionInfoMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(sessionInfoMenuItem);

        addUserMenuItem.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        addUserMenuItem.setText("Add User");
        addUserMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(addUserMenuItem);

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
        AMaster item = new AMaster(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            System.out.println("setting size");
            item.setSize(dim);
        }else{
            item.setSize(790, 300);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aMasterMenuItemActionPerformed

    private void eMasterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eMasterMenuItemActionPerformed
        EMaster item = new EMaster(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(785, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_eMasterMenuItemActionPerformed

    private void aTransactionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aTransactionMenuItemActionPerformed
        ATransaction item = new ATransaction(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 310);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aTransactionMenuItemActionPerformed

    private void aStockMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aStockMenuItemActionPerformed
        AStock item = new AStock(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 360);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aStockMenuItemActionPerformed

    private void aPurchaseLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aPurchaseLatexMenuItemActionPerformed
        APLatex item = new APLatex(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 450);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aPurchaseLatexMenuItemActionPerformed

    private void aPurchaseOthersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aPurchaseOthersMenuItemActionPerformed
        APOthers item = new APOthers(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 410);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aPurchaseOthersMenuItemActionPerformed

    private void aSalesLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aSalesLatexMenuItemActionPerformed
        ASLatex item = new ASLatex(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 530);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aSalesLatexMenuItemActionPerformed

    private void aConsumptionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aConsumptionMenuItemActionPerformed
        AConsumption item = new AConsumption(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 380);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aConsumptionMenuItemActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        initLogin();
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Preferences.storeAllProperties();
    }//GEN-LAST:event_formWindowClosing

    private void eTransactionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eTransactionMenuItemActionPerformed
        ETransaction item = new ETransaction(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_eTransactionMenuItemActionPerformed

    private void eStockMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eStockMenuItemActionPerformed
        EStock item = new EStock(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_eStockMenuItemActionPerformed

    private void ePurchaseLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ePurchaseLatexMenuItemActionPerformed
        EPLatex item = new EPLatex(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_ePurchaseLatexMenuItemActionPerformed

    private void ePurchaseOthersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ePurchaseOthersMenuItemActionPerformed
        EPOthers item = new EPOthers(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_ePurchaseOthersMenuItemActionPerformed

    private void eSalesLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eSalesLatexMenuItemActionPerformed
        ESLatex item = new ESLatex(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_eSalesLatexMenuItemActionPerformed

    private void eBranchesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eBranchesMenuItemActionPerformed
        EBranches item = new EBranches(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_eBranchesMenuItemActionPerformed

    private void eCustomersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eCustomersMenuItemActionPerformed
        ECustomers item = new ECustomers(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_eCustomersMenuItemActionPerformed

    private void eConsumptionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eConsumptionMenuItemActionPerformed
        EConsumption item = new EConsumption(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_eConsumptionMenuItemActionPerformed

    private void startNewSessionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startNewSessionMenuItemActionPerformed
        addNewSession();
    }//GEN-LAST:event_startNewSessionMenuItemActionPerformed

    private void loadSessionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSessionMenuItemActionPerformed
        loadSession();
    }//GEN-LAST:event_loadSessionMenuItemActionPerformed

    private void addUserMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserMenuItemActionPerformed
        CreateUser item = new CreateUser(this, Codes.CREATE_USER);
        addToMainDesktopPane(item, this.level, Codes.OPTIONS);
    }//GEN-LAST:event_addUserMenuItemActionPerformed

    private void aBranchesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aBranchesMenuItemActionPerformed
        ABranches item = new ABranches(dbConnection, Codes.NEW_ENTRY, null, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_aBranchesMenuItemActionPerformed

    private void pLedgerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pLedgerMenuItemActionPerformed
        PLedger item = new PLedger(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_pLedgerMenuItemActionPerformed

    private void pTrialBalanceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pTrialBalanceMenuItemActionPerformed
        PTrialBalance item = new PTrialBalance(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_pTrialBalanceMenuItemActionPerformed

    private void dayBookMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dayBookMenuItemActionPerformed
        PAccountBooks item = new PAccountBooks(dbConnection, this, this.level+1, "DAY");
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_dayBookMenuItemActionPerformed

    private void cashBookMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashBookMenuItemActionPerformed
        PAccountBooks item = new PAccountBooks(dbConnection, this, this.level+1, "CH");
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_cashBookMenuItemActionPerformed

    private void bankBookMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bankBookMenuItemActionPerformed
        PAccountBooks item = new PAccountBooks(dbConnection, this, this.level+1, "BK");
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_bankBookMenuItemActionPerformed

    private void statementsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementsMenuItemActionPerformed
        PStatements item = new PStatements(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_statementsMenuItemActionPerformed

    private void debtorsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debtorsMenuItemActionPerformed
        PDebtorCreditor item = new PDebtorCreditor(dbConnection, this, this.level+1, "DB");
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_debtorsMenuItemActionPerformed

    private void creditorsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_creditorsMenuItemActionPerformed
        PDebtorCreditor item = new PDebtorCreditor(dbConnection, this, this.level+1, "CR");
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_creditorsMenuItemActionPerformed

    private void pProfitLossBLMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pProfitLossBLMenuItemActionPerformed
        PProfitLossBalanceSheet item = new PProfitLossBalanceSheet(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_pProfitLossBLMenuItemActionPerformed

    private void expensesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expensesMenuItemActionPerformed
        PExpenses item = new PExpenses(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_expensesMenuItemActionPerformed

    private void sessionInfoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sessionInfoMenuItemActionPerformed
        SessionInfo item = new SessionInfo(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_sessionInfoMenuItemActionPerformed

    private void mainDesktopPaneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mainDesktopPaneKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT){
            System.out.println("Right Arrow");
            UtilityFuncs.click(this.fileMenu, 10, 10);
            evt.consume();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT){
            System.out.println("Left Arrow");
            UtilityFuncs.click(this.editMenu, 10, 10);
            evt.consume();
        }
        
    }//GEN-LAST:event_mainDesktopPaneKeyPressed

    private void pPurchaseLatexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pPurchaseLatexMenuItemActionPerformed
        PPurchaseLatex item = new PPurchaseLatex(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_pPurchaseLatexMenuItemActionPerformed

    private void pPartyWiseStatementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pPartyWiseStatementActionPerformed
        PPartyWiseStatement item = new PPartyWiseStatement(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_pPartyWiseStatementActionPerformed

    private void pListOfAccountsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pListOfAccountsMenuItemActionPerformed
        PListOfAccounts item = new PListOfAccounts(dbConnection, this, this.level+1);
        Dimension dim = Preferences.getInternalFrameDimension(item);
        if(dim != null){
            item.setSize(dim);
        }else{
            item.setSize(790, 470);
        }
        addToMainDesktopPane(item, this.level, Codes.DATABASE_DEP);
    }//GEN-LAST:event_pListOfAccountsMenuItemActionPerformed
    
    /**
     * @param item the internal frame to be added to desktop pane
     */
    public void addToMainDesktopPane(javax.swing.JInternalFrame item, int level, int dep){
        if(dep == Codes.DATABASE_DEP && this.dbFound == false){
            JOptionPane.showConfirmDialog(this, "Please Load or Create a Session (Options)", "No Active Session", JOptionPane.WARNING_MESSAGE);
            return;
        }
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
        try{
            item.setSelected(true);
        }catch(Exception se){
            se.printStackTrace();
        }
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
    private javax.swing.JMenuItem aBranchesMenuItem;
    private javax.swing.JMenuItem aConsumptionMenuItem;
    private javax.swing.JMenuItem aMasterMenuItem;
    private javax.swing.JMenuItem aPurchaseLatexMenuItem;
    private javax.swing.JMenuItem aPurchaseOthersMenuItem;
    private javax.swing.JMenuItem aSalesLatexMenuItem;
    private javax.swing.JMenuItem aStockMenuItem;
    private javax.swing.JMenuItem aTransactionMenuItem;
    private javax.swing.JMenu accountBooksMenu;
    private javax.swing.JMenuItem addUserMenuItem;
    private javax.swing.JMenuItem bankBookMenuItem;
    private javax.swing.JMenuItem cashBookMenuItem;
    private javax.swing.JMenuItem creditorsMenuItem;
    private javax.swing.JLabel databaseNameStatus;
    private javax.swing.JMenuItem dayBookMenuItem;
    private javax.swing.JMenuItem debtorsMenuItem;
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
    private javax.swing.JMenuItem expensesMenuItem;
    private javax.swing.JMenuBar fainMainMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem loadSessionMenuItem;
    private javax.swing.JDesktopPane mainDesktopPane;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JMenuItem pLedgerMenuItem;
    private javax.swing.JMenuItem pListOfAccountsMenuItem;
    private javax.swing.JMenuItem pPartyWiseStatement;
    private javax.swing.JMenuItem pProfitLossBLMenuItem;
    private javax.swing.JMenuItem pPurchaseLatexMenuItem;
    private javax.swing.JMenuItem pTrialBalanceMenuItem;
    private javax.swing.JMenu printingMenu;
    private javax.swing.JMenuItem sessionInfoMenuItem;
    private javax.swing.JMenuItem startNewSessionMenuItem;
    private javax.swing.JMenuItem statementsMenuItem;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
