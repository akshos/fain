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
import database.CompanyBarrelDB;
import database.MasterDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import utility.UtilityFuncs;
import utility.Wait;
/**
 *
 * @author akshos
 */
public class BarrelSummaryReport {
    private static String PREFIX = "barrelsummaryreport";
    
    private static int pageNum;
    
    
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new BarrelSummaryReport.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, Wait wait, String paper, String orientation){
        pageNum = 1;
        
        boolean ret;
        Document doc;
        try{
            doc = startDocument(paper, orientation);
            
            addBarrelSummary(con, doc);
            CommonFuncs.addEmptyLine(doc, 1);
            ret = addTable(con, doc);
            
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
    
    private static void addBarrelSummary(DBConnection dbConnection, Document doc){
        float columns[] = {1f, 0.5f, 1f, 0.5f};
        PdfPTable table = new PdfPTable(columns);
        PdfPCell cell;
        
        int companyOpStock = CompanyBarrelDB.getCompanyOpStock(dbConnection.getStatement());
        int companyTotalIssued = CompanyBarrelDB.getTotalIssued(dbConnection.getStatement());
        int companyTotalLifted = CompanyBarrelDB.getTotalLifted(dbConnection.getStatement());
        
        
        int companyInitBarrels = companyOpStock + companyTotalIssued - companyTotalLifted;
        
        int companyShortage = CompanyBarrelDB.getCompanyShortage(dbConnection.getStatement());
        
        //net number of barrels from company currently at hand/at customer
        int companyBarrels = companyInitBarrels - companyShortage;
        
        int customerTotalIssued = BarrelDB.getTotalIssued(dbConnection.getStatement());
        int customerTotalLifted = BarrelDB.getTotalLifted(dbConnection.getStatement());
        
        //get current number of issued barrels
        int customerIssued = customerTotalIssued - customerTotalLifted;
        //get the current number of latex barrels at hand
        int latexBarrels = customerTotalLifted - companyTotalLifted;
        //get the current number of empty barrels at hand
        int emptyBarrels = companyBarrels - (customerIssued + latexBarrels);
        
        
        cell = new PdfPCell(new Phrase("Business Account", CommonFuncs.tableContentFont));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Customer Account", CommonFuncs.tableContentFont));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Company : ", CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(String.valueOf(companyInitBarrels), CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Issued : ", CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(String.valueOf(customerIssued), CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Shortage : ", CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(String.valueOf(companyShortage), CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Latex Barrels : ", CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(String.valueOf(latexBarrels), CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Total : ", CommonFuncs.tableContentFont));
        cell.setRowspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(String.valueOf(companyBarrels), CommonFuncs.tableBoldFont));
        cell.setRowspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Empty Barrels : ", CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(String.valueOf(emptyBarrels), CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Total : ", CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(String.valueOf(customerIssued + latexBarrels + emptyBarrels), CommonFuncs.tableBoldFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        
        try{
            doc.add(table);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addTableRow(PdfPTable table, int border, Font font, String custCode, String customer, String contact, String balance){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(custCode, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(customer, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(contact, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(balance, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        table.addCell(cell);
    }
    
    private static boolean addTable(DBConnection con, Document doc){
        float columns[] = {0.7f, 2f, 1f, 0.7f};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Cust Code");
        addHeaderCell(table, "Customer");
        addHeaderCell(table, "Contact");
        addHeaderCell(table, "Balance");
        table.setHeaderRows(1);
        
        String custCode, customer, contact, balance;
        try{
            ResultSet rs = BarrelDB.getBarrelsSummaryRS(con.getStatement());
            if(rs == null){
                JOptionPane.showMessageDialog(null, "Error getting barrel issues and lifts", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            while(rs.next()){
                custCode = rs.getString("customerCode");
                customer = rs.getString("name");
                contact = rs.getString("contact");
                balance = rs.getString("barrels");

                addTableRow(table, (PdfPCell.NO_BORDER),
                    CommonFuncs.tableContentFont, custCode, customer, contact, balance);  
                
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
            pageNum = pageNum + 1;
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 5, 0);
        }
        
       private void addTitle(PdfContentByte cb, Document document){
        try{
            int base = 10;
                
                Phrase title = new Phrase("BARREL SUMMARY REPORT", CommonFuncs.titleFont);
                
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    title,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + base + 35, 0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    }
}
