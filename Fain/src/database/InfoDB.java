/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utility.Codes;
/**
 *
 * @author akshos
 */
public class InfoDB {
    private static String sep = File.separator;
    private static String infoDBFile = "db"+sep+"info.db";
    private static String url = "jdbc:sqlite:" + infoDBFile;
    private static final String sessionNamePattern = "^[a-z0-9A-Z_ -]{3,15}$";
    
    public static String[] getSessionNames(){
        String query1 = "select count(*) from sessions;";
        String query2 = "select sessionName from sessions;";
        String[] sessionNames = null;
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query1);
            sessionNames = new String[rs.getInt(1)];
            rs = stmt.executeQuery(query2);
            int i = 0;
            while(rs.next()){
                sessionNames[i++] = rs.getString(1);
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return sessionNames;
    }
    
    private static void createTables(Statement stmt){
        String sessionsTable = "create table sessions( sessionName varchar(30) primary key);";
        try{
            stmt.execute(sessionsTable);
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    private static boolean existingSession(Statement stmt, String sessionName){
        try{
            String query = "select * from sessions where sessionName=\""+sessionName+"\";";
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()){
                return true;
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return false;
    }
    
    public static int addSessionName(String sessionName){
        boolean exists = false;
        File file = new File(infoDBFile);
        if(file.exists()){
            exists = true;
        }
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            if(!exists){
                System.out.println("Creating new sessions table");
                createTables(stmt);
            }
            if(existingSession(stmt, sessionName)){
                return Codes.EXISTING_ENTRY;
            }
            String insertQuery = "insert into sessions values(\""+sessionName+"\");";
            stmt.execute(insertQuery);
            return Codes.SUCCESS;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return Codes.FAIL;
    }
    
    public static boolean validSessionName(String sessionName){
        Pattern pattern = Pattern.compile(sessionNamePattern);
        Matcher matcher = pattern.matcher(sessionName);
        return matcher.matches();
    }
}
