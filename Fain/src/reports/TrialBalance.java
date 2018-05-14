/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;
import database.DBConnection;

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
import database.MasterDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author akshos
 */
public class TrialBalance {
    private static String PREFIX = "trialbalance";
    
    private static String currAcc;
    private static DBConnection scon;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    private static String sdate;
    
    public static void addTitle(DBConnection con, Document doc, String date){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("TRIAL BALANCE", CommonFuncs.titleFont));
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
            writer.setPageEvent(new TrialBalance.ShowHeader());
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
        try{
            doc = startDocument(paper, orientation);
            
            HashMap<String, Account> accountData = generateData(con, date);
            if(accountData == null){
                return false;
            }
            ret = addTable(doc, accountData);
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return ret;
    }
    
    public static HashMap<String, Account> generateData(DBConnection con, String date){
        HashMap<String, Account> accountData;
        
        String[][] accountStrs = MasterDB.getIdHeadOpBal(con.getStatement());
        accountData = CommonFuncs.addToHashMap(accountStrs);
        if(accountData.isEmpty()){
            return null;
        }
        
        ResultSet rs = TransactionDB.getTransactionsBeforeDateRS(con.getStatement(), date);
        String debitAcc, creditAcc;
        Account acc;
        double amount;
        try{
            if(rs.next()){
                do{
                    debitAcc = rs.getString("debit");
                    creditAcc = rs.getString("credit");
                    amount = rs.getDouble("amount");
                    acc = accountData.get(debitAcc);
                    acc.addDebit(amount);
                    acc = accountData.get(creditAcc);
                    acc.addCredit(amount);
                }while(rs.next());
            }else{
                return null;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return null;
        }
        return accountData;
    }
    
     private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addTableRow(PdfPTable table, int border, Font font, String accName, String debit, String credit){
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
    
    private static boolean addTable(Document doc, HashMap<String, Account> accountData){
        float columns[] = {2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Account");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        double creditTotal, debitTotal;
        double closingBalance;
        creditTotal = debitTotal = 0.0;
        Account acc;
        try{
            for (Map.Entry<String, Account> entry : accountData.entrySet()){
                acc = entry.getValue();
                closingBalance = acc.calculateClosingBal();
                if(closingBalance > 0){
                    addTableRow(table, (PdfPCell.NO_BORDER),
                            CommonFuncs.tableContentFont, acc.getAccountHead(), 
                            "", new DecimalFormat("##,##,##0.00").format(closingBalance));

                    creditTotal += closingBalance;
                    pageCreditTotal += closingBalance;
                }else{
                    addTableRow(table, (PdfPCell.NO_BORDER),
                            CommonFuncs.tableContentFont, acc.getAccountHead(), 
                            new DecimalFormat("##,##,##0.00").format(Math.abs(closingBalance)), "");

                    debitTotal += Math.abs(closingBalance);
                    pageDebitTotal += Math.abs(closingBalance);
                }
            }

            double netBalance = creditTotal - debitTotal;
            addTableRow(table, (PdfPCell.RECTANGLE|PdfPCell.TOP), 
                        CommonFuncs.tableBoldFont, "TOTALS : ", 
                        new DecimalFormat("##,##,##0.00").format(debitTotal), 
                        new DecimalFormat("##,##,##0.00").format(creditTotal));

            if(netBalance > 0){
                    addTableRow(table, (PdfPCell.RECTANGLE|PdfPCell.TOP), 
                            CommonFuncs.tableBoldFont, "Net Balance", 
                            "", new DecimalFormat("##,##,##0.00").format(netBalance));

            }else{
                addTableRow(table, (PdfPCell.RECTANGLE),
                        CommonFuncs.tableBoldFont, "Net Balance", 
                        new DecimalFormat("##,##,##0.00").format(Math.abs(netBalance)), "");

            }
            
            doc.add(table);
            
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        ViewPdf.openPdfViewer(PREFIX + ".pdf");
        return true;
    }
    
    private static class ShowHeader extends PdfPageEventHelper{        
        public void onStartPage(PdfWriter writer, Document docuement){
            CommonFuncs.addHeader(scon, docuement);
            addTitle(scon, docuement, sdate);
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
