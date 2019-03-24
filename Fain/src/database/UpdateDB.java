/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author akshos
 */
public class UpdateDB {
    
    public static void updateFromPrevSession(DBConnection currDB, String prevSession) {
        DBConnection fromDB = new DBConnection();
        fromDB.setDatabaseName(prevSession);
        fromDB.connect();
        
        try{
            currDB.startTransaction();
            updateMasterDB(fromDB, currDB);
            Thread.sleep(200);
            updateBranchDB(fromDB, currDB);
            Thread.sleep(200);
            updateCustomerDB(fromDB, currDB);
            Thread.sleep(200);
            updateBarrelsDB(fromDB, currDB);
            Thread.sleep(200);
            updateCompanyBarrelDB(fromDB, currDB);
            Thread.sleep(200);
            updateSessionInfoDB(fromDB, currDB);
            Thread.sleep(200);
            currDB.endTransaction();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Some error occured when trying to update: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            currDB.rollbackTransaction();
            e.printStackTrace();
        }
        
    }
    
    private static void updateMasterDB(DBConnection fromDB, DBConnection toDB) throws SQLException{
        ResultSet rs = MasterDB.selectAll(fromDB.getStatement());
        String accountNo;
        double closingBalance;
        while (rs.next()) {
            accountNo = rs.getString("accountNo");
            closingBalance = rs.getDouble("closingBal");
            
            if (MasterDB.checkExisting(toDB.getStatement(), accountNo)) {
                MasterDB.setOpeningBalance(toDB.getStatement(), accountNo, String.valueOf(closingBalance));
            } else {
                MasterDB.insert(toDB.getStatement(), accountNo, rs.getString("accountHead"), closingBalance, 0, rs.getString("category"));
            }
        }   
    }
    
    private static void updateBranchDB(DBConnection fromDB, DBConnection toDB) throws SQLException{
        ResultSet rs = BranchDB.selectAll(fromDB.getStatement());
        String branchName;
        while(rs.next()) {
            branchName = rs.getString("name");
            if(!BranchDB.checkExistingName(toDB.getStatement(), branchName)) {
                 BranchDB.insert(toDB.getStatement(), branchName, rs.getString("address"), rs.getString("kgst"), rs.getString("rbno"));
            }
        }
    }
    
    private static void updateCustomerDB(DBConnection fromDB, DBConnection toDB) throws SQLException{
        ResultSet rs = CustomerDB.selectAll(fromDB.getStatement());
        String cusid;
        while(rs.next()) {
            cusid = rs.getString("customerCode");
            if (!CustomerDB.checkExisting(toDB.getStatement(), cusid)) {
                CustomerDB.insert(toDB.getStatement(), cusid, rs.getString("name"), rs.getString("address"), rs.getString("branch"), rs.getString("kgst"), rs.getString("rbno"), rs.getString("contact"), rs.getString("barrels"));
            }
            
        }
    }
    
    private static void updateBarrelsDB(DBConnection fromDB, DBConnection toDB) throws SQLException {
        ResultSet rs = BarrelDB.selectAll(fromDB.getStatement());
        String barrelId;
        while (rs.next()) {
            barrelId = rs.getString("barrelId");
            if(!BarrelDB.checkExisting(toDB.getStatement(), barrelId)) {
                BarrelDB.insert(toDB.getStatement(), rs.getString("branch"), rs.getString("customerCode"), rs.getString("date"), rs.getString("stock"), rs.getString("issued"), rs.getString("lifted"), rs.getString("difference"));
            }
        }
    }
    
    private static void updateCompanyBarrelDB(DBConnection fromDB, DBConnection toDB) throws SQLException {
        ResultSet rs = CompanyBarrelDB.selectAll(fromDB.getStatement());
        String id;
        while(rs.next()) {
            id = rs.getString("cbarrelId");
            if(!CompanyBarrelDB.checkExisting(toDB.getStatement(), id)) {
                CompanyBarrelDB.insert(toDB.getStatement(), rs.getString("date"), rs.getString("issued"), rs.getString("lifted"), rs.getString("emptyLifted"), rs.getString("difference"));
            }
        }
        
        int companyOpStock = CompanyBarrelDB.getCompanyOpStock(fromDB.getStatement());
        int companyShortage = CompanyBarrelDB.getCompanyShortage(fromDB.getStatement());
        int customerIssued = CompanyBarrelDB.getCustomerIssued(fromDB.getStatement());
        int latexBarrel = CompanyBarrelDB.getLatexBarrel(fromDB.getStatement());
        int emptyBarrel = CompanyBarrelDB.getEmptyBarrel(fromDB.getStatement());
        
        CompanyBarrelDB.setCompanyOpStock(toDB.getStatement(), String.valueOf(companyOpStock));
        CompanyBarrelDB.setCompanyShortage(toDB.getStatement(), String.valueOf(companyShortage));
        CompanyBarrelDB.setCustomerIssued(toDB.getStatement(), String.valueOf(customerIssued));
        CompanyBarrelDB.setEmptyBarrel(toDB.getStatement(), String.valueOf(emptyBarrel));
        CompanyBarrelDB.setLatexBarrel(toDB.getStatement(), String.valueOf(latexBarrel)); 
    }
    
    private static void updateSessionInfoDB(DBConnection fromDB, DBConnection toDB) throws Exception{
        ResultSet rs = SessionInfoDB.selectAll(fromDB.getStatement());
        while (rs.next()) {
            String id = rs.getString("id");
            if(!SessionInfoDB.checkExisting(toDB.getStatement(), id)) {
                SessionInfoDB.insert(toDB.getStatement(), rs.getString("name"), rs.getString("address"), rs.getString("gst"), rs.getString("rbreg"), rs.getString("phone1"), rs.getString("phone2"));
            } else {
                SessionInfoDB.update(toDB.getStatement(), rs.getString("name"), rs.getString("address"), rs.getString("gst"), rs.getString("rbreg"), rs.getString("phone1"), rs.getString("phone2"));
            }
        }
        SessionInfoDB.loadSessionDetails(toDB.getStatement());
    }
}
