/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lenovo
 */
public class CategoryDB {
    public static boolean insert(Statement stmt, String code,String name){
        String in ="insert into category values('"      +name   +"','"
                                                        +code           + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
}
        public static String[][] getCategory(Statement stmt){
        String sql="select code,name from category;";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
        
        public static String getCategoryName(Statement stmt, String id){
            String sql = "select name from category where code='"+id+"' ;";
            try{
                ResultSet rs = stmt.executeQuery(sql);
                if(rs.next()){
                    return rs.getString(1);
                }
            }catch(SQLException se){
                se.printStackTrace();
            }
            return null;
        }
}
