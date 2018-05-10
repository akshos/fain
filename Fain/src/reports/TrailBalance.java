/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;
import database.DBConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
/**
 *
 * @author akshos
 */
public class TrailBalance {
    private static String PREFIX = "trialBal";
    
    public static void addTitle(Document doc, String branch, String acFrom, String acTo){
        try{
            Paragraph title = new Paragraph();
            title.add(CommonFuncs.alignCenter("TRIAL BALANCE", CommonFuncs.titleFont));
            doc.add(title);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static String createReport(DBConnection con, String branch, String acFrom, String acTo){
        Document doc = new Document();
        try{
            PdfWriter.getInstance(doc, new FileOutputStream(CommonFuncs.generateFileName(PREFIX)));
            doc.open();
            CommonFuncs.addMetaData(doc);
            CommonFuncs.addHeader(con, doc);
            doc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args){
        createReport(null, null, null, null);
    }
}
