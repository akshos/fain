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
import utility.Codes;

/**
 *
 * @author lenovo
 */
public final class TransactionDB {
    public static boolean insert(Statement stmt, String date, String branch, String debit, String credit, double amount, String narration, String tid){
        String in ="insert into transactions values(NULL,'" +date       + "','"
                                                            +branch     + "','"
                                                            +debit      + "','"
                                                            +credit     + "',"
                                                            +amount     + ",'"
                                                            +narration  + "','"
                                                            +tid        +"')";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
            
    }
    
    public static boolean update(Statement stmt, String code,String date, String branch, String debit, String credit, double amount, String narration, String tid){
        String sql = "update transactions set date='"       +date       + "',"
                                           +"branch='"      +branch     + "',"
                                           +"debit='"       +debit      + "',"
                                           +"credit='"      +credit     + "',"
                                           +"amount="       +amount     + ","
                                           +"narration='"   +narration  + "',"
                                           +"tid='"         +tid        +"'"
                               + "where transactionNo=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void delete(Statement stmt,String tNo){
        String del="delete from transactions where transactionNo="+tNo+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExistingTNo(Statement stmt,String tNo){
        String check="select * from transactions where transactionNo="+tNo+";";
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
    
    public static boolean checkExistingTid(Statement stmt,String tid){
        String check="select * from transactions where tid='"+tid+"';";
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
        String sqlQuery = "select t.transactionNo as 'Transaction No', t.date as 'Date', "
                + "b.name as 'Branch', mdebit.accountHead as 'Debit', mcredit.accountHead as 'Credit', "
                + "printf(\"%.2f\", t.amount) as 'Amount', t.narration as 'Narration' from transactions as t, "
                + "branch as b, master as mdebit, master mcredit "
                + "where t.branch=b.branchId and t.credit=mcredit.accountNo and t.debit=mdebit.accountNo;";
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
    
    public static String[] selectOneId(Statement stmt, String id){
        String sql="select * from transactions where transactionNo="+id+";";
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
    
    public static String getTidFromTNo(Statement stmt, String tNo){
        String sql = "select tid from transactions where transactionNo="+tNo+";";
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
    
    public static String generateTid(){
        java.util.UUID uuid = java.util.UUID.randomUUID();
        String id = uuid.toString().substring(0, 20);
        return id;
    }
    
    public static ResultSet getContainingAccount(Statement stmt, String accId){
        String sql = "select * from transactions where debit='"+accId+"' or credit='"+accId+"' ;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getTransactionsBeforeInclDateRS(Statement stmt, String date){
        String sql = "select * from transactions where date<='"+date+"';";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getTransactionsBeforeDateRS(Statement stmt, String date){
        String sql = "select * from transactions where date<'"+date+"' order by date asc;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getTransactionsBetweenInclDateRS(Statement stmt, String from, String to){
        String sql = "select * from transactions where date>='"+from+"' and date<='"+to+"' order by date asc;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static int checkAccountIdPresent(Statement stmt, String id){
        try{
            ResultSet rs = getContainingAccount(stmt, id);
            if(rs.next()){
                return Codes.EXISTING_ENTRY;
            }else{
                return Codes.NOT_EXISTS;
            }
        }catch(SQLException se){
            se.printStackTrace();
            
        }
        return Codes.FAIL;
    }
    
    public static String[] getTrasnsationDatesBetweenIncDatesIdRS(Statement stmt, String from, String to, String id){
        String sql = "select distinct date from transactions where date>='"+from+"' and date<='"+to+"' and "
                + "(credit='"+id+"' or debit='"+id+"') order by date asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray1col(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getNetCreditsOnDateForId(Statement stmt, String date, String creditId){
        String sql = "select date, narration, debit, sum(amount) as amount from transactions where credit='"+creditId+"' group by debit;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getNetDebitsOnDateForId(Statement stmt, String date, String debitId){
        String sql = "select date, narration, credit, sum(amount) as amount from transactions where debit='"+debitId+"' group by credit;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
 
}