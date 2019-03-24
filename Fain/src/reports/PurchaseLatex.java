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
import database.DBConnection;
import database.MasterDB;
import database.PurchaseLatexDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.swing.JOptionPane;
import utility.UtilityFuncs;

/**
 *
 * @author akshos
 */
public class PurchaseLatex {
    private static final String PREFIX = "purchaselatex";
    
    private static DBConnection scon;
    private static String sfromDate;
    private static String stoDate;
    private static String saccountId;
    private static String saccountName;
    private static int pageNum;
    
    
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new PurchaseLatex.ShowHeader());
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
        sfromDate = UtilityFuncs.dateSqlToUser(fromDate);
        stoDate = UtilityFuncs.dateSqlToUser(toDate);
        pageNum = 1;
        saccountId = accountId;

        boolean ret = false;

        Document doc;
        try{
            if(accountId.compareTo("All") == 0){
                saccountName = "All";
            }else{
                saccountName = MasterDB.getAccountHead(con.getStatement(), accountId);
            }
            
            doc = startDocument(paper, orientation);
            Thread.sleep(500);
            ret = createTable(con, doc, fromDate, toDate, accountId);
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
    
     private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
     
    private static void addTableRow(PdfPTable table, int border, Font font, String date, String bill, String party, String qnty, String drc, String dryWt, String rate, String value){
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
        
        cell = new PdfPCell(new Phrase(party, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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
    }
    
    private static boolean createTable(DBConnection con, Document doc, String fromDate, String toDate, String accountId){
        float columns[] = {1, 0.7f, 2, 1, 1, 1, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Bill");
        addHeaderCell(table, "Party");
        addHeaderCell(table, "Qnty");
        addHeaderCell(table, "DRC");
        addHeaderCell(table, "Dry Wt.");
        addHeaderCell(table, "Rate");
        addHeaderCell(table, "Value");
        table.setHeaderRows(1);
        
        int count = 0;
        double totalQuantity = 0.0;
        double totalDry = 0.0;
        double totalRate = 0.0;
        double totalValue = 0.0;
        
        String date, partyCode, partyName, bill;
        double qnty, drc, dryWt, rate, value;
        
        try{
            ResultSet rs = PurchaseLatexDB.getAllPurchasesPartyDateRange(con.getStatement(), accountId, fromDate, toDate);
            if(rs == null){
                JOptionPane.showMessageDialog(null, "No Purchases Available", "No Purchases", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            while(rs.next()){
                date = rs.getString("date");
                bill = rs.getString("prBill");
                partyCode = rs.getString("party");
                partyName = rs.getString("accountHead");
                qnty = rs.getDouble("quantity");
                drc = rs.getDouble("drc");
                dryWt = (qnty*drc/100);
                rate = rs.getDouble("rate");
                value = dryWt*rate;
                
                addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT),
                                CommonFuncs.tableContentFont, date, bill, partyCode + " : "+partyName,
                                new DecimalFormat("##,##,##0.000").format(qnty), 
                                new DecimalFormat("##,##,##0.000").format(drc), 
                                new DecimalFormat("##,##,##0.000").format(dryWt),
                                new DecimalFormat("##,##,##0.00").format(rate), 
                                new DecimalFormat("##,##,##0.00").format(value));
                
                
                totalQuantity += qnty;
                totalDry += dryWt;
                totalRate += rate;
                totalValue += value;
                count++;
            }
            //average
            double avgDrc = (totalDry/totalQuantity)*100;
            double avgRate = totalRate / count;
            addTableRow(table, (PdfPCell.BOTTOM|PdfPCell.TOP|PdfPCell.LEFT|PdfPCell.RIGHT),
                                CommonFuncs.tableContentFont, "", "", "",
                                new DecimalFormat("##,##,##0.000").format(totalQuantity), 
                                new DecimalFormat("##,##,##0.000").format(avgDrc), 
                                new DecimalFormat("##,##,##0.000").format(totalDry),
                                new DecimalFormat("##,##,##0.00").format(avgRate), 
                                new DecimalFormat("##,##,##0.00").format(totalValue));
            
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
            
            pageNum = pageNum + 1;
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 5, 0);
        }
        
        private void addTitle(PdfContentByte cb, Document document){
            try{
               int base = 10;

                Phrase title = new Phrase("PURCHASE REPORT (LATEX)", CommonFuncs.titleFont);
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
