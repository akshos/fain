/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;
import database.DBConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import database.MasterDB;
import database.TransactionDB;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author akshos
 */
public class TrailBalance {
    private static String PREFIX = "trialBal";
    
    private static String currAcc;
    private static DBConnection scon;
    private static double pageDebitTotal;
    private static double pageCreditTotal;
    private static int pageNum;
    
    public static void addTitle(DBConnection con, Document doc, String date){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("TRIAL BALANCE", CommonFuncs.titleFont));
            String subTitle = "As on : " + date;
            title.add(CommonFuncs.alignCenter(subTitle, CommonFuncs.subTitleFont));
            doc.add(title);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static String createReport(DBConnection con, String date){
        scon = con;
        currAcc = "";
        pageDebitTotal = 0.0;
        pageCreditTotal = 0.0;
        pageNum = 0;
        
        
        Document doc = new Document();
        try{
            PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            doc.open();
            CommonFuncs.addMetaData(doc);
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static HashMap<String, Account> generateData(DBConnection con, String date){
        HashMap<String, Account> accountData;
        
        String[][] accountStrs = MasterDB.getIdHeadOpBal(con.getStatement());
        accountData = CommonFuncs.addToHashMap(accountStrs);
        if(accountData.isEmpty()){
            return null;
        }
        
        ResultSet rs = TransactionDB.getAllTransactionsRS(con.getStatement());
        String debitAcc, creditAcc;
        Account acc;
        double amount;
        try{
            if(rs.next()){
                do{
                    debitAcc = rs.getString("debit");
                    creditAcc = rs.getString("credit");
                    amount = rs.getDouble("amount");
                    acc = accountData.get(debitAcc);
                    acc.addDebit(amount);
                    acc = accountData.get(creditAcc);
                    acc.addCredit(amount);
                }while(rs.next());
            }else{
                return null;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return null;
        }
        return accountData;
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
