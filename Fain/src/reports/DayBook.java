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
import com.itextpdf.text.pdf.PdfContentByte;;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import database.BranchDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.JOptionPane;
import static reports.TrialBalance.addTitle;

/**
 *
 * @author akshos
 */
public class DayBook {
    private static String PREFIX = "daybook";
    
    private static DBConnection scon;
    private static String sfromDate;
    private static String stoDate;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    
    public static void addTitle(DBConnection con, Document doc, String fromDate, String toDate){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("DAY BOOK", CommonFuncs.titleFont));
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
            writer.setPageEvent(new DayBook.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, String paper, String orientation, String fromDate, String toDate, String cashAccountId){
        scon = con;
        sfromDate = fromDate;
        stoDate = toDate;
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 0;
        boolean ret = false;
        
        Document doc;
        try{
            doc = startDocument(paper, orientation);
            
            if(cashAccountId == null){
                JOptionPane.showMessageDialog(null, "No CASH Account available", "No ACCOUNT", JOptionPane.WARNING_MESSAGE);
                doc.close();
                return false;
            }
            
            double balance = calculatePreviousBalance(con, fromDate, cashAccountId);
            createTable(con, doc, fromDate, toDate, cashAccountId, balance);
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
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
    
    private static void endDay(double debit, double credit, double balance){
    }
    
    private static void createTable(DBConnection con, Document doc, String fromDate, String toDate, String cashAccountId, double prevBalance){
        float columns[] = {0.7f, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Narration");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        double debitTotal = prevBalance;
        double creditTotal = 0.0;
        double credit, debit;
        double dailyBal = 0.0;
        String currDate = "";
        
        String[] transactionDates = TransactionDB.getTrasnsationDatesBetweenIncDatesIdRS(con.getStatement(), fromDate, toDate, cashAccountId);
        if(transactionDates == null){
            JOptionPane.showMessageDialog(null, "No Transactions between the selected dates", "No Transaction", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ResultSet rs;
        String sql;
        String nar;
        String prtDate = "";
        try{
            for (String date : transactionDates){
                prtDate = date;
                if(prevBalance != 0.0){
                    addTableRow(table, (PdfPCell.BOTTOM),
                                CommonFuncs.tableContentFont, prtDate, "Opening Balance",
                                new DecimalFormat("##,##,##0.00").format(Math.abs(prevBalance)), "");
                    
                    prtDate = "";
                    prevBalance = 0.0;
                }               
                
                //sql = "select date, narration, debit, sum(amount) as amount from transactions where date='"+date+"' and credit='"+cashAccountId+"' group by debit;";
                //rs = con.getStatement().executeQuery(sql);
                rs = TransactionDB.getNetCreditsOnDateForId(con.getStatement(), date, cashAccountId);
                while(rs.next()){
                    credit = rs.getDouble("amount");
                    nar = rs.getString("accountHead");
                    addTableRow(table, (PdfPCell.NO_BORDER), //Start the day
                            CommonFuncs.tableContentFont, prtDate, nar,
                            "", new DecimalFormat("##,##,##0.00").format(Math.abs(credit)));
                    
                    creditTotal += credit;
                     prtDate = "";
                }
                
                //sql = "select date, narration, credit, sum(amount) as amount from transactions where date='"+date+"' and debit='"+cashAccountId+"' group by credit;";
                //rs = con.getStatement().executeQuery(sql);
                rs = TransactionDB.getNetDebitsOnDateForId(con.getStatement(), date, cashAccountId);
                while(rs.next()){
                    debit = rs.getDouble("amount");
                    nar = rs.getString("accountHead");
                    addTableRow(table, (PdfPCell.NO_BORDER), //Start the day
                            CommonFuncs.tableContentFont, prtDate, nar,
                            new DecimalFormat("##,##,##0.00").format(Math.abs(debit)), "");
                    
                    debitTotal += debit;
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
        }
        
    }
    
    private static class ShowHeader extends PdfPageEventHelper{        
        public void onStartPage(PdfWriter writer, Document docuement){
            CommonFuncs.addHeader(scon, docuement);
            addTitle(scon, docuement, sfromDate, stoDate);
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
