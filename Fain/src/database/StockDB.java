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
    public static boolean insert(Statement stmt, String itemName,double currentStock, double rate, String purchaseAC, String saleAC, String stockAC ){
        String in ="insert into stock values(NULL,'" +itemName      + "',"
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
            return false;
        }
            return true;
    }
    public static boolean update(Statement stmt, String code,String itemName,double currentStock, double rate, String purchaseAC, String saleAC, String stockAC){
        String sql = "update stock set itemName     ='"+itemName      + "',"
                                     +"currentStock="  +currentStock  + ","
                                     +"rate="          +rate          + ","
                                     +"purchaseAC='"   +purchaseAC    + "',"
                                     +"saleAC='"       +saleAC        + "',"
                                     +"stockAC='"      +stockAC       + "'"
                               + "where itemCode=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }     
    public static void delete(Statement stmt,String id){
        String del="delete from stock where itemCode="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(StockDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static double getRateByName(Statement stmt, String name){
        String sql = "select rate from stock where itemName='"+name+"' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getDouble(1);
            }else{
                return 0.0;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return 0.0;
        }
    }
    
    public static double getCurrentStockByName(Statement stmt, String name){
        String sql = "select currentStock from stock where itemName='"+name+"' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getDouble(1);
            }else{
                return 0.0;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return 0.0;
        }
    }
    
    public static boolean setCurrentStockByName(Statement stmt, double amt, String name){
        String sql = "update stock set currentStock=" + amt + " where itemName='" + name + "' ;";
        try{
            stmt.executeUpdate(sql);
            return true;
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from stock where itemCode="+id+";";
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
        String sqlQuery = "select itemCode as 'Item Code', itemName as 'Item Name', "
                + "printf(\"%.3f\", currentStock) as 'Current Stock', "
                + "printf(\"%.2f\", rate) as 'Rate', m1.accountHead as 'Purchase A/C', "
                + "m2. accountHead as 'Sales A/C',m3.accountHead as 'Stock A/C' "
                + "from stock, master m1, master m2, master m3 where purchaseAC=m1.accountNo "
                + "and saleAC=m2.accountNo and stockAC=m3.accountNo;";
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
    
    public static String[] selectOneId(Statement stmt, String id){
        String sql="select * from stock where itemCode="+id+";";
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
    
    public static String[][] getItems(Statement stmt){
        String sql="select itemCode, itemName from stock;";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getNameFromCode(Statement stmt, String code){
        String sql = "select itemName from stock where itemCode="+code+";";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString(1);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return "";
    }
    
    public static String getPurchaseAccount(Statement stmt, String itemCode){
        String sql = "select purchaseAC from stock where itemCode="+itemCode+";";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString(1);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String getLatexPurchaseAccount(Statement stmt){
        String sql = "select purchaseAC from stock where itemName='Latex';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if( rs.next() )
                return rs.getString(1);
            else
                return "none";
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String getStockAccByName(Statement stmt, String name){
        String sql = "select stockAC from stock where itemName='"+name+"' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getString(1);
            }else{
                return null;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return null;
        }
    }
}