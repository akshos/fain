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
import javax.swing.table.TableModel;
/**
 *
 * @author lenovo
 */
public class SessionInfoDB {
    public static boolean insert(Statement stmt, String name, String address, String gst,String rbno, String phone1, String phone2 ){
        String in ="insert into info values(NULL,'"      +name   + "','"
                                                            +address  + "','"
                                                            +gst + "','"
                                                            +rbno   + "','"
                                                            +phone1 +"','"
                                                            +phone2 +"')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
            return true;
    }
    public static boolean update(Statement stmt, String code,String name, String address, String gst,String rbno, String phone1, String phone2 ){
        String sql = "update info set name='"           +name   + "',"
                                                +"address='"+address  + "',"
                                                +"kgst='"    +gst + "',"
                                                +"rbno='"  +rbno   + "',"
                                                +"phone1='"+phone1+"',"
                                                +"phone2='"+phone2+"' "
                                        + "where id=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }    
    public static void delete(Statement stmt,String id){
        String del="delete from info where id="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(BranchDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static String[] getDetails(Statement stmt){
        String sql="select * from info";
        ResultSet rs;
        try{
            rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getRowAsStringArray(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
}
