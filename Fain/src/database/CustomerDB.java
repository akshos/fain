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
import utility.Codes;

/**
 *
 * @author lenovo
 */
public final class CustomerDB {
    public static boolean insert(Statement stmt, String code, String name, String address,String branch, String kgst,String rbno,String contact ){
        String in ="insert into customer values('"      +code           + "','"
                                                        +name           + "','"
                                                        +address        + "','"
                                                        +branch         + "','"
                                                        +kgst           + "','"
                                                        +rbno           + "','"
                                                        +contact        + "',0);";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean update(Statement stmt, String code, String name, String address, String branch, String kgst,String rbno,String contact ){
        String sql = "update customer set name='" + name + "', "
                + "address='" + address + "', "
                + "branch='" + branch + "', "
                + "kgst='" + kgst + "', "
                + "rbno='" + rbno + "',"
                + "contact=' "+contact + "'"
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
    
    public static String getCustomerName(Statement stmt, String id){
        String check="select name from customer where customerCode='"+id+"';";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select c.customerCode as 'ID', c.name as 'Name', c.address as 'Address', "
                + "b.name AS 'Branch', c.kgst as 'GST', c.rbno as 'RBNO',c.contact as 'Contact' from customer as c, branch as b "
                + "where c.branch=b.branchId order by customerCode asc;";
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
        String sql="select * from customer order by customerCode asc;";
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
        if(branchId.compareToIgnoreCase("All") != 0){
            sql += " where branch='"+branchId+"' ";
        }else if(branchId.compareToIgnoreCase("None") == 0){
            return null;
        }
        sql += " order by customerCode asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray3col(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[][] getCustomersFilteredCodeBranch(Statement stmt, String branch, String code){
        String sql = "select customerCode, name from customer";
        if(code.compareToIgnoreCase("All") != 0){
            sql += " where customerCode='"+code+"' ";
        }else if(code.compareToIgnoreCase("All") == 0 && branch.compareToIgnoreCase("All") != 0){
            sql += " where branch='"+branch+"' ";
        }
        else if(code.compareToIgnoreCase("None") == 0){
            return null;
        }
        sql += " order by customerCode asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[][] getCustomersFilteredCode(Statement stmt, String code){
        String sql = "select customerCode, name from customer";
        if(code.compareToIgnoreCase("All") != 0){
            sql += " where customerCode='"+code+"' ";
        }
        sql += " order by customerCode asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
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
    
    public static int checkBranchCodePresent(Statement stmt, String branchCode){
        String sql = "select * from customer where branch='"+branchCode+"' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return Codes.EXISTING_ENTRY;
            }else{
                return Codes.NOT_EXISTS;
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return Codes.FAIL;
    }
    
    public static void updateCustomerBarrelBalance(Statement stmt, String accId){
        String sql = "select sum(difference) as 'diff' from barrel where customerCode='" + accId + "'; ";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            int balance = 0;
            if(rs.next())
                balance = rs.getInt(1);
            sql = "update customer set barrels=" + balance + " where cusotmerCode='" + accId + "' ;";
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
}