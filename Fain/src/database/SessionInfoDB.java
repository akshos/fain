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
    public static String sessionDetails[] = null;
    
    public static boolean insert(Statement stmt, String name, String address, String gst,String rbno, String phone1, String phone2 ){
        String in ="insert into info values(1, '"         +name   + "','"
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
    
    public static boolean update(Statement stmt,String name, String address, String gst,String rbno, String phone1, String phone2 ){
        String sql = "update info set name='"           +name   + "',"
                                                +"address='"+address  + "',"
                                                +"gst='"    +gst + "',"
                                                +"rbreg='"  +rbno   + "',"
                                                +"phone1='"+phone1+"',"
                                                +"phone2='"+phone2+"' "
                                        + "where id=1 ;";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean checkExisting(Statement stmt, String id) throws SQLException{
        String sql = "select * from info where id=" + id + ";";
        ResultSet rs = stmt.executeQuery(sql);
        if(rs.next()) {
            return true;
        }
        return false;
    }
    
    public static ResultSet selectAll(Statement stmt) throws SQLException {
        String sql = "select * from info where id=1";
        return stmt.executeQuery(sql);
    }
    
    public static void delete(Statement stmt,String id){
        String del="delete from info where id="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(BranchDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String[] getDetails(Statement stmt) throws Exception{
        String sql="select * from info where id=1";
        ResultSet rs;
        try{
            rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getRowAsStringArray(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static boolean loadSessionDetails(Statement stmt) throws Exception{
        String sql="select * from info where id=1";
        ResultSet rs;
        try{
            rs=stmt.executeQuery(sql);
            sessionDetails = ResultSetToStringArray.getRowAsStringArray(rs);
            return true;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return false;
    }
}
