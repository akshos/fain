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
import javax.swing.JOptionPane;
import utility.UtilityFuncs;
import utility.Wait;
/**
 *
 * @author akshos
 */
public class TrialBalance {
    private static String PREFIX = "trialbal";
    
    private static String currAcc;
    private static DBConnection scon;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    private static String sdate;
    
    
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
    
    public static boolean createReport(DBConnection con, Wait wait, String paper, String orientation, String date) throws Exception{
        scon = con;
        currAcc = "";
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 1;
        sdate = UtilityFuncs.dateSqlToUser(date);
        boolean ret = false;
        
        ret = CommonFuncs.updateAllAccounts(con, date);
        if(!ret){
            JOptionPane.showMessageDialog(null, "Failed to Update Accounts", "FAILED", JOptionPane.WARNING_MESSAGE);
            wait.closeWait();
            return false;
        }
        
        Document doc;
        try{
            doc = startDocument(paper, orientation);
            Thread.sleep(500);
            ret = addTable(con, doc);
            if(ret)
                doc.close();
            Thread.sleep(500);
        }catch(Exception e){
            e.printStackTrace();
            wait.closeWait();
            return false;
        }
        if(ret)
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
    
    private static void addTableRow(PdfPTable table, int border, Font font, String accNo, String accName, String debit, String credit){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(accNo, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
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
    
    private static boolean addTable(DBConnection con, Document doc){
        float columns[] = {0.6f, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Code");
        addHeaderCell(table, "Account");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        double creditTotal, debitTotal;
        double currentBalance;
        String accNo;
        String accHead;
        creditTotal = debitTotal = 0.0;
        String cat;
        
        try{
            ResultSet rs = MasterDB.selectAll(con.getStatement());
            while(rs.next()){
                accHead = rs.getString("accountHead");
                accNo = rs.getString("accountNo");
                cat = rs.getString("category");
                if(cat.compareTo("SK") == 0){
                    accHead = "Op. " + accHead;
                    currentBalance = rs.getDouble("openingBal");
                }else{
                    currentBalance = rs.getDouble("closingBal");
                }
                
                if(currentBalance > 0){
                    addTableRow(table, (PdfPCell.NO_BORDER),
                        CommonFuncs.tableBoldFont, accNo, accHead, 
                        new DecimalFormat("##,##,##0.00").format(Math.abs(currentBalance)), "");
                    
                    debitTotal += currentBalance;
                    pageDebitTotal += currentBalance;
                }else if(currentBalance < 0){
                    addTableRow(table, (PdfPCell.NO_BORDER),
                        CommonFuncs.tableBoldFont, accNo, accHead, 
                        "", new DecimalFormat("##,##,##0.00").format(Math.abs(currentBalance)));
                    
                    creditTotal += Math.abs(currentBalance);
                    pageCreditTotal += Math.abs(currentBalance);
                }
                
            }
                
            addTableRow(table, (PdfPCell.RECTANGLE|PdfPCell.TOP),
                        CommonFuncs.tableBoldFont, "", "TOTALS", 
                        new DecimalFormat("##,##,##0.00").format(debitTotal), 
                        new DecimalFormat("##,##,##0.00").format(creditTotal));


            
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
                
                Phrase title = new Phrase("TRIAL BALANCE", CommonFuncs.titleFont);
                Phrase date = new Phrase("As on : " + sdate);
                
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
