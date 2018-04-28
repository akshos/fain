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
public final class StockDB {
    public static void insert(Statement stmt, String itemCode,String itemName,int currentStock, double rate, String purchaseAC, String saleAC, String stockAC ){
        String in ="insert into stock values('"      +itemCode      + "','"
                                                     +itemName      + "',"
                                                     +currentStock  + ","
                                                     +rate          + ",'"
                                                     +purchaseAC    + "','"
                                                     +saleAC        + "','"
                                                     +stockAC       + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
}