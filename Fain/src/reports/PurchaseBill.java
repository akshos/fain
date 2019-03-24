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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import database.BranchDB;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.PurchaseLatexDB;
import java.io.FileOutputStream;
import javax.swing.JOptionPane;
import utility.UtilityFuncs;

/**
 *
 * @author akshos
 */
public class PurchaseBill {
    private static String PREFIX = "purchasebill";
    
    private static void addTitle(DBConnection con, Document doc, String accId, String accountHead){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("PURCHASE BILL", CommonFuncs.titleFont));
            doc.add(title);
            
            Paragraph para = new Paragraph();
            para.add(CommonFuncs.alignCenter("CUSTOMER NAME : " + accountHead + " (" + accId + ")", CommonFuncs.accountHeadFont));
            String branch = CustomerDB.getBranch(con.getStatement(), accId);
            if(branch != null && !branch.isEmpty()){
                String branchName = BranchDB.getBranchName(con.getStatement(), branch);
                para.add(CommonFuncs.alignCenter("BRANCH : " + branchName, CommonFuncs.branchFont));
            }
            doc.add(para);
            CommonFuncs.addEmptyLine(doc, 1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static Document startDocument(){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, "A4", "Portrait");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            doc.open();
            CommonFuncs.addMetaData(doc);
            
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createBill(DBConnection con, String purchaseId){
        try{
            Thread.sleep(500);
            String[] purchaseData = PurchaseLatexDB.selectOneId(con.getStatement(), purchaseId);
            if(purchaseData == null){
                JOptionPane.showMessageDialog(null, "Could not get Purchase Data", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String date = purchaseData[2];
            String billNo  = purchaseData[3];
            String party = purchaseData[4];
            String quantity = purchaseData[5];
            String drc = purchaseData[6];
            String dryRubber = purchaseData[7];
            String rate = purchaseData[8];
            String value = purchaseData[9];
            
            Document doc = startDocument();
            
            CommonFuncs.addEmptyLine(doc, 2);
            CommonFuncs.addHeader(con, doc);
            String accountHead = MasterDB.getAccountHead(con.getStatement(), party);
            addTitle(con, doc, party, accountHead);
            
            float columns[] = {1, 2};
            PdfPTable table = new PdfPTable(columns);
            table.setWidthPercentage(60);
            addHeaderCell(table, "PARTICULARS");
            addHeaderCell(table, "VALUE");
            table.setHeaderRows(1);
            
            
            addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), CommonFuncs.tableContentFont,
                    "DATE : ", UtilityFuncs.dateSqlToUser(date));
            
            addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), CommonFuncs.tableContentFont,
                    "BILL NO : ", billNo);
            
            addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), CommonFuncs.tableContentFont,
                    "QUANTITY : ", quantity);
            
            addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), CommonFuncs.tableContentFont,
                    "DRC : ", drc);
            
            addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), CommonFuncs.tableContentFont,
                    "DRY RUBBER : ", dryRubber);
            
            addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), CommonFuncs.tableContentFont,
                    "RATE : ", "Rs. " + rate);
            
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM|PdfPCell.LEFT|PdfPCell.RIGHT), CommonFuncs.tableBoldFont,
                    "VALUE : ", "Rs. " + value);
            
            doc.add(table);
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        ViewPdf.openPdfViewer(PREFIX + ".pdf");
        return true;
    }
    
    private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addTableRow(PdfPTable table, int border, Font font, String key, String value){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(key, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);

        
        cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
    }
}
