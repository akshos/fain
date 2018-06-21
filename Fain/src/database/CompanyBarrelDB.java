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
public final class CompanyBarrelDB {
    public static boolean insert(Statement stmt, String date, String issued, String lifted, String difference ){
        String in ="insert into companyBarrel values(NULL, '"   +date       + "', "
                                                                +issued     + " , "
                                                                +lifted     + " , "
                                                                +difference + "); ";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
            return true;
    }
    public static boolean update(Statement stmt,String code, String date, String issued, String lifted, String difference ){
        String sql = "update companyBarrel set date='"    +date + "',"
                                                +"issued="  +issued   + ","
                                                +"lifted="  +lifted   + ","
                                                +"difference="  +difference + " "
                                                + "where cbarrelId=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }    
    public static void delete(Statement stmt,String id){
        String del="delete from companyBarrel where cbarrelId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyBarrelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from companyBarrel where cbarrelId="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyBarrelDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select cbarrelId as 'ID', date as 'Date',  "
                + "issued as 'Issued', lifted as 'Lifted', difference as 'Difference' "
                + "from companyBarrel order by date asc;";
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
    
    public static TableModel getTableFilteredDate(Statement stmt, String date){
        String sql = "select cbarrelId as 'ID', date as 'Date', "
                + "issued as 'Issued', lifted as 'Lifted', difference as 'Difference' "
                + "from companyBarrel date='" + date + "' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToTableModel.getTableModel(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[] selectOneId(Statement stmt, String id){
        String sql="select * from companyBarrel where cbarrelId="+id+";";
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
        String sql = "select sum(issued) from companyBarrel;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getInt(1);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return -1;
    }
    
     public static int getTotalLifted(Statement stmt){
         String sql = "select sum(lifted) from companyBarrel;";
         try{
             ResultSet rs = stmt.executeQuery(sql);
             return rs.getInt(1);
         }catch(SQLException se){
             se.printStackTrace();
         }
         return -1;
     }
     
    public static String[] getBarrelDetails(Statement stmt){
        String sql="select * from barrelDetails where barrelDetailsId=1;";
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
    
    public static int getCompanyOpStock(Statement stmt){
        String sql = "select companyOpStock from barrelDetails where barrelDetailsId=1; ";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getInt(1);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return -1;
    }
    
    public static int getCompanyShortage(Statement stmt){
        String sql = "select companyShortage from barrelDetails where barrelDetailsId=1; ";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getInt(1);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return -1;
    }
    
    public static boolean setCompanyOpStock (Statement stmt, String val){
        String sql="update barrelDetails set companyOpStock="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyBarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setCompanyShortage (Statement stmt, String val){
        String sql="update barrelDetails set companyShortage="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyBarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setCustomerIssued (Statement stmt, String val){
        String sql="update barrelDetails set customerIssued="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyBarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setLatexBarrel (Statement stmt, String val){
        String sql="update barrelDetails set latexBarrel="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyBarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean setEmptyBarrel (Statement stmt, String val){
        String sql="update barrelDetails set emptyBarrel="+val+" where barrelDetailsId=1;";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyBarrelDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static ResultSet getCompanyBarrelsBetweenDateRS(Statement stmt, String fromDate, String toDate){
        String sql = "select issued, lifted, difference"
                + " from companyBarrel where date<='" + toDate + "' and date>='"+fromDate+"' ;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }

}