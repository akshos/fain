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
    public static boolean insert(Statement stmt, String code, String name, String address,String branch, String kgst,String rbno ){
        String in ="insert into customer values('"      +code           +"','"
                                                        +name           + "','"
                                                        +address        + "','"
                                                        +branch       +"','"
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
    
    public static boolean update(Statement stmt, String code, String name, String address, String branch, String kgst,String rbno ){
        String sql = "update customer set name='" + name + "', "
                + "address='" + address + "', "
                + "branch='" + branch + "', "
                + "kgst='" + kgst + "', "
                + "rbno='" + rbno + "' "
                + "where customerCode='" + code + "';";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean modifyName(Statement stmt, String code, String name){
        String sql = "update customer set name='"+name+"' where customerCode='"+code+"'";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean modifyId(Statement stmt, String prevId, String newId){
        String sql = "update customer set customerCode='"+newId+"' where customerCode='"+prevId+"';";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
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
        String sqlQuery = "select c.customerCode as 'ID', c.name as 'Name', c.address as 'Address', "
                + "b.name AS 'Branch', c.kgst as 'GST', c.rbno as 'RBNO' from customer as c, branch as b "
                + "where c.branch=b.branchId;";
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
        String sql = "select customerCode, name, address from customer ";
        if(branchId.compareToIgnoreCase("All") != 0){
            sql += " where branch='"+branchId+"' ";
        }
        sql += "order by name asc;";
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
            if( rs.next() ){
                String branch = rs.getString(1);
                return branch;
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
}