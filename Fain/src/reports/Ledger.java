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
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.swing.JOptionPane;
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
            if(accountData == null){
                JOptionPane.showMessageDialog(null, "No Accounts Available", "NO ACCOUNTS", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int startIndex = Arrays.asList(accountData[0]).indexOf(accFrom);
            int endIndex = 0;
            if(accTo.compareTo("All") == 0){
                endIndex = accountData[0].length-1;                
            }else if(accTo.compareTo("None") == 0)
            {
                endIndex = startIndex;
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
            return;
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
        if(openingBal > 0){
            addTableRow(table, (PdfPCell.RECTANGLE), 
                    CommonFuncs.tableBoldFont, "", "Opening Balance", 
                    new DecimalFormat("##,##,##0.00").format(openingBal), "" );
            
            debitTotal = openingBal;
        }else{
            addTableRow(table, (PdfPCell.RECTANGLE), 
                    CommonFuncs.tableBoldFont, "", "Opening Balance", 
                    "", new DecimalFormat("##,##,##0.00").format(-1*openingBal));
            
            creditTotal = Math.abs(openingBal);
        }
        
        ResultSet rs = TransactionDB.getContainingAccount(con.getStatement(), accId);
        try{
            while(rs.next()){
                date = rs.getString("date");
                amount = rs.getDouble("amount");
                debit = credit = 0.0;
                nar = rs.getString("narration");
                if(rs.getString("debit").compareTo(accId) == 0){
                    if(nar.isEmpty()){
                        nar = MasterDB.getAccountHead(con.getStatement(), rs.getString("credit"));
                    }
                    addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), 
                            CommonFuncs.tableContentFont,
                            date, nar, 
                            new DecimalFormat("##,##,##0.00").format(amount), "");
                    
                    debitTotal += amount;
                    pageDebitTotal += amount;                    
                }else if(rs.getString("credit").compareTo(accId) == 0){
                    if(nar.isEmpty()){
                        nar = MasterDB.getAccountHead(con.getStatement(), rs.getString("debit"));
                    }
                    addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), 
                            CommonFuncs.tableContentFont, 
                            date, nar, 
                            "", new DecimalFormat("##,##,##0.00").format(amount));
                    
                    creditTotal += amount;
                    pageCreditTotal += amount;
                }
            }
            Double closingBal  = creditTotal - debitTotal;
            addTableRow(table, (PdfPCell.RECTANGLE|PdfPCell.TOP), 
                    CommonFuncs.tableBoldFont,"", "TOTALS : ", 
                    new DecimalFormat("##,##,##0.00").format(debitTotal), 
                    new DecimalFormat("##,##,##0.00").format(creditTotal));
            
            if(closingBal > 0){
                addTableRow(table, (PdfPCell.RECTANGLE|PdfPCell.TOP), 
                        CommonFuncs.tableBoldFont, "", "Closing Balance", 
                        "", new DecimalFormat("##,##,##0.00").format(closingBal));
            
            }else{
                addTableRow(table, (PdfPCell.RECTANGLE),
                        CommonFuncs.tableBoldFont, "", "Closing Balance", 
                        new DecimalFormat("##,##,##0.00").format(Math.abs(closingBal)), "");
            
            }
            
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
