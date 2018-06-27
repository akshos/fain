/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
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
import database.SessionInfoDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
    private static String sfromDate;
    private static String stoDate;
    private static String saccountId;
    private static String saccountName;
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
    
    public static int createReport(DBConnection con, String paper, String orientation, String fromDate, String toDate, String branch, String accountId){
        scon = con;
        sfromDate = UtilityFuncs.dateSqlToUser(fromDate);
        stoDate = UtilityFuncs.dateSqlToUser(toDate);
        pageNum = 1;
        sbranch = branch;
        
        int ret = 0;
        double amount = 0.0;
        Document doc;
        try{
            
            String[][] accountData = CustomerDB.getCustomersFilteredCodeBranch(con.getStatement(), branch, accountId);
            if(accountData == null){
                JOptionPane.showMessageDialog(null, "No Customer Accounts Available", "No Customers", JOptionPane.ERROR_MESSAGE);
                return Codes.FAIL;
            }
            
            LinkedHashMap<String, Account> accounts = CommonFuncs.addToHashMapLinked(accountData, false);
            calculateBalance(con, accounts, fromDate, toDate);
            
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
               amount = createTable(con, doc, fromDate, toDate, key, acc);
               if(ret == Codes.FAIL){
                   return ret;
               }
               if(i < len-1){
                   doc.newPage();
               }
            }
            
            addVoucher(con, doc, accountId, branch, new DecimalFormat("##,##,##0.00").format(amount));
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(ret != Codes.FAIL)
            ViewPdf.openPdfViewer(PREFIX + ".pdf");
        
        return ret;
    }
    
    //Calculate net balance from transactions before the start date (fromDate)
    private static boolean calculateBalance(DBConnection con, LinkedHashMap<String, Account> accounts, String fromDate, String toDate){
        String debitAcc, creditAcc;
        double amount;
        Account acc;
        try{
            ResultSet rs = TransactionDB.getTransactionsBeforeDateRS(con.getStatement(), fromDate);
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
    
    private static double createTable(DBConnection con, Document doc, String fromDate, String toDate, String accId, Account acc){
        float columns[] = {0.7f, 0.7f, 0.7f, 0.8f, 0.8f, 0.7f, 1, 1, 1};
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
        
        String[] dates = TransactionDB.getTrasnsationDatesBetweenIncDatesIdRS(con.getStatement(), fromDate, toDate, accId);
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
        return balance;
    }
    
    private static void addVoucherRow(PdfPTable table, int border, Font font, String col1, String col2, String col3){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(col1, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(col2, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
        
        /*
        cell = new PdfPCell(new Phrase(col3, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        */
    }
    
    private static void addVoucherTableHeader(PdfPTable table){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase("Particulars", CommonFuncs.tableBoldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.BOX);
        
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Amount", CommonFuncs.tableBoldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.BOX);
        table.addCell(cell);
        
        /*
        cell = new PdfPCell(new Phrase("Rs.", CommonFuncs.tableBoldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.BOX);
        table.addCell(cell);
                cell.setColspan(2);
        cell = new PdfPCell(new Phrase("Ps.", CommonFuncs.tableBoldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.BOX);
        table.addCell(cell);
    */
        
    }
    
    private static void addVoucher(DBConnection con, Document doc, String accId, String branch, String amount){
            LocalDateTime now = LocalDateTime.now();
            Date currDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String date = df.format(currDate);
            
            String accName = MasterDB.getAccountHead(con.getStatement(), accId);
            
            CommonFuncs.addEmptyLine(doc, 5);
            
            CommonFuncs.addHeader(con, doc);
            
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("VOUCHER", CommonFuncs.titleFont));
            title.add(CommonFuncs.alignCenter(accName + " (" + accId  + ") Br No. " + branch, CommonFuncs.smallNameFont));
            title.add(CommonFuncs.alignCenter("Date : " + date, CommonFuncs.subTitleFont));
            
            float columns[] = {4.5f, 1.5f};
            PdfPTable table = new PdfPTable(columns);
            table.setWidthPercentage(90);
            
            addVoucherTableHeader(table);
            
            addVoucherRow(table, PdfPCell.LEFT|PdfPCell.RIGHT, CommonFuncs.tableContentFont, "Payment : Cash / Cheque No. ", amount, "");
            addVoucherRow(table, PdfPCell.LEFT|PdfPCell.RIGHT, CommonFuncs.tableContentFont, " ", "", "");
            addVoucherRow(table, PdfPCell.LEFT|PdfPCell.RIGHT, CommonFuncs.tableContentFont, " ", "", "");
            addVoucherRow(table, PdfPCell.LEFT|PdfPCell.RIGHT, CommonFuncs.tableContentFont, " ", "", "");
            addVoucherRow(table, PdfPCell.LEFT|PdfPCell.RIGHT, CommonFuncs.tableContentFont, " ", "", "");
            addVoucherRow(table, PdfPCell.LEFT|PdfPCell.RIGHT, CommonFuncs.tableContentFont, " ", "", "");
            
            addVoucherRow(table, PdfPCell.BOX, CommonFuncs.tableBoldFont, "TOTALS", amount, "");
            
            try{
                doc.add(title);
                CommonFuncs.addEmptyLine(doc, 1);
                doc.add(table);
                CommonFuncs.addEmptyLine(doc, 3);
                
                Paragraph sign = new Paragraph("Customer Signature         ", CommonFuncs.titleFont);
                sign.setAlignment(Element.ALIGN_RIGHT);
                doc.add(sign);
                
            }
            catch(Exception e){
                    e.printStackTrace();
            }
            
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
                Phrase date = new Phrase("From : " + sfromDate + "  To : " + stoDate, CommonFuncs.subTitleFont);
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
