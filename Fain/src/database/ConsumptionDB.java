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
public final class ConsumptionDB {
    public static boolean insert(Statement stmt, String branch, String date, String refNo,String itemCode,String itemName,String narration,double quantity){
        String in ="insert into consumption values(NULL,'"      +branch         + "','"
                                                                +date           + "','"
                                                                +refNo          + "','"
                                                                +itemCode       + "','"
                                                                +itemName       + "','"
                                                                +narration      + "',"
                                                                +quantity       + ")";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
            return true;
    }
    public static boolean update(Statement stmt, String code,String branch, String date, String refNo,String itemCode,String itemName,String narration,double quantity){
        String sql = "update consumption set branch='"               +branch         + "',"
                                                +"date='"        +date           + "',"
                                                +"refNo='"       +refNo          + "',"
                                                +"itemCode='"    +itemCode       + "',"
                                                +"itemName='"    +itemName       + "',"
                                                +"narration='"   +narration      + "',"
                                                +"quantity="     +quantity       + " "
                               + "where consumptionId=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }    
public static void delete(Statement stmt,String id){
        String del="delete from consumption where consumptionId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(ConsumptionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from consumption where consumptionId="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsumptionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select c.consumptionId as 'ID', b.name as 'Branch', c.date as 'Date', "
                + "c.refNo as 'Ref. No:', c.itemCode as 'Item Code', c.itemName as 'Item Name', "
                + "c.narration as 'Narration', printf(\"%.3f\", c.quantity) as 'Quantity' from "
                + "consumption as c, branch as b where c.branch=b.branchId;";
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
        String sql="select * from consumption;";
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
        String sql="select * from consumption where consumptionId="+id+";";
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
}