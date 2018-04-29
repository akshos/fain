/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import fain.Preferences;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class DBConnection {
    String databaseName;
    Connection conn;
    Statement stmt;
    
    public DBConnection(){
        databaseName = Preferences.getDatabaseName();
        conn = null;
        stmt = null;
    }
    public DBConnection(String name){
        databaseName = name;
        conn = null;
        stmt = null;
    }
    
    public Statement getStatement(){
        
        return this.stmt;
    }
    
    public String getDatabaseName(){
        
        return databaseName;
    }
    public void setDatabaseName(String name){
        databaseName = name;
    }
    
    public boolean checkExisting(String databaseName){
        String location = Preferences.getDatabaseLocation();
        String fileUrl = location + databaseName;
        File dbFile = new File(fileUrl);
        if(!dbFile.exists()){
            System.out.println("Database file " + databaseName + " does not exist");
            return false;
        }
        System.out.println("Database file " + databaseName + " found");
        return true;
    }
    

    
    public void createDatabase(String url){
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            System.out.println("New database created : " + databaseName);
            Tables.createTables(stmt);
            stmt.close();
            conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    private String dbNameFromDate(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = formatter.format(date);
        String dbFileName = "db_" + strDate + "_.db";
        return dbFileName;
    }
    
    public void createNewDatabase(String dbName){
        String location = Preferences.getDatabaseLocation();
        databaseName = dbName;
        Preferences.setDatabaseName(databaseName);
        String url = "jdbc:sqlite:"+location+databaseName;
        createDatabase(url);
    }
    
    public void connect(){
        try{
            String location = Preferences.getDatabaseLocation();
            String url = "jdbc:sqlite:"+location+databaseName;
            if( !checkExisting(databaseName) ){
                System.out.println("ERROR : Database : " + databaseName + " not found");
            }
            else{
                conn = DriverManager.getConnection(url);
                stmt = conn.createStatement();
            }         
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    public int checkDatabaseAvailability(){
        if(databaseName.compareTo("none") == 0){
            System.out.println("No previous database defined");
            return Codes.NO_DATABASE;
        }
        else if(!checkExisting(databaseName)){
            System.out.println("ERROR : Database : " + databaseName + " not found");
            return Codes.FAIL;
        }
        System.out.println("Database : " + databaseName + " found");
        return Codes.DATABASE_FOUND;
    }
}
