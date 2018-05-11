/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lenovo
 */
public class ViewPdf {
    
    public static void viewPdf(String filePath){
        try {
            Process exec;
            exec = Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler "+filePath );
        } catch (IOException ex) {
            Logger.getLogger(ViewPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    /*
    public static void main(String args[]){
        viewPdf("C:\\Users\\lenovo\\Documents\\Projects\\fain\\Fain\\ledger.pdf");
    }
    */
    
}
