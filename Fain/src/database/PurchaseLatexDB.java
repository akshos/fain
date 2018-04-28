/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author lenovo
 */
public final class PurchaseLatexDB {
    public static void insert(Statement stmt, String branch, String date, String credit, String prBill,String party,double quantity, double drc, double dryRubber, double rate, double value ){
        String in ="insert into purchaseLatex values(NULL,'"     +branch         + "','"
                                                                +date           + "','"
                                                                +prBill         + "','"
                                                                +party          + "',"
                                                                +quantity       + ","
                                                                +drc            + ","
                                                                +dryRubber      + ","
                                                                +rate           + ","
                                                                +value          + ")";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
}