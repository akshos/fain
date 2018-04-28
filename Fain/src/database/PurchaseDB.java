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
public final class PurchaseDB {
    public static void insert(Statement stmt, String branch, String date, String billNo,String party,String itemCode,String itemName,double quantity,double value ){
        String in ="insert into purchase values(NULL,'"         +branch         + "','"
                                                                +date           + "','"
                                                                +billNo         + "','"
                                                                +party          + "','"
                                                                +itemCode       + "','"
                                                                +itemName       + "',"
                                                                +quantity       + ","
                                                                +value          + ")";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
}