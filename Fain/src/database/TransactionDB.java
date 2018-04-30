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
public final class TransactionDB {
    public static void insert(Statement stmt, String date, String branch, String debit, String credit, double amount, String narration ){
        String in ="insert into transactions values(NULL,'"      +date   + "','"
                                                            +branch  + "','"
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
    public static void delete(Statement stmt,String id){
        String del="delete from transactions where transactionID="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from transactions where transactionId="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select transactionID as 'Transaction ID', date as 'Date', debit as 'Debit', credit as 'Credit', amount as 'Amount', narration as 'Narration' from transactions;";
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
        String sql="select * from transactions;";
        ResultSet rs = null;
        try{
            rs=stmt.executeQuery(sql);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
        return rs;
    }
    
    public static ResultSet selectOneId(Statement stmt, String id){
        String sql="select * from transactions where transactionID="+id+";";
        ResultSet rs=null;
        ResultSet rs1=null;
        try{
            rs=stmt.executeQuery(sql);
            rs1=rs;
        }
        catch(SQLException se){
            se.printStackTrace();
        }
        return rs1;
    }
}