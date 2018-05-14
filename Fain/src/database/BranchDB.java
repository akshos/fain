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
public final class BranchDB {
    public static boolean insert(Statement stmt, String name, String address, String kgst,String rbno ){
        String in ="insert into branch values(NULL,'"      +name   + "','"
                                                            +address  + "','"
                                                            +kgst + "','"
                                                            +rbno   + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
            return true;
    }
    public static boolean update(Statement stmt, String code,String name, String address, String kgst,String rbno ){
        String sql = "update branch set name='"           +name   + "',"
                                                +"address='"+address  + "',"
                                                +"kgst='"    +kgst + "',"
                                                +"rbno='"  +rbno   + "'"
                                        + "where branchId=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }    
    public static void delete(Statement stmt,String id){
        String del="delete from branch where branchId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(BranchDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from branch where branchId="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(BranchDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select branchId as 'ID', name as 'Name', address as 'Address', kgst as 'GST', rbno as 'RBNO' from branch;";
	TableModel table = null;
        ResultSet rs = null;
	try{
            rs = stmt.executeQuery(sqlQuery);
            table = ResultSetToTableModel.getTableModel(rs);
	}catch( SQLException se ){
            se.printStackTrace();
	}
	return table;
    }
    
    public static ResultSet selectAll(Statement stmt){
        String sql="select * from branch;";
        ResultSet rs = null;
        try{
            rs=stmt.executeQuery(sql);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
        return rs;
    }
    
    public static String[] selectOneId(Statement stmt, String id){
        String sql="select * from branch where branchId="+id+";";
        ResultSet rs=null;
        try{
            rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getRowAsStringArray(rs);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[][] getBranch(Statement stmt){
        String sql="select branchId,name from branch;";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getBranchName(Statement stmt, String id){
        String sql = "select name from branch where branchId='"+id+"';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String name = rs.getString(1);
            return name;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
}