/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
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
import java.util.Arrays;
/**
 *
 * @author akshos
 */
public class Ledger {
    private static String PREFIX = "ledger";
    private static String sbranch = "";
    private static String saccFrom = "";
    private static String saccTo = "";
    private static DBConnection scon = null;
    private static String currAcc = "";
    private static int pageNum = 1;
    private static double pageCreditTotal = 0;
    private static double pageDebitTotal = 0;
    
    
    private static void addTitle(DBConnection con, Document doc, String accId){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("LEDGER", CommonFuncs.titleFont));
            doc.add(title);
            
            String accountHead = MasterDB.getAccountHead(con.getStatement(), accId);
            Paragraph para = new Paragraph();
            para.add(CommonFuncs.alignCenter("ACCOUNT : " + accountHead + " (" + accId + ")", CommonFuncs.accountHeadFont));
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
    
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static void createReport(DBConnection con, String paper, String orientation,  String branch, String accFrom, String accTo){
        sbranch = branch;
        scon = con;
        saccFrom = accFrom;
        saccTo = accTo;
        pageNum = 1;
        pageCreditTotal = 0;
        pageDebitTotal = 0;
        
        try{
            String accountData[][];
            
            if(branch.compareTo("None") == 0){
                accountData = MasterDB.getAccountHead(con.getStatement());
            }else{
                accountData = CustomerDB.getCustomersInBranch(con.getStatement(), branch);
            }
            
            int startIndex = Arrays.asList(accountData[0]).indexOf(accFrom);
            int endIndex = startIndex;
            if(accTo.compareTo("All") == 0){
                endIndex = accountData[0].length-1;                
            }else if(!accTo.isEmpty()){
                endIndex = Arrays.asList(accountData[0]).indexOf(accTo);
            }
            
            currAcc = accountData[0][startIndex];
            Document doc = startDocument(paper, orientation);
            for(int i = startIndex; i <= endIndex; i++){
                addLedger(con, doc, accountData[0][i], branch, accFrom, accTo);
                if(i < endIndex){
                    currAcc = accountData[0][i+1];
                    doc.newPage();
                }
            }
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        ViewPdf.openPdfViewer(PREFIX + ".pdf");
    }
       
    private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addLedger(DBConnection con, Document doc, String accId, String branch, String accFrom, String accTo){
        
        addTable(con, doc, accId);
    }
    
    private static void addTableRow(PdfPTable table, int border, String date, String nar, String debit, String credit){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(date, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);

        
        cell = new PdfPCell(new Phrase(nar, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(debit, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(credit, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(border);
        table.addCell(cell);
    }
    
    private static void addTable(DBConnection con, Document doc, String accId){
        float columns[] = {0.7f, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Narration");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        String date, nar;
        Double openingBal = Double.parseDouble(MasterDB.getOpeningBal(con.getStatement(), accId));
        Double creditTotal = 0.0, debitTotal = 0.0, amount, debit , credit;
        addTableRow(table, (PdfPCell.RECTANGLE), "", "Opening Balance", "", String.format("%.2f",openingBal));
        ResultSet rs = TransactionDB.getContainingAccount(con.getStatement(), accId);
        creditTotal = openingBal;
        try{
            while(rs.next()){
                date = rs.getString("date");
                amount = rs.getDouble("amount");
                debit = credit = 0.0;
                nar = rs.getString("narration");
                if(rs.getString("debit").compareTo(accId) == 0){
                    debit = amount;
                }else if(rs.getString("credit").compareTo(accId) == 0){
                    credit = amount;
                }
                addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), date, nar, String.format("%.2f",debit), String.format("%.2f",credit));
                debitTotal += debit;
                pageDebitTotal += debit;
                creditTotal += credit;
                pageCreditTotal += credit;
                
            }
            Double closingBal  = creditTotal - debitTotal;
            addTableRow(table, (PdfPCell.RECTANGLE|PdfPCell.TOP), "", "Closing Balance", String.format("%.2f",closingBal), "");
            doc.add(table);
        }catch(Exception se){
            se.printStackTrace();
        }
        
    }
       
    private static class ShowHeader extends PdfPageEventHelper{        
        public void onStartPage(PdfWriter writer, Document docuement){
            CommonFuncs.addHeader(scon, docuement);
            addTitle(scon, docuement, currAcc);
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
