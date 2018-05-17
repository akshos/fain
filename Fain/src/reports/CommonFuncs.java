/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.PurchaseLatexDB;
import database.SalesDB;
import database.StockDB;
import database.TransactionDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
/**
 *
 * @author akshos
 */
public class CommonFuncs {
    private static final Font nameFont = new Font(Font.FontFamily.COURIER, 18);
    private static final Font addressFont = new Font(Font.FontFamily.COURIER, 13);
    
    public static final Font titleFont = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
    public static final Font subTitleFont = new Font(Font.FontFamily.COURIER, 12);
    
    public static final Font accountHeadFont = new Font(Font.FontFamily.COURIER, 12 , Font.BOLD);
    public static final Font branchFont = new Font(Font.FontFamily.COURIER, 12);
    
    public static final Font spacingFont = new Font(Font.FontFamily.COURIER, 12);
    public static final Font tableHeaderFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
    public static final Font tableContentFont = new Font(Font.FontFamily.COURIER, 12);
    public static final Font tableBoldFont = new Font(Font.FontFamily.COURIER, 12 , Font.BOLD);
    
    public static final Font footerFont = new Font(Font.FontFamily.COURIER, 12);
    public static final Font footerFontBold = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
    public static Paragraph alignCenter(String str, Font f){
        Paragraph para = new Paragraph(str, f);
        para.setAlignment(Element.ALIGN_CENTER);
        return para;
    }
    
    public static void addHeader(DBConnection dBConnection, Document doc){
        try{
            Paragraph header = new Paragraph();
            header.add(alignCenter("BEENA RUBBERS", nameFont));
            addEmptyLine(header, 1);
            doc.add(header);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void setDocumentSizeOrientation(Document doc, String paper, String orientation){
        try{
            if(paper.compareTo("A4") == 0){
                if(orientation.compareTo("Portrait") == 0){
                    doc.setPageSize(PageSize.A4);
                }
                else{
                    doc.setPageSize(PageSize.A4.rotate());
                }
            }
            else if(paper.compareTo("Legal") == 0){
                if(orientation.compareTo("Portrait") == 0){
                    doc.setPageSize(PageSize.LEGAL);
                }
                else{
                    doc.setPageSize(PageSize.LEGAL.rotate());
                }
            }
            doc.setMargins(1, 1, 1, 15);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void addMetaData(Document doc){
        
    }
    
    public static String generateFileName(String prefix){
        return prefix + ".pdf";
    }
    
    public static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" ", spacingFont));
        }
    }
    
    public static void addEmptyLine(Document doc, int number){
        try{
            Paragraph paragraph = new Paragraph();
            for (int i = 0; i < number; i++) {
                paragraph.add(new Paragraph(" ", spacingFont));
            }
            doc.add(paragraph);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static HashMap<String, Account> addToHashMap(String[][] data, boolean opBal){
        HashMap<String, Account> accountData = new HashMap();
        if(data == null){
            System.out.println("ERROR : addToHashMap : data received is null");
        }
        int len = data[0].length;
        for( int i = 0; i < len; i++ ){
            accountData.put(data[0][i], (opBal)?(new Account(data[1][i], data[2][i])):(new Account(data[1][i])) );
        }
        return accountData;
    }
    
    public static boolean calculateLatexStock(DBConnection con, String date){
        
        double purchaseQuantity = PurchaseLatexDB.getTotalPurchaseQuantity(con.getStatement(), date);
        double saleQuantity = SalesDB.getTotalSaleQuantity(con.getStatement(), date);
        
        double netQuantity = purchaseQuantity - saleQuantity;
        boolean ret = StockDB.setCurrentStockByName(con.getStatement(), netQuantity, "Latex");
        return ret;
    }
    
    public static boolean updateStockAccount(DBConnection con, String date){
        String items[][] = StockDB.getItems(con.getStatement());
        if(items == null){
            JOptionPane.showMessageDialog(null, "No Items in Stock", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        double rate = StockDB.getRateByName(con.getStatement(), "Latex");
        double quantity = StockDB.getCurrentStockByName(con.getStatement(), "Latex");
        
        double value = quantity * rate;
        String stockAcc = StockDB.getStockAccByName(con.getStatement(), "Latex");
        if(stockAcc == null){
            JOptionPane.showMessageDialog(null, "ERROR getting stock account for Latex", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        boolean ret = MasterDB.setClosingBalance(con.getStatement(), stockAcc, String.valueOf(value));
        return ret;
    }
    
    public static boolean updateMaster(DBConnection con, String date){
        String accountData[][] = MasterDB.getAccountHead(con.getStatement());
        HashMap<String, Account> accounts = addToHashMap(accountData, false);
        
        Account debitAcc, creditAcc, acc;
        String accId;
        double amount;
        double openingBal, closingBal;
        boolean ret;
        
        
        try{
            //set opening balance of all accounts as correspoinding debit
            for (Map.Entry<String, Account> entry : accounts.entrySet()){
                acc = entry.getValue();
                accId = entry.getKey();
                openingBal = MasterDB.getOpeningBalance(con.getStatement(), accId);
                acc.setDebit(openingBal);
            }
            
            //get credits and debits of all accounts
            ResultSet rs = TransactionDB.getTransactionsBeforeInclDateRS(con.getStatement(), date);
            if(rs == null){
                JOptionPane.showMessageDialog(null, "ERROR updating Accounts", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            //add all credit and debit amounts to corresponding accounts
            while(rs.next()){
                creditAcc = accounts.get(rs.getString("credit"));
                debitAcc = accounts.get(rs.getString("debit"));
                
                amount = rs.getDouble("amount");
                creditAcc.addCredit(amount);
                debitAcc.addDebit(amount);
            }
            
            //calculate closing balance of each account and update master
            //closing balalce = opening balance + total debit - total credit
            for (Map.Entry<String, Account> entry : accounts.entrySet()){
                acc = entry.getValue();
                accId = entry.getKey();
                closingBal = acc.getDebit() - acc.getCredit();
                ret = MasterDB.setClosingBalance(con.getStatement(), accId, String.valueOf(closingBal));
                if(ret == false){
                    JOptionPane.showMessageDialog(null, "ERROR updating Closing Balance", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean updateAllAccounts(DBConnection con, String date){
        boolean ret;
        
        ret = calculateLatexStock(con, date);
        if(!ret) return ret;
        
        ret = updateMaster(con, date);
        if(!ret) return ret;
        
        ret = updateStockAccount(con, date);
        return ret;
    }
}
