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
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import database.DBConnection;
import database.MasterDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.swing.JOptionPane;
import utility.UtilityFuncs;
import utility.Wait;

/**
 *
 * @author akshos
 */
public class CashBankAccount {
    private static String PREFIX = "daybook";
    
    private static DBConnection scon;
    private static String sfromDate;
    private static String stoDate;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    private static String prtDate;
    private static String sdate;
    private static String scat;
    private static String saccId;
    private static String saccName;
    
       
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new CashBankAccount.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, String paper, String orientation, String fromDate, String toDate, String accountId, String category){
        scon = con;
        sfromDate = UtilityFuncs.dateSqlToUser(fromDate);
        stoDate = UtilityFuncs.dateSqlToUser(toDate);
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 1;
        scat = category;
        sdate = prtDate = "";
        saccId = saccName = "";
        
        boolean ret = false;
        
        if(category.compareTo("CH") == 0){
            PREFIX = "cashbook";
        }else if(category.compareTo("BK") == 0){
            PREFIX = "bankbook";
        }
        
        Document doc;
        
        try{
            if(accountId == null){
                JOptionPane.showMessageDialog(null, "No CASH Account available", "No ACCOUNT", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            saccId = accountId;
            saccName = MasterDB.getAccountHead(con.getStatement(), accountId);
            Thread.sleep(500);
            doc = startDocument(paper, orientation);
                        
            //calculate the opening balance till fromDate
            double balance = calculatePreviousBalance(con, fromDate, accountId);
            //generate report from fromDate to toDate with the previous opening balance
            ret = createTable(con, doc, fromDate, toDate, accountId, balance);
            if(ret)
                doc.close();
            Thread.sleep(500);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(ret)
            ViewPdf.openPdfViewer(PREFIX + ".pdf");
        return ret;
    }
    
    private static double calculatePreviousBalance(DBConnection con, String fromDate, String cashAccountId){
        double debit, credit;
        debit = credit = 0.0;
        ResultSet rs = TransactionDB.getTransactionsBeforeDateRS(con.getStatement(), fromDate);
        try{
            while(rs.next()){
                if(rs.getString("debit").compareTo(cashAccountId) == 0){
                    debit += rs.getDouble("amount");
                }else if(rs.getString("credit").compareTo(cashAccountId) == 0){
                    credit += rs.getDouble("amount");
                }
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        double balance = debit-credit;
        return balance;
    }
    
     private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
     
    private static void addTableRow(PdfPTable table, int border, Font font, String date, String nar, String debit, String credit){
        PdfPCell cell;
        
        date = UtilityFuncs.dateSqlToUser(date);
        
        cell = new PdfPCell(new Phrase(date, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(nar, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(debit, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(credit, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
    }
    
    private static boolean createTable(DBConnection con, Document doc, String fromDate, String toDate, String accountId, double prevBalance) throws Exception{
        float columns[] = {0.7f, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Account");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        double openingBalance = Double.parseDouble(MasterDB.getOpeningBal(con.getStatement(), accountId));
        double debitTotal = prevBalance + openingBalance;
        openingBalance = debitTotal;
        double creditTotal = 0.0;
        double credit, debit;
        double dailyBal = 0.0;
        double amount;
        
        String[] transactionDates = TransactionDB.getTrasnsationDatesBetweenIncDatesIdRS(con.getStatement(), fromDate, toDate, accountId);
        if(transactionDates == null){
            JOptionPane.showMessageDialog(null, "No Transactions between the selected dates", "No Transaction", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        ResultSet rs;
        String sql;
        String nar;
        
        HashMap<String, String> accountHeads = MasterDB.getAccountHeadHashMap(con.getStatement());
        if(accountHeads == null){
            JOptionPane.showMessageDialog(null, "Failed to get Accounts", "FAILED", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try{
            for (String date : transactionDates){
                
                prtDate = date;
                sdate = date;
                
                if(openingBalance != 0.0){
                    addTableRow(table, (PdfPCell.BOTTOM),
                                CommonFuncs.tableContentFont, prtDate, "Opening Balance",
                                new DecimalFormat("##,##,##0.00").format(Math.abs(openingBalance)), "");
                    
                    prtDate = "";
                    openingBalance = 0.0;
                }               
                
                
                rs = TransactionDB.getTransactionsOnDateForId(con.getStatement(), date, accountId);
                while(rs.next()){
                    String debitAcc = rs.getString("debit");
                    String creditAcc = rs.getString("credit");
                    if(creditAcc.compareTo(accountId) == 0){
                        amount = rs.getDouble("amount");
                        nar = accountHeads.get(rs.getString("debit"));
                        if(!rs.getString("narration").isEmpty())
                            nar += " ( " + rs.getString("narration") + " )";
                        addTableRow(table, (PdfPCell.NO_BORDER), //Start the day
                            CommonFuncs.tableContentFont, prtDate, nar,
                            "", new DecimalFormat("##,##,##0.00").format(Math.abs(amount)));
                        
                        creditTotal += amount;
                    }else if(debitAcc.compareTo(accountId) == 0){
                        amount = rs.getDouble("amount");
                        nar = accountHeads.get(rs.getString("credit"));
                        if(!rs.getString("narration").isEmpty())
                            nar += " ( " + rs.getString("narration") + " )";
                        addTableRow(table, (PdfPCell.NO_BORDER), //Start the day
                            CommonFuncs.tableContentFont, prtDate, nar,
                            new DecimalFormat("##,##,##0.00").format(Math.abs(amount)), "");
                        
                        debitTotal += amount;
                    }
                    
                    prtDate = "";
                }
                
                dailyBal = debitTotal - creditTotal;
                if(dailyBal > 0){ //Balance C/F
                    addTableRow(table, (PdfPCell.NO_BORDER), //Start the day
                            CommonFuncs.tableContentFont, "", "Balance C/F",
                            "", new DecimalFormat("##,##,##0.00").format(Math.abs(dailyBal)));
                    
                    creditTotal += dailyBal;
                }else{
                    addTableRow(table, (PdfPCell.NO_BORDER), //Start the day
                            CommonFuncs.tableContentFont, "", "Balance C/F",
                            new DecimalFormat("##,##,##0.00").format(Math.abs(dailyBal)), "");
                    
                    debitTotal += Math.abs(dailyBal);
                }
                //Total debit credit
                addTableRow(table, (PdfPCell.BOTTOM|PdfPCell.TOP), //Start the day
                            CommonFuncs.tableContentFont, "", "TOTALS",
                            new DecimalFormat("##,##,##0.00").format(Math.abs(debitTotal)),
                            new DecimalFormat("##,##,##0.00").format(Math.abs(creditTotal)));
                
                if(dailyBal > 0){ //Balance B/F
                    addTableRow(table, (PdfPCell.TOP), //Start the day
                            CommonFuncs.tableContentFont, "", "Balance B/F",
                            new DecimalFormat("##,##,##0.00").format(Math.abs(dailyBal)), "");
                    
                    debitTotal = dailyBal;
                    creditTotal = 0.0;
                }else{
                    addTableRow(table, (PdfPCell.TOP), //Start the day
                            CommonFuncs.tableContentFont, "", "Balance B/F",
                            "", new DecimalFormat("##,##,##0.00").format(Math.abs(dailyBal)));
                    
                    creditTotal = Math.abs(dailyBal);
                    debitTotal = 0.0;
                }
            }
            
            doc.add(table);
            
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
        
    }
    
    private static class ShowHeader extends PdfPageEventHelper{        

        
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            CommonFuncs.addHeader(cb, document);
            addTitle(cb, document);
            
            Phrase footer = new Phrase();
            footer.add(new Phrase("Page : " + pageNum + "    ", CommonFuncs.footerFont));
            /*
            footer.add(new Phrase("Debit : ", CommonFuncs.footerFont));
            footer.add(new Phrase(String.format("%.2f", pageDebitTotal) + "    ", CommonFuncs.footerFontBold));
            footer.add(new Phrase("Credit : ", CommonFuncs.footerFont));
            footer.add(new Phrase(String.format("%.2f", pageCreditTotal), CommonFuncs.footerFontBold));
            */
            pageNum = pageNum + 1;
            pageCreditTotal = 0;
            pageDebitTotal = 0;
            prtDate = sdate;
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 5, 0);
        }
        
       public void addTitle(PdfContentByte cb, Document document){
            try{
                int base = 10;
                
                String pre = "";
                if(scat.compareTo("CH") == 0){
                    pre = "CASH";
                }else if(scat.compareTo("BK") == 0){
                    pre = "BANK";
                }
                pre += " BOOK";

                Phrase title = new Phrase(pre, CommonFuncs.titleFont);
                Phrase account = new Phrase(saccName + " ("+ saccId + ")", CommonFuncs.subTitleFont);
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
