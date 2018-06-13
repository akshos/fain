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
import database.BranchDB;
import database.DBConnection;
import database.MasterDB;
import database.SalesDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JOptionPane;
import utility.UtilityFuncs;

/**
 *
 * @author akshos
 */
public class SalesLatexReport {
    private static String PREFIX = "salesreport";
    
    private static DBConnection scon;
    private static String sfromDate;
    private static String stoDate;
    private static String sbranch;
    private static String sbranchName;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    private static String scat;
       
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new SalesLatexReport.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, String paper, String orientation, String fromDate, String toDate){
        scon = con;
        sfromDate = UtilityFuncs.dateSqlToUser(fromDate);
        stoDate = UtilityFuncs.dateSqlToUser(toDate);
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 0;
        
        boolean ret = false;
        
        Document doc;
        try{
            doc = startDocument(paper, orientation);
            
            ret = createTable(con, doc, fromDate, toDate);
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
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
     
    private static void addTableRow(PdfPTable table, int border, Font font, String date, String billNo, String party, String barrels, String qnty, String drc, String drywt, String rate, String value){
        PdfPCell cell;
        
        date = UtilityFuncs.dateSqlToUser(date);
        
        cell = new PdfPCell(new Phrase(date, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(billNo, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(party, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(barrels, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
        
        cell = new PdfPCell(new Phrase(drywt, font));
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
    
    private static boolean createTable(DBConnection con, Document doc, String fromDate, String toDate){
        float columns[] = {1, 0.8f, 2, 0.7f, 1, 1, 1, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Bill");
        addHeaderCell(table, "Party");
        addHeaderCell(table, "Brrls");
        addHeaderCell(table, "Qnty");
        addHeaderCell(table, "Drc");
        addHeaderCell(table, "Dry Wt.");
        addHeaderCell(table, "Rate");
        addHeaderCell(table, "Value");
        table.setHeaderRows(1);
        
        double valueTotal = 0.0;
        double dryWtTotal = 0.0;
        double qntyTotal = 0.0;
        int barrelTotal = 0;
        
        double drc = 0.0;
        String date;
        String party;
        String billNo;
        double value, dryWt, qnty, rate;
        int barrels;
        try{
            String expenseAccounts[][] = MasterDB.getAccountIdByCat(con.getStatement(), "EX");
            if(expenseAccounts == null){
                JOptionPane.showMessageDialog(null, "No Expense Accounts", "ERROR", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            ResultSet rs = SalesDB.getSalesBetweenIncDatesRS(con.getStatement(), fromDate, toDate);
            while(rs.next()){
                date = rs.getString("date");
                billNo = rs.getString("billNo");
                party = rs.getString("accountHead");
                barrels = rs.getInt("diff");
                qnty = rs.getDouble("quantity");
                drc = rs.getDouble("drc");
                dryWt = rs.getDouble("dryRubber");
                rate = rs.getDouble("rate");
                value = rs.getDouble("value");
                
                addTableRow(table, (PdfPCell.NO_BORDER), 
                        CommonFuncs.tableContentFont, date, billNo, party, String.valueOf(barrels),
                        new DecimalFormat("##,##,##0.000").format(qnty), 
                        new DecimalFormat("##,##,##0.000").format(drc), 
                        new DecimalFormat("##,##,##0.000").format(dryWt), 
                        new DecimalFormat("##,##,##0.00").format(rate), 
                        new DecimalFormat("##,##,##0.00").format(value));

                qntyTotal += qnty;
                dryWtTotal += dryWt;
                valueTotal += value;
                barrelTotal += barrels;
            }
            
            drc = (100*dryWtTotal/qntyTotal);
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM), 
                            CommonFuncs.tableContentFont, "", "", "", 
                            new DecimalFormat("##,##,##0").format(barrelTotal),
                            new DecimalFormat("##,##,##0.000").format(qntyTotal), 
                        new DecimalFormat("##,##,##0.000").format(drc), 
                        new DecimalFormat("##,##,##0.000").format(dryWtTotal), 
                        "", 
                        new DecimalFormat("##,##,##0.00").format(valueTotal));
            
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
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 5, 0);
        }
        
        private void addTitle(PdfContentByte cb, Document document){
        try{
            int base = 10;
            
            Phrase title = new Phrase("SALES LATEX REPORT", CommonFuncs.titleFont);
            Phrase branch = new Phrase(sbranchName + " (" + sbranch + ")", CommonFuncs.subTitleFont);
            Phrase accounts = new Phrase("From : " + sfromDate + " To : " + stoDate, CommonFuncs.subTitleFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    title,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 35, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    branch,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 20, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    accounts,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 10, 0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    }
}
