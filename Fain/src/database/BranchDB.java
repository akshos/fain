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
public final class BranchDB {
    public static void insert(Statement stmt, String name, String address, String kgst,String rbno ){
        String in ="insert into branch values(NULL,'"      +name   + "','"
                                                            +address  + "','"
                                                            +kgst + "','"
                                                            +rbno   + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
}