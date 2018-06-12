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
import database.DBConnection;
import database.MasterDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import utility.UtilityFuncs;

/**
 *
 * @author akshos
 */
public class ProfitLossBalanceSheet {
    private static String PREFIX = "plbalancesheet";
    
    private static String currAcc;
    private static DBConnection scon;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    private static String sdate;
    private static String stitle;
    
    private static String[][] liabilityCats =   {{"SH","SHARE CAPITAL"},
                                                {"LI","LIABILITIES"},
                                                {"LN","LOAN & ADVANCES"},
                                                {"CR","CREDITORS"}};
    
    private static String[][] assetCats =   {{"AS","ASSETS"},
                                            {"DP","DEPOSITS"},
                                            {"DB","DEBTORS"},
                                            {"CH","CASH IN HAND "},
                                            {"BK","CASH AT BANK"},
                                            {"SK","CLOSING STOCK"}};
    
    
    
    private static Document startDocument(String paper, String orientation){
        try{
            Document doc = new Document();
            CommonFuncs.setDocumentSizeOrientation(doc, paper, orientation);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            writer.setPageEvent(new ProfitLossBalanceSheet.ShowHeader());
            doc.open();
            CommonFuncs.addMetaData(doc);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean createReport(DBConnection con, String paper, String orientation, String date){
        scon = con;
        currAcc = "";
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 0;
        sdate = UtilityFuncs.dateSqlToUser(date);
        boolean ret = false;
        
        Document doc;
        
        //update closing balance of all accounts
        ret = CommonFuncs.updateAllAccounts(con, date);
        if(!ret){
            JOptionPane.showMessageDialog(null, "Failed to Update Accounts", "FAILED", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try{
            stitle = "TRADING ACCOUNT";
            doc = startDocument(paper, orientation);
            double grossIncome = tradingAccount(con, doc);
            
            doc.newPage();
            stitle = "PROFIT AND LOSS ACCOUNT";
            double profitLoss = profitAndLoss(con, doc, grossIncome);
            
            doc.newPage();
            stitle = "BALANCE SHEET";
            balanceSheet(con, doc, profitLoss);
            
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        ViewPdf.openPdfViewer(PREFIX + ".pdf");
        return ret;
    }
    
    private static void addHeaderCell(PdfPTable table, String header){
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(header, CommonFuncs.tableHeaderFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private static void addTableRow(PdfPTable table, int border, Font font, String code, String accName, String debit, String credit){
        PdfPCell cell;
        
        cell = new PdfPCell(new Phrase(code, CommonFuncs.tableContentFont));
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
    
    public static void addCreditDebit(PdfPTable table, int border, String accId, String accName, double amount){
        if(amount > 0){
            addTableRow(table, border,
                        CommonFuncs.tableContentFont, accId, accName,
                        new DecimalFormat("##,##,##0.00").format(amount), "");
        }else if(amount < 0){
            addTableRow(table, border,
                        CommonFuncs.tableContentFont, accId, accName,
                        "", new DecimalFormat("##,##,##0.00").format(Math.abs(amount)));
        }
    }
    
    private static double tradingAccount(DBConnection con, Document doc){
        
        float columns[] = {1, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Code");
        addHeaderCell(table, "Account Head");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        double purchaseTotal = 0.0, saleTotal = 0.0;
        double openingBal, currentBal;
        ResultSet rs;
        
        try{
            //Opening stock
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SK");
            while(rs.next()){
                openingBal = rs.getDouble("openingBal");
                if(openingBal > 0){
                    addTableRow(table, (PdfPCell.NO_BORDER),
                                CommonFuncs.tableContentFont, rs.getString("accountNo"), "Op.Stk " + rs.getString("accountHead"),
                                new DecimalFormat("##,##,##0.00").format(openingBal), "");
                }
                
                purchaseTotal += openingBal;
            }
            //Purchase
            rs = MasterDB.getAccountsByCat(con.getStatement(), "PR");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                purchaseTotal += currentBal;
            }
            //Sales
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SL");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                saleTotal += currentBal;
            }
            //Closing stock
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SK");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                addTableRow(table, (PdfPCell.NO_BORDER),
                                CommonFuncs.tableContentFont, rs.getString("accountNo"), "Cl.Stk " + rs.getString("accountHead"),
                                "", new DecimalFormat("##,##,##0.00").format(currentBal));
                
                saleTotal += (-1 * currentBal);
            }
            //Selling Expenses
            rs = MasterDB.getAccountsByCat(con.getStatement(), "SE");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                purchaseTotal += currentBal;
            }
            
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                        CommonFuncs.tableContentFont, "", "",
                        new DecimalFormat("##,##,##0.00").format(purchaseTotal), 
                        new DecimalFormat("##,##,##0.00").format(Math.abs(saleTotal)));
            
            //print GROSS INCOME
            double balance = purchaseTotal + saleTotal;
            addCreditDebit(table, (PdfPCell.TOP|PdfPCell.BOTTOM), "", "GROSS INCOME", balance);
            
            doc.add(table);
            
            return balance;
            
        }catch(Exception se){
            se.printStackTrace();
            return 0.0;
        }
    }
    
    
    private static double profitAndLoss(DBConnection con, Document doc, double grossIncome){
        float columns[] = {1, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Code");
        addHeaderCell(table, "Account Head");
        addHeaderCell(table, "Debit");
        addHeaderCell(table, "Credit");
        table.setHeaderRows(1);
        
        addCreditDebit(table, (PdfPCell.NO_BORDER), "", "GROSS INCOME", grossIncome);
        
        double currentBal;
        double incomeTotal = 0.0;
        double expenseTotal = 0.0;
        
        if(grossIncome < 0){
            incomeTotal += grossIncome;
        }else{
            expenseTotal += grossIncome;
        }
        
        ResultSet rs;
        
        try{
            //Income accounts
            rs = MasterDB.getAccountsByCat(con.getStatement(), "IN");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                incomeTotal += currentBal;
            }
            
            //Expence accounts
            rs = MasterDB.getAccountsByCat(con.getStatement(), "EX");
            while(rs.next()){
                currentBal = rs.getDouble("closingBal");
                
                addCreditDebit(table, (PdfPCell.NO_BORDER), 
                        rs.getString("accountNo"), rs.getString("accountHead"), currentBal);
                
                expenseTotal += currentBal;
            }
            
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                        CommonFuncs.tableContentFont, "", "TOTALS",
                        new DecimalFormat("##,##,##0.00").format(Math.abs(expenseTotal)), 
                        new DecimalFormat("##,##,##0.00").format(Math.abs(incomeTotal)));
            
            double balance = incomeTotal + expenseTotal;
            if(balance < 0){
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                        CommonFuncs.tableContentFont, "", "NET PROFIT",
                        "", new DecimalFormat("##,##,##0.00").format(Math.abs(balance)));
            }else{
                addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                            CommonFuncs.tableContentFont, "", "NET LOSS",
                            new DecimalFormat("##,##,##0.00").format(Math.abs(balance)), "");
            }
            
            doc.add(table);
            return balance;
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    private static void balanceSheet(DBConnection con, Document doc, double profitLoss){
        //Liability Calculation
        calculateAssetLiability(con, doc, "LIABILITIES", liabilityCats, -1, profitLoss);
        doc.newPage();
        //Asset Calculation
        calculateAssetLiability(con, doc, "ASSETS", assetCats, 1, profitLoss);
    }
    
    private static double calculateAssetLiability(DBConnection con, Document doc, String rowHeader, String[][] categories, int mul, double profitLoss){
        float columns[] = {1, 2, 1, 1};
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(90);
        addHeaderCell(table, "Code");
        addHeaderCell(table, rowHeader);
        addHeaderCell(table, "Amount");
        addHeaderCell(table, "Total");
        table.setHeaderRows(1);
        
        double amount = 0.0;
        double netTotal = 0.0;
        ResultSet rs;
        String catCode, catName;
        try{
            for( int i = 0; i < categories.length; i++ ){
                double catTotal = 0.0;
                catCode = categories[i][0];
                catName = categories[i][1];
                
                if(!MasterDB.checkCategoryAvailable(con.getStatement(), catCode)){
                    continue;
                }
                
                addTableRow(table, (PdfPCell.NO_BORDER), //print the category name
                            CommonFuncs.tableContentFont, "", catName, "", "");
                
                
                rs = MasterDB.getAccountsByCat(con.getStatement(), catCode);
                while(rs.next()){
                    amount = (mul * rs.getDouble("closingBal"));
                    addTableRow(table, (PdfPCell.NO_BORDER),
                            CommonFuncs.tableContentFont, rs.getString("accountNo"), rs.getString("accountHead"),
                            new DecimalFormat("##,##,##0.00").format(amount), "");
                    
                    netTotal += amount;
                    catTotal += amount;
                }
                
                //print the category total
                addTableRow(table, (PdfPCell.TOP),
                            CommonFuncs.tableContentFont, "", "",
                            "", new DecimalFormat("##,##,##0.00").format(catTotal));
   
            }
            
            //print profit b/f or loss b/f
            if(mul == -1 && profitLoss < 0){ //mul == -1 for Liabilities
                addTableRow(table, (PdfPCell.NO_BORDER),
                        CommonFuncs.tableContentFont, "", "PROFIT C/F TO BALANCE SHEET",
                        "", new DecimalFormat("##,##,##0.00").format(Math.abs(profitLoss)));

                netTotal += Math.abs(profitLoss);
            }
            else if(mul == 1 && profitLoss > 0){ //mul == 1 for Assets
                addTableRow(table, (PdfPCell.NO_BORDER),
                        CommonFuncs.tableContentFont, "", "LOSS C/F TO BALANCE SHEET",
                        "", new DecimalFormat("##,##,##0.00").format(profitLoss));

                netTotal += profitLoss;
            }

            //print total liability or total asset
            addTableRow(table, (PdfPCell.TOP|PdfPCell.BOTTOM),
                        CommonFuncs.tableContentFont, "", "TOTAL",
                        "", new DecimalFormat("##,##,##0.00").format(netTotal));
            
            doc.add(table);
            return netTotal;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return 0.0;
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
            pageCreditTotal = 0;
            pageDebitTotal = 0;
            */
            
            pageNum = pageNum + 1;
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 5, 0);
        }
        
        private void addTitle(PdfContentByte cb, Document document){
            try{
                int base = 10;
                
                Phrase title = new Phrase(stitle, CommonFuncs.titleFont);
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
