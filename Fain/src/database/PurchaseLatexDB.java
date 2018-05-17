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
public final class PurchaseLatexDB {
    public static boolean insert(Statement stmt, String branch, String date, String prBill,String party,double quantity, double drc, double dryRubber, double rate, double value, String tid ){
        String in ="insert into purchaseLatex values(NULL,'"    +branch         + "','"
                                                                +date           + "','"
                                                                +prBill         + "','"
                                                                +party          + "',"
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
    public static boolean update(Statement stmt, String code,String branch, String date, String prBill,String party,double quantity, double drc, double dryRubber, double rate, double value, String tid){
        String sql = "update purchaseLatex set branch='"          +branch         + "',"
                                            +"date='"           +date           + "',"
                                            +"prBill='"          +prBill         + "',"
                                            +"party='"           +party          + "',"
                                            +"quantity="        +quantity       + ","
                                            +"drc="                  +drc            + ","
                                            +"dryRubber="        +dryRubber      + ","
                                            +"rate="            +rate           + ","
                                            +"value="            +value          + ","
                                            +"tid='"            +tid            + "'"
                               + "where purchaseLatexId=" + code + ";";
        try{
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void delete(Statement stmt,String id){
        String del="delete from purchaseLatex where purchaseLatexId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseLatexDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean checkExisting(Statement stmt,String id){
        String check="select * from purchaseLatex where purchaseLatexId="+id+";";
        try {
            ResultSet rs=stmt.executeQuery(check);
            if (rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseLatexDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean checkExistingBillNo(Statement stmt, String bill){
        String check = "select * from purchaseLatex where prBill='"+bill+"';";
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
        String sqlQuery = "select l.purchaseLatexId as 'ID', b.name as 'Branch', l.date as 'Date', "
                + "l.prBill as 'Pr. Bill', c.name as 'Party', printf(\"%.3f\", l.quantity) as 'Quantity', "
                + "printf(\"%.3f\", l.drc) as 'DRC', printf(\"%.3f\", l.dryRubber) as 'Dry Rubber', "
                + "printf(\"%.2f\", l.rate) as 'Rate' , printf(\"%.2f\", l.value) as 'Value' from "
                + "purchaseLatex as l, branch as b, customer as c where l.party=c.customerCode and "
                + "l.branch=b.branchId;";
	
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
        String sql="select * from purchaseLatex;";
        ResultSet rs = null;
        try{
            rs=stmt.executeQuery(sql);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
        return rs;
    }
    
    public static String getTidFromPid(Statement stmt, String pid){
        String sql = "select tid from purchaseLatex where purchaseLatexId="+pid+";";
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
    
    public static String[] selectOneId(Statement stmt, String id){
        String sql="select * from purchaseLatex where purchaseLatexId="+id+";";
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
    
    public static double getTotalPurchaseQuantity(Statement stmt, String date){
        String sql = "select sum(quantity) from purchaseLatex where date<='"+date+"' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                double sum = rs.getDouble(1);
                return sum;
            }else{
                return 0.0;
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return 0.0;
    }
}