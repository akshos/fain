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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import database.BarrelDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import utility.UtilityFuncs;
import utility.Wait;
/**
 *
 * @author akshos
 */
public class BarrelDailyReport {
    private static String PREFIX = "barreldailyreport";
    
    private static int pageNum;
    private static String sdate;
    
    
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new BarrelDailyReport.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, Wait wait, String paper, String orientation, String date){
        pageNum = 1;
        sdate = UtilityFuncs.dateSqlToUser(date);
       
        boolean ret;
        Document doc;
        try{
            doc = startDocument(paper, orientation);
            
            ret = addTable(con, doc, date);
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
            wait.closeWait();
            return false;
        }
        ViewPdf.openPdfViewer(PREFIX + ".pdf");
        wait.closeWait();
        return ret;
    }
    
    private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addTableRow(PdfPTable table, int border, Font font, String custCode, String custName, String stock, String issued, String lifted, String diff){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(custCode, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(custName, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(stock, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(issued, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(lifted, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(diff, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
    }
    
    private static boolean addTable(DBConnection con, Document doc, String date){
        float columns[] = {0.6f, 2, 0.8f, 0.8f, 0.8f, 0.8f};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Cust Code");
        addHeaderCell(table, "Customer");
        addHeaderCell(table, "Stock");
        addHeaderCell(table, "Issued");
        addHeaderCell(table, "Lifted");
        addHeaderCell(table, "Diff");
        table.setHeaderRows(1);
        
        String custCode, custName, stock, issued, lifted, diff;
        int count = 0;
        try{
            ResultSet rs = BarrelDB.getBarrelsOnDateRS(con.getStatement(), date);
            if(rs == null){
                JOptionPane.showMessageDialog(null, "Error getting barrel issues and lifts", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            while(rs.next()){
                custCode = rs.getString("customerCode");
                custName = rs.getString("accountHead");
                stock = rs.getString("stock");
                issued = rs.getString("issued");
                lifted = rs.getString("lifted");
                diff = rs.getString("difference");

                addTableRow(table, (PdfPCell.NO_BORDER),
                    CommonFuncs.tableContentFont, custCode, custName, 
                    stock, issued, lifted, diff);  
                count++;
            }            
            doc.add(table);
            if(count == 0){
                JOptionPane.showMessageDialog(null, "There is nothing to show", "Empty", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
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
                
                Phrase title = new Phrase("BARREL DAILY REPORT", CommonFuncs.titleFont);
                Phrase date = new Phrase("DATE : " + sdate);
                
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    title,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 35, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    date,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 20, 0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    }
}
