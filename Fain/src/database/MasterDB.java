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
public final class MasterDB {
    public static void insert(Statement stmt, String accountNo, String accountHead, double openingBal, double closingBal, String category ){
        String in ="insert into master values('"+accountNo  + "','"
                                                +accountHead+ "',"
                                                +openingBal + ","
                                                +closingBal + ",'"
                                                +category   + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
    
    public static void delete(Statement stmt,String id){
        String del="delete from master where accountNo='"+id+"';";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from master where accountNo='"+id+"';";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select accountNo as 'Account Number', accountHead as 'Account Head', openingBal as 'Opening Balance', closingBal as 'Closing Balance', category as 'Category' from master;";
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
        String sql="select * from master;";
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
        String sql="select * from master where accountNo='"+id+"';";
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
