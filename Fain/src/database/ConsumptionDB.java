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
public final class ConsumptionDB {
    public static void insert(Statement stmt, String branch, String date, String refNo,String itemCode,String itemName,String narration,double quantity){
        String in ="insert into consumption values(NULL,'"      +branch         + "','"
                                                                +date           + "','"
                                                                +refNo          + "','"
                                                                +itemCode       + "','"
                                                                +itemName       + "','"
                                                                +narration      + "',"
                                                                +quantity       + ")";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
}