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
    public static void insert(Statement stmt, String branch, String date, String prBill,String party,double quantity, double drc, double dryRubber, double rate, double value ){
        String in ="insert into purchaseLatex values(NULL,'"     +branch         + "','"
                                                                +date           + "','"
                                                                +prBill         + "','"
                                                                +party          + "',"
                                                                +quantity       + ","
                                                                +drc            + ","
                                                                +dryRubber      + ","
                                                                +rate           + ","
                                                                +value          + ")";
        try{
            stmt.execute(in);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
            
    }
    public static void delete(Statement stmt,String id){
        String del="delete from purchaseLatex where purchaseLatexId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select purchaseLatexId as 'ID', branch as 'Branch', date as 'Date', prBill as 'Pr. Bill', party as 'Party', quantity as 'Quantity', drc as 'DRC', dryRubber as 'Dry Rubber', rate as 'Rate' , value as 'Value' from purchaseLatex;";
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
    
    public static ResultSet selectOneId(Statement stmt, String id){
        String sql="select * from purchaseLatex where purchaseLatexId="+id+";";
        ResultSet rs=null;
        ResultSet rs1=null;
        try{
            rs=stmt.executeQuery(sql);
            rs1=rs;
        }
        catch(SQLException se){
            se.printStackTrace();
        }
        return rs1;
    }
}