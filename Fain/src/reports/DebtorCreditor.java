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
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author akshos
 */
public class DebtorCreditor {
    private static String PREFIX = "";
    private static String sbranch = "";
    private static String saccFrom = "";
    private static String saccTo = "";
    private static String stype = "";
    private static DBConnection scon = null;
    private static String currAcc = "";
    private static int pageNum = 1;
    private static double pageCreditTotal = 0;
    private static double pageDebitTotal = 0;
    
    
    private static void addTitle(DBConnection con, Document doc, String type){
        try{
            Paragraph title = new Paragraph();
            
            String str = (type.compareTo("DB") == 0)?"DEBTORS":"CREDITORS";
            
            title.add(CommonFuncs.alignCenter(str, CommonFuncs.titleFont));
            doc.add(title);
            
            Paragraph para = new Paragraph();
            para.add(CommonFuncs.alignCenter("ACCOUNT From: " + saccFrom + " To: " + saccTo , CommonFuncs.accountHeadFont));

            if(sbranch != null && !sbranch.isEmpty()){
                String branchName = BranchDB.getBranchName(con.getStatement(), sbranch);
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
    
    public static boolean createReport(DBConnection con, String paper, String orientation,  String branch, String accFrom, String accTo, String type){
        sbranch = branch;
        scon = con;
        saccFrom = accFrom;
        saccTo = accTo;
        pageNum = 1;
        pageCreditTotal = 0;
        pageDebitTotal = 0;
        stype = type;
        
        try{
            String accountData[][];
            
            accountData = CustomerDB.getCustomersInBranch(con.getStatement(), branch);
            
            if(accountData == null){
                JOptionPane.showMessageDialog(null, "No Accounts Available", "NO ACCOUNTS", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            int startIndex = Arrays.asList(accountData[0]).indexOf(accFrom);
            int endIndex = startIndex;
            
            if(startIndex == -1){
                JOptionPane.showMessageDialog(null, "Cannot find From Account", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if(accTo.compareTo("All") == 0){
                endIndex = accountData[0].length-1;                
            }else if(accTo.compareTo("None") == 0)
            {
                endIndex = startIndex;
            }else if(!accTo.isEmpty()){
                endIndex = Arrays.asList(accountData[0]).indexOf(accTo);
            }
            if(endIndex == -1){
                JOptionPane.showMessageDialog(null, "Cannot find To Account", "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if(endIndex < startIndex){
                endIndex = startIndex;
            }
            
            //calculate closing balance of all accounts
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();  
            CommonFuncs.updateMaster(con, df.format(now)); //pass current date
            
            Document doc = startDocument(paper, orientation);
            boolean ret = addTable(con, doc, accountData, startIndex, endIndex, type);
            if(!ret){
                JOptionPane.showMessageDialog(null, "Failed to Create Report", "FAILED", JOptionPane.WARNING_MESSAGE);
                doc.close();
                return false;
            }
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
    
    private static boolean addTable(DBConnection con, Document doc, String[][] accountData, int start, int end, String type){
        float columns[] = {0.7f, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Code");
        addHeaderCell(table, "Account");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        double debitTotal, creditTotal, closingBalance;
        debitTotal = creditTotal = 0.0;
        boolean print = false;
        String accId, accName;
        
        
        try{
            for(int i = start; i <= end; i++){
                accId = accountData[0][i];
                accName = accountData[1][i];
                
                closingBalance = MasterDB.getClosingBalance(con.getStatement(), accId);
                if(type.compareTo("DB") == 0 && closingBalance > 0){
                    print = true;
                }else if(type.compareTo("CR") == 0 && closingBalance < 0){
                    print = true;
                }else{
                    print = false;
                }
                
                if(print){
                    if(closingBalance > 0){
                        addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), 
                            CommonFuncs.tableContentFont,
                            accId, accName, 
                            new DecimalFormat("##,##,##0.00").format(closingBalance), "");
                        
                        debitTotal += closingBalance;
                        pageDebitTotal += closingBalance;
                    }else{
                        addTableRow(table, (PdfPCell.LEFT|PdfPCell.RIGHT), 
                            CommonFuncs.tableContentFont,
                            accId, accName, 
                            "", new DecimalFormat("##,##,##0.00").format(Math.abs(closingBalance)));
                        
                        creditTotal += Math.abs(closingBalance);
                        pageCreditTotal += Math.abs(closingBalance);
                    }
                }
                
            }
            addTableRow(table, (PdfPCell.RECTANGLE|PdfPCell.TOP), 
                    CommonFuncs.tableBoldFont,"", "TOTALS : ", 
                    new DecimalFormat("##,##,##0.00").format(debitTotal), 
                    new DecimalFormat("##,##,##0.00").format(creditTotal));
                        
            doc.add(table);
            
        }catch(Exception se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
       
    private static class ShowHeader extends PdfPageEventHelper{        
        public void onStartPage(PdfWriter writer, Document docuement){
            CommonFuncs.addHeader(scon, docuement);
            addTitle(scon, docuement, stype);
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