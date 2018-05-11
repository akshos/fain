/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import database.DBConnection;
/**
 *
 * @author akshos
 */
public class CommonFuncs {
    private static final Font nameFont = new Font(Font.FontFamily.COURIER, 18);
    private static final Font addressFont = new Font(Font.FontFamily.COURIER, 13);
    
    public static final Font titleFont = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
    public static final Font subTitleFont = new Font(Font.FontFamily.COURIER, 12);
    
    public static final Font accountHeadFont = new Font(Font.FontFamily.COURIER, 12 , Font.BOLD);
    
    public static final Font spacingFont = new Font(Font.FontFamily.COURIER, 12);
    public static final Font tableHeaderFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
    public static final Font tableContentFont = new Font(Font.FontFamily.COURIER, 12);
    public static final Font tableBoldFont = new Font(Font.FontFamily.COURIER, 12 , Font.BOLD);
    
    public static Paragraph alignCenter(String str, Font f){
        Paragraph para = new Paragraph(str, f);
        para.setAlignment(Element.ALIGN_CENTER);
        return para;
    }
    
    public static void addHeader(DBConnection dBConnection, Document doc){
        try{
            Paragraph header = new Paragraph();
            header.add(alignCenter("BEENA RUBBERS", nameFont));
            addEmptyLine(header, 1);
            doc.add(header);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void setDocumentSizeOrientation(Document doc, String paper, String orientation){
        try{
            if(paper.compareTo("A4") == 0){
                if(orientation.compareTo("Portrait") == 0){
                    doc.setPageSize(PageSize.A4);
                }
                else{
                    doc.setPageSize(PageSize.A4.rotate());
                }
            }
            else if(paper.compareTo("Legal") == 0){
                if(orientation.compareTo("Portrait") == 0){
                    doc.setPageSize(PageSize.LEGAL);
                }
                else{
                    doc.setPageSize(PageSize.LEGAL.rotate());
                }
            }
            doc.setMargins(1, 1, 1, 1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void addMetaData(Document doc){
        
    }
    
    public static String generateFileName(String prefix){
        return prefix + ".pdf";
    }
    
    public static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" ", spacingFont));
        }
    }
    
    public static void addEmptyLine(Document doc, int number){
        try{
            Paragraph paragraph = new Paragraph();
            for (int i = 0; i < number; i++) {
                paragraph.add(new Paragraph(" ", spacingFont));
            }
            doc.add(paragraph);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}