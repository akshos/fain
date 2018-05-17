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
public final class SalesDB {
    public static boolean insert(Statement stmt, String branch, String date, String billNo,String party,int barrelNoFrom,int barrelNoTo,double quantity,double drc,double dryRubber,double rate,double value, String tid ){
        int diff=barrelNoTo-barrelNoFrom+1;
        String in ="insert into sales values(NULL,'"            +branch         + "','"
                                                                +date           + "','"
                                                                +billNo         + "','"
                                                                +party          + "',"
                                                                +barrelNoFrom   + ","
                                                                +barrelNoTo     + ","
                                                                +diff           + ","
                                                                +quantity       + ","
                                                                +drc            + ","
                                                                +dryRubber      + ","
                                                                +rate           + ","
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
    public static boolean update(Statement stmt,String code, String branch, String date, String billNo,String party,int barrelNoFrom,int barrelNoTo,double quantity,double drc,double dryRubber,double rate,double value, String tid ){
        int diff=barrelNoTo-barrelNoFrom+1;
        String sql = "update sales set branch='"          +branch         + "',"
                                            +"date='"           +date           + "',"
                                            +"billNo='"          +billNo         + "',"
                                            +"party='"           +party          + "',"
                                            +"barrelNoFrom="     +barrelNoFrom   + ","
                                            +"barrelNoTo="       +barrelNoTo     + ","
                                            +"diff="            +diff           + ","
                                            +"quantity="        +quantity       + ","
                                            +"drc="                  +drc            + ","
                                            +"dryRubber="        +dryRubber      + ","
                                            +"rate="            +rate           + ","
                                            +"value="            +value          + ","
                                            +"tid='"            +tid            + "'"
                               + "where salesId=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    } 
    public static void delete(Statement stmt,String id){
        String del="delete from sales where salesId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(SalesDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from sales where Id="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select s.salesId as 'ID', b.name as 'Branch', s.date as 'Date', "
                + "s.billNo as 'Bill No:', c.name as 'Party',s.barrelNoFrom as 'Barrel # From', "
                + "s.barrelNoTo as 'Barrel # To', s.diff as 'Nos:', "
                + "printf(\"%.3f\", s.quantity) as 'Quantity', printf(\"%.3f\", s.drc) as 'DRC', "
                + "printf(\"%.3f\", s.dryRubber) as 'Dry Rubber', printf(\"%.2f\", s.rate) as 'Rate', "
                + "printf(\"%.2f\", s.value) as 'Value' from sales as s, branch as b, customer as c where "
                + "s.branch=b.branchId and s.party=c.customerCode;";
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
    
    public static String getTidFromSid(Statement stmt, String sid){
        String sql = "select tid from sales where salesId="+sid+";";
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
    
    public static ResultSet selectAll(Statement stmt){
        String sql="select * from sales;";
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
        String sql="select * from sales where salesId="+id+";";
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
    
    public static boolean checkExistingBillNo(Statement stmt, String billNo){
        String check = "select * from sales where billNo='"+billNo+"';";
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
    
    public static double getTotalSaleQuantity(Statement stmt, String date){
       String sql = "select sum(quantity) from sales where date<='"+date+"' ;";
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
    
    public static int checkAccountCodePresent(Statement stmt, String accCode){
        String sql = "select * from sales where party='"+accCode+"' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return Codes.EXISTING_ENTRY;
            }else{
                return Codes.NOT_EXISTS;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return Codes.FAIL;
        }
    }
    
    public static int checkTidCodePresent(Statement stmt, String tid){
        String sql = "select * from sales where tid='"+tid+"' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return Codes.EXISTING_ENTRY;
            }else{
                return Codes.NOT_EXISTS;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return Codes.FAIL;
        }
    }
}