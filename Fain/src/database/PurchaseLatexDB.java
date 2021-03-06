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
    public static boolean update(Statement stmt, String code,String branch, String date, String prBill,String party,double quantity, double drc, double dryRubber, double rate, double value){
        String sql = "update purchaseLatex set branch='"          +branch         + "',"
                                            +"date='"           +date           + "',"
                                            +"prBill='"          +prBill         + "',"
                                            +"party='"           +party          + "',"
                                            +"quantity="        +quantity       + ","
                                            +"drc="                  +drc            + ","
                                            +"dryRubber="        +dryRubber      + ","
                                            +"rate="            +rate           + ","
                                            +"value="            +value          + " "
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
                + "l.prBill as 'Pr. Bill', l.party || ' ' || c.name as 'Party', printf(\"%.3f\", l.quantity) as 'Quantity', "
                + "printf(\"%.3f\", l.drc) as 'DRC', printf(\"%.3f\", l.dryRubber) as 'Dry Rubber', "
                + "printf(\"%.2f\", l.rate) as 'Rate' , printf(\"%.2f\", l.value) as 'Value' from "
                + "purchaseLatex as l, branch as b, customer as c where l.party=c.customerCode and "
                + "l.branch=b.branchId order by cast(l.prBill as INTEGER) asc;";
	
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
        String sqlQuery = "select l.purchaseLatexId as 'ID', b.name as 'Branch', l.date as 'Date', "
                + "l.prBill as 'Pr. Bill', l.party || ' ' || c.name as 'Party', printf(\"%.3f\", l.quantity) as 'Quantity', "
                + "printf(\"%.3f\", l.drc) as 'DRC', printf(\"%.3f\", l.dryRubber) as 'Dry Rubber', "
                + "printf(\"%.2f\", l.rate) as 'Rate' , printf(\"%.2f\", l.value) as 'Value' from "
                + "purchaseLatex as l, branch as b, customer as c where l.party=c.customerCode and "
                + "l.branch=b.branchId and l.date='" + date + "' order by cast(l.prBill as INTEGER) asc;";
	
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
    
    public static TableModel getTableFilteredAccount(Statement stmt, String party){
        String sqlQuery = "select l.purchaseLatexId as 'ID', b.name as 'Branch', l.date as 'Date', "
                + "l.prBill as 'Pr. Bill', l.party || ' ' || c.name as 'Party', printf(\"%.3f\", l.quantity) as 'Quantity', "
                + "printf(\"%.3f\", l.drc) as 'DRC', printf(\"%.3f\", l.dryRubber) as 'Dry Rubber', "
                + "printf(\"%.2f\", l.rate) as 'Rate' , printf(\"%.2f\", l.value) as 'Value' from "
                + "purchaseLatex as l, branch as b, customer as c where l.party=c.customerCode and "
                + "l.branch=b.branchId and l.party='" + party + "' order by cast(l.prBill as INTEGER) asc;";
	
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
    
    public static TableModel getTableFilteredBill(Statement stmt, String bill){
        String sqlQuery = "select l.purchaseLatexId as 'ID', b.name as 'Branch', l.date as 'Date', "
                + "l.prBill as 'Pr. Bill', l.party || ' ' || c.name as 'Party', printf(\"%.3f\", l.quantity) as 'Quantity', "
                + "printf(\"%.3f\", l.drc) as 'DRC', printf(\"%.3f\", l.dryRubber) as 'Dry Rubber', "
                + "printf(\"%.2f\", l.rate) as 'Rate' , printf(\"%.2f\", l.value) as 'Value' from "
                + "purchaseLatex as l, branch as b, customer as c where l.prBill='" + bill + "' and l.party=c.customerCode and "
                + "l.branch=b.branchId order by cast(l.prBill as INTEGER) asc ;";
	
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
    
    public static String getPidFromTid(Statement stmt, String tid){
        String sql = "select purchaseLatexId from purchaseLatex where tid='"+tid+"';";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String pid = rs.getString(1);
            return pid;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String getAccIdFromBillNo(Statement stmt, String billNo){
        String sql = "select party from purchaseLatex where prBill='" + billNo + "' ;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getString(1);
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[] selectOneId(Statement stmt, String id) throws Exception{
        String sql="select purchaseLatexId as 'ID', branch, date as 'Date', "
                + "prBill as 'Pr. Bill', party, printf(\"%.3f\", quantity) as 'Quantity', "
                + "printf(\"%.3f\", drc) as 'DRC', printf(\"%.3f\", dryRubber) as 'Dry Rubber', "
                + "printf(\"%.2f\", rate) as 'Rate' , printf(\"%.2f\", value) as 'Value' from "
                + "purchaseLatex where purchaseLatexId="+id+" ;";
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
    
    public static int checkAccountCodePresent(Statement stmt, String accCode){
        String sql = "select * from purchaseLatex where party='"+accCode+"' ;";
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
        String sql = "select * from purchaseLatex where tid='"+tid+"' ;";
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
    
    public static ResultSet getAllPurchasesParty(Statement stmt, String party){
        String sql = "select purchaseLatexId, date, prBill, party, accountHead, quantity, drc, dryrubber, rate, value "
                + "from purchaseLatex, master where purchaseLatex.party=master.accountNo ";
        if(party.compareTo("All") != 0){
            sql += "and party ='" + party + "' ";
        }
        sql += " order by cast(prBill as INTEGER) asc;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getAllPurchasesPartyDateRange(Statement stmt, String party, String from, String to){
        String sql = "select purchaseLatexId, date, prBill, party, accountHead, quantity, drc, dryrubber, rate, value "
                + "from purchaseLatex, master where purchaseLatex.party=master.accountNo "
                + "and date>='"+from+"' and date<='" + to + "' ";
        if(party.compareTo("All") != 0){
            sql += "and party ='" + party + "' ";
        }
        sql += " order by cast(prBill as INTEGER) asc;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static String[] getPurchaseDatesRange(Statement stmt, String from, String to, String party) throws Exception{
        String sql = "select date from purchaseLatex where party='"+party+"' and date>='"+from+"' and date<='"+to+"' order by cast(prBill as INTEGER) asc;";
        try{
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToStringArray.getStringArray1col(rs);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
    public static ResultSet getPurchasesOnDateForParty(Statement stmt, String date, String party){
        String sql = "select * from purchaseLatex where party='"+party+"' and date='"+date+"' order by cast(prBill as INTEGER) asc ;";
        try{
            return stmt.executeQuery(sql);
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
    
}