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
public final class TransactionDB {
    public static void insert(Statement stmt, String date, String debit, String credit, double amount, String narration ){
        String in ="insert into transactions values(NULL,'"      +date   + "','"
                                                            +debit  + "','"
                                                            +credit + "',"
                                                            +amount + ",'"
                                                            +narration   + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
}