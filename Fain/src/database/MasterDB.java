/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

/**
 *
 * @author lenovo
 */
public final class MasterDB {
    public static boolean insert(Statement stmt, String accountNo, String accountHead, double openingBal, double closingBal, String category ){
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
            return false;
        }
        return true;       
    }
    
    public static boolean update(Statement stmt, String code,String accountHead, double openingBal, double closingBal, String category){
        String sql = "update master set accountHead='"+accountHead+ "',"
                                                +"openingBal=" +openingBal + ","
                                                +"closingBal=" +closingBal + ","
                                                +"category='"  +category   + "'"
                               + "where accountNo='" + code + "';";
        try{
            stmt.execute(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean modifyId(Statement stmt, String prevId, String newId){
        String sql = "update master set accountNo='"+newId+"' where accountNo='"+prevId+"';";
        try{
            stmt.executeUpdate(sql);  
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
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
        String sqlQuery = "select accountNo as 'Account Number', accountHead as 'Account Head', "
                + "printf(\"%.2f\", openingBal) as 'Opening Balance', "
                + "printf(\"%.2f\", closingBal) as 'Current Balance', "
                + "category as 'Category' from master order by accountNo asc;";
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
    
    public static String[] selectOneId(Statement stmt, String id){
        String sql="select * from master where accountNo='"+id+"';";
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
    
    public static String[][] getAccountHead(Statement stmt){
        String sql="select accountNo,accountHead from master order by accountNo asc;";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static double getOpeningBalance(Statement stmt, String id){
        String sql = "select openingBal from master where accountNo='"+id+"'; ";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getDouble(1);
            }
        }catch(SQLException se){
            se.printStackTrace();  
        }
        return 0.0;
    }
    
    public static double getClosingBalance(Statement stmt, String id){
        String sql = "select closingBal from master where accountNo='"+id+"'; ";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getDouble(1);
            }
        }catch(SQLException se){
            se.printStackTrace();  
        }
        return 0.0;
    }
    
    public static boolean setClosingBalance(Statement stmt, String id, String closingBal){
        String sql = "update master set closingBal="+closingBal+" where accountNo='"+id+"' ;";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static HashMap<String, String> getAccountHeadHashMap(Statement stmt){
        String sql="select accountNo, accountHead from master order by accountNo asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToHashMap.getHashMap(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[][] getAccountHeadByCat(Statement stmt, String cat){
        String sql="select accountNo,accountHead from master where category='" + cat +"' ;";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String[][] getPurchaseAC(Statement stmt){
        String sql="select accountNo,accountHead from master where category='PR';";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static String[][] getSalesAC(Statement stmt){
        String sql="select accountNo,accountHead from master where category='SL';";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static String[][] getStockAC(Statement stmt){
        String sql="select accountNo,accountHead from master where category='SK';";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    // doubtful need to confirm
    public static String[][] getParty(Statement stmt){
        String sql="select accountNo,accountHead from master where category='CR';";
        try {
            ResultSet rs=stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray2col(rs);
            
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    } 
    
    public static String getOpeningBal(Statement stmt, String accId){
        String sql = "select openingBal from master where accountNo='"+accId+"';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String bal = rs.getString(1);
            return bal;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return "0";
    }
    
    public static String getAccountHead(Statement stmt, String accId){
        String sql = "select accountHead from master where accountNo='"+accId+"';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String head = rs.getString(1);
            return head;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[][] getIdHeadOpBal(Statement stmt){
       String sql = "select accountNo, accountHead,  openingBal from master";
       try{
           ResultSet rs = stmt.executeQuery(sql);
           return ResultSetToStringArray.getStringArray3col(rs);
       }catch(SQLException se){
           se.printStackTrace();
       }
       return null;
    }
    
    public static String getAccountIdByCat(Statement stmt, String cat){
        String sql = "select accountNo from master where category='"+cat+"';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getString(1);
            }else{
                return null;
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
}
