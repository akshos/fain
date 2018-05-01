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
public final class StockDB {
    public static void insert(Statement stmt, String itemCode,String itemName,int currentStock, double rate, String purchaseAC, String saleAC, String stockAC ){
        String in ="insert into stock values('"      +itemCode      + "','"
                                                     +itemName      + "',"
                                                     +currentStock  + ","
                                                     +rate          + ",'"
                                                     +purchaseAC    + "','"
                                                     +saleAC        + "','"
                                                     +stockAC       + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
    public static void delete(Statement stmt,String id){
        String del="delete from stock where itemCode='"+id+"';";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(StockDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public static boolean checkExisting(Statement stmt,String id){
        String check="select * from stock where itemCode='"+id+"';";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(StockDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select itemCode as 'Item Code', itemName as 'Item Name', currentStock as 'Current Stock', rate as 'Rate', purchaseAC as 'Purchase A/C', saleAC as'SaleA/C', stockAC as 'StockA/C' from stock;";
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
        String sql="select * from stock;";
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
        String sql="select * from stock where itemCode='"+id+"';";
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
    public static String[][] getItems(Statement stmt){
        String sql="select itemCode,itemName from stock;";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    } 
}