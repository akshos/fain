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
        String in ="insert into barrel values(NULL,'"      +branch   + "','"
                                                            +customerCode  + "','"
                                                            +date + "',"
                                                            +stock   + ")"
                                                            +issued   + ")"
                                                            +lifted   + ")"
                                                            +difference   + ")";
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
                                                +"difference="  +difference   + ","
                                        + "where barrelId=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
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
        String sqlQuery = "select barrelId as 'ID', customerCode as 'CustCode', accountHead as 'Customer', "
                + "date as 'Date', stock as 'Stock', issued as 'Issued', lifted as 'Lifted', difference as 'Difference' from branch,master where customerCode=accountCode;";
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
    public static String[] selectOneId(Statement stmt, String id){
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
    public static String[] getBarrelDetails(Statement stmt){
        String sql="select * from barrelDetails where barrelId=1;";
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
    
    public static boolean setCompanyTotal (Statement stmt, String val){
        String sql="update barrelDetails set companyTotal="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(BarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setCompanyShortage (Statement stmt, String val){
        String sql="update barrelDetails set companyShortage="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(BarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setCustomerIssued (Statement stmt, String val){
        String sql="update barrelDetails set customerIssued="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(BarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setLatexBarrel (Statement stmt, String val){
        String sql="update barrelDetails set latexBarrel="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(BarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setEmptyBarrel (Statement stmt, String val){
        String sql="update barrelDetails set emptyBarrel="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(BarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

}