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
public final class SalesDB {
    public static void insert(Statement stmt, String branch, String date, String billNo,String party,int barrelNoFrom,int barrelNoTo,double quantity,double drc,double dryRubber,double rate,double value ){
        String in ="insert into sales values(NULL,'"            +branch         + "','"
                                                                +date           + "','"
                                                                +billNo         + "','"
                                                                +party          + "',"
                                                                +barrelNoFrom   + ","
                                                                +barrelNoTo     + ","
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
        String del="delete from sales where salesId="+id+";";
        try {
            stmt.executeUpdate(del);
        } catch (SQLException ex) {
            Logger.getLogger(MasterDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static TableModel getTable(Statement stmt){
        String sqlQuery = "select salesId as 'ID', branch as 'Branch', date as 'Date', billNo as 'Bill No:', party as 'Party',barrelNoFrom as 'Barrel # From', barrelNoTo as 'Barrel # To', quantity as 'Quantity', drc as 'DRC', dryRubber as 'Dry Rubber', rate as 'Rate' , value as 'Value' from sales;";
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
    
    public static ResultSet selectOneId(Statement stmt, String id){
        String sql="select * from sales where salesId="+id+";";
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