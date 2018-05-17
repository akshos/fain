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

/**
 *
 * @author akshos
 */
public class Statements {
    private static String PREFIX = "statements";
    
    private static DBConnection scon;
    private static String sfromDate;
    private static String stoDate;
    private static String saccountId;
    private static String saccountName;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    private static String scat;
    
    public static void addTitle(DBConnection con, Document doc, String fromDate, String toDate, String accountName){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter(accountName, CommonFuncs.titleFont));
            String subTitle = "From : " + fromDate + "  To : " + toDate;
            title.add(CommonFuncs.alignCenter(subTitle, CommonFuncs.subTitleFont));
            doc.add(title);
            CommonFuncs.addEmptyLine(doc, 1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new Statements.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, String paper, String orientation, String fromDate, String toDate, String accountId){
        scon = con;
        sfromDate = fromDate;
        stoDate = toDate;
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 0;
        saccountId = accountId;

        boolean ret = false;

        Document doc;
        try{
            saccountName = MasterDB.getAccountHead(con.getStatement(), accountId);
            
            doc = startDocument(paper, orientation);
            
            double balance = calculatePreviousBalance(con, fromDate, accountId);
            createTable(con, doc, fromDate, toDate, accountId, balance);
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
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
    
    private static void createTable(DBConnection con, Document doc, String fromDate, String toDate, String accountId, double prevBalance){
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
        double closingBalance = 0.0;
        double amount;
        
        String[] transactionDates = TransactionDB.getTrasnsationDatesBetweenIncDatesIdRS(con.getStatement(), fromDate, toDate, accountId);
        if(transactionDates == null){
            JOptionPane.showMessageDialog(null, "No Transactions between the selected dates", "No Transaction", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ResultSet rs;
        String nar;
        
        HashMap<String, String> accountHeads = MasterDB.getAccountHeadHashMap(con.getStatement());
        if(accountHeads == null){
            JOptionPane.showMessageDialog(null, "Failed to get Accounts", "FAILED", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try{
            for (String date : transactionDates){
                
                if(openingBalance != 0.0){
                    addTableRow(table, (PdfPCell.BOTTOM),
                                CommonFuncs.tableContentFont, date, "Opening Balance",
                                new DecimalFormat("##,##,##0.00").format(Math.abs(openingBalance)), "");
                    
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
                        addTableRow(table, (PdfPCell.NO_BORDER), 
                            CommonFuncs.tableContentFont, date, nar,
                            "", new DecimalFormat("##,##,##0.00").format(Math.abs(amount)));
                        
                        creditTotal += amount;
                    }else if(debitAcc.compareTo(accountId) == 0){
                        amount = rs.getDouble("amount");
                        nar = accountHeads.get(rs.getString("credit"));
                        if(!rs.getString("narration").isEmpty())
                            nar += " ( " + rs.getString("narration") + " )";
                        addTableRow(table, (PdfPCell.NO_BORDER), //Start the day
                            CommonFuncs.tableContentFont, date, nar,
                            new DecimalFormat("##,##,##0.00").format(Math.abs(amount)), "");
                        
                        debitTotal += amount;
                    }
                   
                }
                
            }
            //Total debit credit
            addTableRow(table, (PdfPCell.BOTTOM|PdfPCell.TOP), //Start the day
                        CommonFuncs.tableContentFont, "", "TOTALS",
                        new DecimalFormat("##,##,##0.00").format(Math.abs(debitTotal)),
                        new DecimalFormat("##,##,##0.00").format(Math.abs(creditTotal)));
            
            closingBalance = debitTotal - creditTotal;
            if(closingBalance > 0){ //Balance B/F
                    addTableRow(table, (PdfPCell.TOP), //Start the day
                            CommonFuncs.tableContentFont, "", "Closing Balance",
                            new DecimalFormat("##,##,##0.00").format(Math.abs(closingBalance)), "");
                    
                    debitTotal = closingBalance;
                    creditTotal = 0.0;
                }else{
                    addTableRow(table, (PdfPCell.TOP), //Start the day
                            CommonFuncs.tableContentFont, "", "Closing Balance",
                            "", new DecimalFormat("##,##,##0.00").format(Math.abs(closingBalance)));
                    
                    creditTotal = Math.abs(closingBalance);
                    debitTotal = 0.0;
                }
            doc.add(table);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private static class ShowHeader extends PdfPageEventHelper{        
        public void onStartPage(PdfWriter writer, Document docuement){
            CommonFuncs.addHeader(scon, docuement);
            addTitle(scon, docuement, sfromDate, stoDate, saccountName);
        }
        
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            Phrase footer = new Phrase();
            footer.add(new Phrase("Page : " + pageNum + "    ", CommonFuncs.footerFont));
            footer.add(new Phrase("Debit : ", CommonFuncs.footerFont));
            footer.add(new Phrase(String.format("%.2f", pageDebitTotal) + "    ", CommonFuncs.footerFontBold));
            footer.add(new Phrase("Credit : ", CommonFuncs.footerFont));
            footer.add(new Phrase(String.format("%.2f", pageCreditTotal), CommonFuncs.footerFontBold));
            
            pageNum = pageNum + 1;
            pageCreditTotal = 0;
            pageDebitTotal = 0;
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 5, 0);
        }
        
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            PdfTemplate t = writer.getDirectContent().createTemplate(30, 16);
            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(pageNum), CommonFuncs.footerFont),
                (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 5, 0);
        }
    }
}
