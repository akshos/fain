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
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author akshos
 */
public class ProfitLossBalanceSheet {
    private static String PREFIX = "plbalancesheet";
    
    private static String currAcc;
    private static DBConnection scon;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    private static String sdate;
    private static String stitle;
    
    public static void addTitle(DBConnection con, Document doc, String date, String titleStr){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter(titleStr, CommonFuncs.titleFont));
            String subTitle = "As on : " + date;
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
            writer.setPageEvent(new ProfitLossBalanceSheet.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, String paper, String orientation, String date){
        scon = con;
        currAcc = "";
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 0;
        sdate = date;
        boolean ret = false;
        
        Document doc;
        
        //update closing balance of all accounts
        ret = CommonFuncs.updateAllAccounts(con, date);
        if(!ret){
            JOptionPane.showMessageDialog(null, "Failed to Update Accounts", "FAILED", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try{
            doc = startDocument(paper, orientation);
            stitle = "TRADING ACCOUNT";
            double grossIncome = tradingAccount(con, doc);
            doc.newPage();
            
            stitle = "PROFIT AND LOSS ACCOUNT";
            profitAndLoss(con, doc, grossIncome);
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        ViewPdf.openPdfViewer(PREFIX + ".pdf");
        return ret;
    }
    
    private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addTableRow(PdfPTable table, int border, Font font, String code, String accName, String debit, String credit){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(accName, CommonFuncs.tableContentFont));
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
    
    public static void addCreditDebit(PdfPTable table, int border, String accId, String accName, double amount){
        if(amount > 0){
            addTableRow(table, border,
                        CommonFuncs.tableContentFont, accId, accName,
                        new DecimalFormat("##,##,##0.00").format(amount), "");
        }else if(amount < 0){
            addTableRow(table, border,
                        CommonFuncs.tableContentFont, accId, accName,
                        "", new DecimalFormat("##,##,##0.00").format(Math.abs(amount)));
        }
    }
    
    private static double tradingAccount(DBConnection con, Document doc){
        
        float columns[] = {1, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Code");
        addHeaderCell(table, "Account Head");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        double purchaseTotal = 0.0, saleTotal = 0.0;
        double openingBal, currentBal;
        ResultSet rs;
        
        try{
            //Opening stock
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SK");
            while(rs.next()){
                openingBal = rs.getDouble("openingBal");
                if(openingBal > 0){
                    addTableRow(table, (PdfPCell.NO_BORDER),
                                CommonFuncs.tableContentFont, rs.getString("accountNo"), "Op.Stk " + rs.getString("accountHead"),
                                new DecimalFormat("##,##,##0.00").format(openingBal), "");
                }
                
                purchaseTotal += openingBal;
            }
            //Purchase
            rs = MasterDB.getAccountsByCat(con.getStatement(), "PR");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                purchaseTotal += currentBal;
            }
            //Sales
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SL");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                saleTotal += currentBal;
            }
            //Closing stock
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SK");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                addTableRow(table, (PdfPCell.NO_BORDER),
                                CommonFuncs.tableContentFont, rs.getString("accountNo"), "Cl.Stk " + rs.getString("accountHead"),
                                "", new DecimalFormat("##,##,##0.00").format(currentBal));
                
                saleTotal += (-1 * currentBal);
            }
            //Selling Expenses
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SE");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                purchaseTotal += currentBal;
            }
            
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                        CommonFuncs.tableContentFont, "", "",
                        new DecimalFormat("##,##,##0.00").format(purchaseTotal), 
                        new DecimalFormat("##,##,##0.00").format(Math.abs(saleTotal)));
            
            //print GROSS INCOME
            double balance = purchaseTotal + saleTotal;
            addCreditDebit(table, (PdfPCell.TOP|PdfPCell.BOTTOM), "", "GROSS INCODE", balance);
            
            doc.add(table);
            
            return balance;
            
        }catch(Exception se){
            se.printStackTrace();
            return 0.0;
        }
    }
    
    
    private static double profitAndLoss(DBConnection con, Document doc, double grossIncome){
        float columns[] = {1, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Code");
        addHeaderCell(table, "Account Head");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        addCreditDebit(table, (PdfPCell.NO_BORDER), "", "GROSS INCOME", grossIncome);
        
        double currentBal;
        double incomeTotal = 0.0;
        double expenseTotal = 0.0;
        
        ResultSet rs;
        
        try{
            //Income accounts
            rs = MasterDB.getAccountsByCat(con.getStatement(), "IN");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                incomeTotal += currentBal;
            }
            
            //Expence accounts
            rs = MasterDB.getAccountsByCat(con.getStatement(), "EX");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                expenseTotal += currentBal;
            }
            
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                        CommonFuncs.tableContentFont, "", "TOTALS",
                        new DecimalFormat("##,##,##0.00").format(expenseTotal), 
                        new DecimalFormat("##,##,##0.00").format(Math.abs(incomeTotal)));
            
            double balance = incomeTotal + expenseTotal;
            if(balance < 0){
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                        CommonFuncs.tableContentFont, "", "NET PROFIT",
                        "", new DecimalFormat("##,##,##0.00").format(balance));
            }else{
                addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                            CommonFuncs.tableContentFont, "", "NET LOSS",
                            new DecimalFormat("##,##,##0.00").format(Math.abs(balance)), "");
        }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    
    private static class ShowHeader extends PdfPageEventHelper{        
        public void onStartPage(PdfWriter writer, Document docuement){
            CommonFuncs.addHeader(scon, docuement);
            addTitle(scon, docuement, sdate, stitle);
        }
        
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            Phrase footer = new Phrase();
            footer.add(new Phrase("Page : " + pageNum + "    ", CommonFuncs.footerFont));
            
            /*
            footer.add(new Phrase("Debit : ", CommonFuncs.footerFont));
            footer.add(new Phrase(String.format("%.2f", pageDebitTotal) + "    ", CommonFuncs.footerFontBold));
            footer.add(new Phrase("Credit : ", CommonFuncs.footerFont));
            footer.add(new Phrase(String.format("%.2f", pageCreditTotal), CommonFuncs.footerFontBold));
            pageCreditTotal = 0;
            pageDebitTotal = 0;
            */
            
            pageNum = pageNum + 1;
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
