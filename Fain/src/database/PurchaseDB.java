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
public final class PurchaseDB {
    public static boolean insert(Statement stmt, String branch, String date, String billNo, String party, String itemCode, String itemName, double quantity, double value, String tid ){
        String in ="insert into purchase values(NULL,'"         +branch         + "','"
                                                                +date           + "','"
                                                                +billNo         + "','"
                                                                +party          + "','"
                                                                +itemCode       + "','"
                                                                +itemName       + "',"
                                                                +quantity       + ","
                                                                +value          + ",'"
                                                                +tid            + "')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean update(Statement stmt, String code,String branch, String date, String billNo, String party, String itemCode, String itemName, double quantity, double value, String tid ){
        String sql = "update purchase set branch='"          +branch         + "',"
                                            +"date='"           +date           + "',"
                                            +"billNo='"          +billNo         + "',"
                                            +"party='"           +party          + "',"
                                            +"itemCode='"        +itemCode       + "',"
                                            +"itemName='"        +itemName       + "',"
                                            +"quantity="        +quantity       + ","
                                            +"value="            +value          + ","
                                            +"tid='"            +tid            + "'"
                               + "where purchaseId=" + code + ";";
        try{
            stmt.execute(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }       
    public static void delete(Statement stmt,String id){
        String del="delete from purchase where purchaseId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from purchase where purchaseId="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean checkExistingBillNo(Statement stmt, String bill){
        String check = "select * from purchase where billNo='"+bill+"';";
        try{
            ResultSet rs = stmt.executeQuery(check);
            if(rs.next()){
                return true;
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return false;
    }
    
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select purchaseId as 'ID', branch as 'Branch', date as 'Date', billNo as 'Bill No:', party as 'Party', itemCode as 'Item Code', itemName as 'Item Name', quantity as 'Quantity',  value as 'Value' from purchase;";
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
        String sql="select * from purchase;";
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
        String sql="select * from purchase where purchaseId="+id+";";
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
    
    public static String getTidFromPid(Statement stmt, String pid){
        String sql = "select tid from purchase where purchaseId="+pid+";";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String tid = rs.getString(1);
            return tid;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
}