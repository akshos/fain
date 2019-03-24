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
public final class BarrelDB {
    public static boolean insert(Statement stmt, String branch, String customerCode, String date,String stock, String issued, String lifted, String difference ){
        String in ="insert into barrel values(NULL, '"      +branch   + "', '"
                                                            +customerCode  + "', '"
                                                            +date + "', "
                                                            +stock   + ", "
                                                            +issued   + ", "
                                                            +lifted   + ", "
                                                            +difference   + "); ";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
            return true;
    }
    public static boolean update(Statement stmt,String code, String branch, String customerCode, String date,String stock, String issued, String lifted, String difference ){
        String sql = "update barrel set branch='"           +branch   + "',"
                                                +"customerCOde='"+customerCode  + "',"
                                                +"date='"    +date + "',"
                                                +"stock="  +stock   + ","
                                                +"issued="  +issued   + ","
                                                +"lifted="  +lifted   + ","
                                                +"difference="  +difference + " "
                                                + "where barrelId=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }

    public static ResultSet selectAll(Statement stmt) throws SQLException{
        String sql = "select * from barrel;";
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }
    
    public static void delete(Statement stmt,String id){
        String del="delete from barrel where barrelId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(BarrelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from barrel where barrelId="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(BarrelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select barrelId as 'ID', date as 'Date', customerCode as 'CustCode', accountHead as 'Customer', "
                + "stock as 'Stock', issued as 'Issued', lifted as 'Lifted', difference as 'Difference' "
                + "from barrel ,master where customerCode=accountNo order by date asc;";
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
    
    public static TableModel getBarrelSummary(Statement stmt){
        String sql = "select customerCode as 'Cust. Code', accountHead as 'Customer', contact as 'Contact', "
                + "barrels as 'Balance' from customer, master where customerCode=accountNo;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToTableModel.getTableModel(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static TableModel getTableFilteredDate(Statement stmt, String date){
        String sql = "select barrelId as 'ID', date as 'Date', customerCode as 'CustCode', accountHead as 'Customer', "
                + "stock as 'Stock', issued as 'Issued', lifted as 'Lifted', difference as 'Difference' "
                + "from barrel ,master where customerCode=accountNo and date='" + date + "' order by date asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToTableModel.getTableModel(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static TableModel getTableFilteredAccount(Statement stmt, String custCode){
        String sql = "select barrelId as 'ID', date as 'Date', customerCode as 'CustCode', accountHead as 'Customer', "
                + "stock as 'Stock', issued as 'Issued', lifted as 'Lifted', difference as 'Difference' "
                + "from barrel ,master where customerCode=accountNo and customerCode='" + custCode + "' order by date asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToTableModel.getTableModel(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[] selectOneId(Statement stmt, String id) throws Exception{
        String sql="select * from barrel where barrelId="+id+";";
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
    
     public static int getTotalIssued(Statement stmt){
        String sql = "select sum(issued) from barrel;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getInt(1);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return -1;
    }
    
     public static int getTotalLifted(Statement stmt){
         String sql = "select sum(lifted) from barrel;";
         try{
             ResultSet rs = stmt.executeQuery(sql);
             return rs.getInt(1);
         }catch(SQLException se){
             se.printStackTrace();
         }
         return -1;
     }
    
    public static ResultSet getBarrelsBetweenDatesIncl(Statement stmt, String fromdate, String toDate){
        String sql = "select date, customerCode, accountHead, stock, issued, lifted, difference"
                + " from barrel, master where customerCode=accountNo and date>='" + fromdate + "' and date<='" + toDate + "'"
                + "order by date asc;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getBarrelsOnDateRS(Statement stmt, String date){        
        String sql = "select customerCode, accountHead, stock, issued, lifted, difference"
                + " from barrel, master where customerCode=accountNo and date='" + date + "' ;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getBarrelsForCustomerRS(Statement stmt, String custCode){
        String sql = "select date, stock, issued, lifted, difference from barrel "
                + "where customerCode='" + custCode + "' ;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getBarrelsSummaryRS(Statement stmt){
        String sql = "select customerCode, name, contact, barrels from customer, master "
                + "where customerCode=accountNo and category='DB' order by cast(customerCode as INTEGER) asc";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }

}