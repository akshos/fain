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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.PurchaseLatexDB;
import database.SalesDB;
import database.SessionInfoDB;
import database.StockDB;
import database.TransactionDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
/**
 *
 * @author akshos
 */
public class CommonFuncs {
    public static final Font nameFont = new Font(Font.FontFamily.HELVETICA, 22);
    public static final Font smallNameFont = new Font(Font.FontFamily.HELVETICA, 13);
    public static final Font addressFont = new Font(Font.FontFamily.HELVETICA, 11);
    
    public static final Font titleFont = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
    public static final Font subTitleFont = new Font(Font.FontFamily.COURIER, 12);
    
    public static final Font accountHeadFont = new Font(Font.FontFamily.COURIER, 12 , Font.BOLD);
    public static final Font branchFont = new Font(Font.FontFamily.COURIER, 12);
    
    public static final Font spacingFont = new Font(Font.FontFamily.COURIER, 12);
    public static final Font tableHeaderFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
    public static final Font tableContentFont = new Font(Font.FontFamily.COURIER, 11);
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
            String name = SessionInfoDB.sessionDetails[1];
            String address = SessionInfoDB.sessionDetails[2];
            String gst = SessionInfoDB.sessionDetails[3];
            String rbreg = SessionInfoDB.sessionDetails[4];
            String phone1 = SessionInfoDB.sessionDetails[5];
            String phone2 = SessionInfoDB.sessionDetails[6];
            
            header.add(alignCenter(name, nameFont));
            header.add(alignCenter(address + "  Phone: " + phone1 + ", " + phone2, addressFont));
            header.add(alignCenter("GST: " + gst + "  RB REG: " + rbreg, addressFont));
            //addEmptyLine(header, 1);
            doc.add(header);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void addHeader(PdfContentByte cb, Document document){
            int base = 60;
            
            LocalDateTime now = LocalDateTime.now();
            Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String date = df.format(currDate);
            
            String name = SessionInfoDB.sessionDetails[1];
            String address = SessionInfoDB.sessionDetails[2];
            String gst = SessionInfoDB.sessionDetails[3];
            String rbreg = SessionInfoDB.sessionDetails[4];
            String phone1 = SessionInfoDB.sessionDetails[5];
            String phone2 = SessionInfoDB.sessionDetails[6];
            
            Phrase cdate = new Phrase(date, addressFont);
            Phrase cname = new Phrase(name, nameFont);
            Phrase caddr = new Phrase(address, addressFont);
            Phrase cphone = new Phrase("Phone: " + phone1 + ", " + phone2, addressFont);
            Phrase cgst = new Phrase("GST: " + gst + "  RB REG: " + rbreg, addressFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    cdate,
                    document.right()-50,
                    document.top() + base + 44, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    cname,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 44, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    caddr,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 30, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    cphone,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 20, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    cgst,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 10, 0);
            
    }
    
    public static void setDocumentSizeOrientation(Document doc, String paper, String orientation){
        try{
            if(paper.compareTo("A4") == 0){
                if(orientation.compareTo("Portrait") == 0){
                    doc.setPageSize(PageSize.A4);
                    System.out.println("A4 Portrait");
                }
                else{
                    doc.setPageSize(PageSize.A4.rotate());
                    System.out.println("A4 Landscape");
                }
            }
            else if(paper.compareTo("Legal") == 0){
                if(orientation.compareTo("Portrait") == 0){
                    doc.setPageSize(PageSize.LEGAL);
                    System.out.println("Legal Portrait");
                }
                else{
                    doc.setPageSize(PageSize.LEGAL.rotate());
                    System.out.println("Legal Landscape");
                }
            }
            doc.setMargins(1, 1, 130, 20);
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
    
    public static LinkedHashMap<String, Account> addToHashMapLinked(String[][] data, boolean opBal){
        LinkedHashMap<String, Account> accountData = new LinkedHashMap();
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
        
        //ret = updateStockAccount(con, date);
        return ret;
    }
}
