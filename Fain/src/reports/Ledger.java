/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import database.CustomerDB;
import database.DBConnection;
import database.MasterDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 *
 * @author akshos
 */
public class Ledger {
    private static String PREFIX = "ledger";
    
    private static void addTitle(Document doc, String branch, String acFrom, String acTo){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("LEDGER", CommonFuncs.titleFont));
            String subTitle = "Branch : " + branch + "  |  ";
            subTitle += "Account From : " + acFrom + " | To : " + acTo;
            title.add(CommonFuncs.alignCenter(subTitle, CommonFuncs.subTitleFont));
            doc.add(title);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static String createReport(DBConnection con, String paper, String orientation,  String branch, String accFrom, String accTo){
        Document doc = new Document();
        CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
        try{
            PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            doc.open();
            
            CommonFuncs.addMetaData(doc);
            CommonFuncs.addHeader(con, doc);
            addTitle(doc, branch, accFrom, accTo);
            CommonFuncs.addEmptyLine(doc, 2);
            String accountData[][] = CustomerDB.getCustomersInBranch(con.getStatement(), branch);
            int startIndex = Arrays.asList(accountData[0]).indexOf(accFrom);
            int endIndex = startIndex;
            if(!accTo.isEmpty()){
                endIndex = Arrays.asList(accountData[0]).indexOf(accTo);
            }
            
            for(int i = startIndex; i <= endIndex; i++){
                addLedger(con, doc, accountData[0][i]);
            }
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addTableRow(PdfPTable table, String date, String branch, String nar, Double debit, Double credit){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(date, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(branch, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(nar, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        
        String debitStr = (debit==0)?"":String.format("%.2f",debit);
        cell = new PdfPCell(new Phrase(debitStr, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        
        String creditStr = (credit==0)?"":String.format("%.2f",credit);
        cell = new PdfPCell(new Phrase(creditStr, CommonFuncs.tableContentFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }
    
    private static void addLedger(DBConnection con, Document doc, String accId){
        float columns[] = {0.5f, 1, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Date");
        addHeaderCell(table, "Branch");
        addHeaderCell(table, "Narration");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        String date, branch, nar;
        Double openingBal = Double.parseDouble(MasterDB.getOpeningBal(con.getStatement(), accId));
        Double creditTotal = 0.0, debitTotal = 0.0, amount, debit , credit;
        addTableRow(table, "", "", "Opening Balance", 0.0, openingBal);
        ResultSet rs = TransactionDB.getContainingAccount(con.getStatement(), accId);
        creditTotal = openingBal;
        try{
            while(rs.next()){
                date = rs.getString("date");
                branch = rs.getString("branch");
                amount = rs.getDouble("amount");
                debit = credit = 0.0;
                nar = rs.getString("narration");
                if(rs.getString("debit").compareTo(accId) == 0){
                    debit = amount;
                }else if(rs.getString("credit").compareTo(accId) == 0){
                    credit = amount;
                }
                addTableRow(table, date, branch, nar, debit, credit);
                debitTotal += debit;
                creditTotal += credit;
            }
            addTableRow(table, "", "", "Closing Balance", creditTotal-debitTotal, 0.0);
            doc.add(table);
        }catch(Exception se){
            se.printStackTrace();
        }
        
    }
    
    public static void main(String[] args){
        createReport(null, "A4", "Landscape", "", "1000", "1023");
    }
}
