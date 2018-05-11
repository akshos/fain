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
public final class CustomerDB {
    public static boolean insert(Statement stmt, String code, String name, String address,String customer, String kgst,String rbno ){
        String in ="insert into customer values('"      +code           +"','"
                                                        +name           + "','"
                                                        +address        + "','"
                                                        +customer       +"','"
                                                        +kgst           + "','"
                                                        +rbno           + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
            return true;
    }
    public static void delete(Statement stmt,String id){
        String del="delete from customer where customerCode='"+id+"';";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        public static boolean checkExisting(Statement stmt,String id){
        String check="select * from customer where customerCode='"+id+"';";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select customerCode as 'ID', name as 'Name', address as 'Address',branch AS 'Branch', kgst as 'GST', rbno as 'RBNO' from customer;";
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
        String sql="select * from customer;";
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
        String sql="select * from customer where customerCode='"+id+"';";
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
    
    public static String[][] getCustomersInBranch(Statement stmt, String branchId){
        String sql = "select customerCode, name, address from customer";
        if(branchId.compareTo("All") == 0) sql += ";";
        else sql += " where branch='"+branchId+"';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray3col(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String getBranch(Statement stmt, String id){
        String sql = "select branch from customer where customerCode='"+id+"';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String branch = rs.getString(1);
            return branch;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
}