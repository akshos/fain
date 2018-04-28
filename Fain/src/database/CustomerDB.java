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
public final class CustomerDB {
    public static void insert(Statement stmt, String customerCode,String name, String address,String branch, String kgst,String rbno ){
        String in ="insert into customer values('"      +customerCode   +"','"
                                                        +name           + "','"
                                                        +address        + "','"
                                                        +branch         +"','"
                                                        +kgst           + "','"
                                                        +rbno           + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
}