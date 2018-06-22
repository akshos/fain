/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.PurchaseLatexDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import utility.Codes;
import utility.UtilityFuncs;

/**
 *
 * @author akshos
 */
public class PartyWiseStatementVoucher {
    private static final String PREFIX = "partywisestatement";
    
    private static DBConnection scon;
    private static String sdate;
    private static String saccountId;
    private static String saccountName;
    private static String sbillNo;
    private static String sbranch;
    private static int pageNum;
    
    
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new PartyWiseStatementVoucher.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static int createReport(DBConnection con, String paper, String orientation, String date, String billNo){
        scon = con;
        sdate = UtilityFuncs.dateSqlToUser(date);
        pageNum = 1;
        sbillNo = billNo;
        int ret = 0;

        Document doc;
        try{
            String accountId = PurchaseLatexDB.getAccIdFromBillNo(con.getStatement(), billNo);
            String[][] accountData = CustomerDB.getCustomersFilteredCode(con.getStatement(), accountId);
            if(accountData == null){
                JOptionPane.showMessageDialog(null, "No Customer Accounts Available", "No Customers", JOptionPane.ERROR_MESSAGE);
                return Codes.FAIL;
            }
            
            LinkedHashMap<String, Account> accounts = CommonFuncs.addToHashMapLinked(accountData, false);
            calculateBalance(con, accounts, date);
            
            List<String> keyList = new ArrayList<String>(accounts.keySet());
            int len = keyList.size();
                        
            doc = startDocument(paper, orientation);
            
            String key;
            Account acc;
            for(int i = 0; i < len; i++){
               key = keyList.get(i);
               saccountId = key;
               saccountName = MasterDB.getAccountHead(con.getStatement(), saccountId);
               acc = accounts.get(key);               
               ret = createTable(con, doc, date, key, acc);
               if(ret == Codes.FAIL){
                   return ret;
               }
               if(i < len-1){
                   doc.newPage();
               }
            }
            
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(ret != Codes.FAIL)
            ViewPdf.openPdfViewer(PREFIX + ".pdf");
        
        return ret;
    }
    
    //Calculate net balance from transactions before the start date (fromDate)
    private static boolean calculateBalance(DBConnection con, LinkedHashMap<String, Account> accounts, String date){
        String debitAcc, creditAcc;
        double amount;
        Account acc;
        try{
            ResultSet rs = TransactionDB.getTransactionsBeforeDateRS(con.getStatement(), date);
            while(rs.next()){
                debitAcc = rs.getString("debit");
                creditAcc = rs.getString("credit");
                amount = rs.getDouble("amount");
                
                acc = accounts.get(creditAcc);
                if(acc != null){
                    acc.balance += amount;
                }
                acc = null;
                acc = accounts.get(debitAcc);
                if(acc != null){
                    acc.balance -= amount;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    
     private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
     
    private static void addTableRow(PdfPTable table, int border, Font font, String date, String bill, String qnty, String drc, String dryWt, String rate, String value, String advance, String balance){
        PdfPCell cell;
        
        date = UtilityFuncs.dateSqlToUser(date);
        
        cell = new PdfPCell(new Phrase(date, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(bill, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(qnty, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(drc, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(dryWt, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(rate, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(advance, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(balance, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
    }
    
    private static int createTable(DBConnection con, Document doc, String billDate, String accId, Account acc){
        float columns[] = {1, 0.7f, 2, 1, 1, 1, 1, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Bill");
        addHeaderCell(table, "Qnty");
        addHeaderCell(table, "DRC");
        addHeaderCell(table, "Dry Wt.");
        addHeaderCell(table, "Rate");
        addHeaderCell(table, "Value");
        addHeaderCell(table, "Advance");
        addHeaderCell(table, "Balance");
        table.setHeaderRows(1);
        
        int count = 0;
        double totalQuantity = 0.0;
        double totalDry = 0.0;
        double totalRate = 0.0;
        double totalValue = 0.0;
        boolean purchased = false;
        boolean advanced = false;
        
        double balance = acc.balance;
        //Add the opening balance of the account to the balance calculated from previous transactions
        balance -= MasterDB.getOpeningBalance(con.getStatement(), accId);
        
        double advance = 0.0;
        
        double amount;
        String partyCode, partyName, bill;
        double qnty, drc, dryWt, rate, value;
        
        addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT),
                                CommonFuncs.tableContentFont, "",
                                "", "", "", "", "", "", "",
                                new DecimalFormat("##,##,##0.00").format(balance));
        
        String[] dates = TransactionDB.getTrasnsationDatesBetweenIncDatesIdRS(con.getStatement(), billDate, billDate, accId);
        if(dates == null){
            return Codes.NOT_EXISTS;
        }
        try{
            for(String date : dates){
                purchased = false;
                ResultSet rs = PurchaseLatexDB.getPurchasesOnDateForParty(con.getStatement(), date, accId);
                while(rs.next()){
                    bill = rs.getString("prBill");
                    qnty = rs.getDouble("quantity");
                    drc = rs.getDouble("drc");
                    dryWt = (qnty*drc/100);
                    rate = rs.getDouble("rate");
                    value = (dryWt * rate);
                    
                    addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT),
                                CommonFuncs.tableContentFont, date, bill, 
                                new DecimalFormat("##,##,##0.000").format(qnty), 
                                new DecimalFormat("##,##,##0.000").format(drc), 
                                new DecimalFormat("##,##,##0.000").format(dryWt),
                                new DecimalFormat("##,##,##0.00").format(rate), 
                                new DecimalFormat("##,##,##0.00").format(value), 
                                "", "");
                    
                    purchased = true;
                    count++;
                    totalQuantity += qnty;
                    totalDry += dryWt;
                    totalRate += rate;
                    totalValue += value;
                    balance += value;
                }
                advanced = false;
                rs = TransactionDB.getTransactionsOnDateForDebitId(con.getStatement(), date, accId);
                while(rs.next()){
                    amount = rs.getDouble("amount");
                    balance -= amount;
                    advance += amount;
                    advanced = true;
                    addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT),
                                CommonFuncs.tableContentFont, (!purchased)?date:"",
                                "", "", "", "", "", "",
                                new DecimalFormat("##,##,##0.00").format(amount), "");
                    
                }
                
                if(advanced || purchased){
                    addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT),
                                CommonFuncs.tableContentFont, "",
                                "", "", "", "", "", "", "",
                                new DecimalFormat("##,##,##0.00").format(balance) );
                }
                
            }
            
            double avgDrc = (totalQuantity!=0.0)?(totalDry/totalQuantity)*100:0.0;
            double avgRate = (count!=0)?(totalRate / count):0.0;
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM|PdfPCell.LEFT|PdfPCell.RIGHT),
                                CommonFuncs.tableBoldFont, "", "TOTALS", 
                                new DecimalFormat("##,##,##0.000").format(totalQuantity), 
                                new DecimalFormat("##,##,##0.000").format(avgDrc), 
                                new DecimalFormat("##,##,##0.000").format(totalDry),
                                new DecimalFormat("##,##,##0.00").format(avgRate), 
                                new DecimalFormat("##,##,##0.00").format(totalValue), 
                                new DecimalFormat("##,##,##0.00").format(advance), 
                                new DecimalFormat("##,##,##0.00").format(balance));
            
            
        }catch(Exception e){
            e.printStackTrace();
            return Codes.FAIL;
        }
        try{
            doc.add(table);
        }catch(Exception e){
            e.printStackTrace();
            return Codes.FAIL;
        }
        return Codes.SUCCESS;
    }
    
    private static class ShowHeader extends PdfPageEventHelper{        
        
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            CommonFuncs.addHeader(cb, document);
            addTitle(cb, document);
            
            Phrase footer = new Phrase();
            footer.add(new Phrase("Page : " + pageNum + "    ", CommonFuncs.footerFont));
            
            pageNum = pageNum + 1;
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 5, 0);
        }
        
        private void addTitle(PdfContentByte cb, Document document){
            try{
               int base = 10;

                Phrase title = new Phrase("PARTY WISE STATEMENT", CommonFuncs.titleFont);
                Phrase account = new Phrase(saccountName + " ("+ saccountId + ")", CommonFuncs.subTitleFont);
                Phrase date = new Phrase("Date : " + sdate, CommonFuncs.subTitleFont);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    title,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 35, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    account,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 20, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    date,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 10, 0);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
