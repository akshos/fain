/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import utility.Codes;
/**
 *
 * @author akshos
 */
public final class UsersDB {
    private static String sep = File.separator;
    private static String usersDBFile = "db" + sep + "users.db";
    private static String url = "jdbc:sqlite:" + usersDBFile;
   
    
    public static int addUser(String username, String password, int type){
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String passwordHash = encode(password);
            String query = "insert into users values (\"" + username + "\", \"" + passwordHash + "\", " + type + " );";
            stmt.execute(query);
            conn.close();
            return Codes.SUCCESS;
        }catch(SQLException se){
            se.printStackTrace();
            return se.getErrorCode();
        }
    }
    
    public static int existingUser(String username){
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String query = "select * from users where username=\"" + username + "\";";
            ResultSet rs = stmt.executeQuery(query);
            conn.close();
            if (!rs.next()){
                return Codes.NOT_EXISTS;
            }else{
                return Codes.EXISTING_USER;
            }
        }catch(SQLException se){
            se.printStackTrace();
            return se.getErrorCode();
        }
    }
    
    public static void createUsersDB(){
        String table = "create table users(\n"
                + "username varchar(20) primary key,\n"
                + "password varchar(50) not null,\n"
                + "type int(2) not null);";
        System.out.println("Creating new users database");
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(table);
            conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    public static String[] getPasswordandType(String username){
        String query = "select password, type from users where username=\""+username+"\";";
        String[] data = null;
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()){
                data = new String[2];
                data[0] = rs.getString(1);
                data[1] = rs.getString(2);
            }
            conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
        return data;
    }
    
    public static String encode(String str){
        byte[] rawBytes = str.getBytes(StandardCharsets.UTF_8);
        String hashString = null;
        byte[] hashCode = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hashCode = digest.digest(rawBytes);
            hashString = new String(hashCode);
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return hashString;
    }
    
    public static boolean chechUsersDB(){
        File usersDbFile = new File(usersDBFile);
        if(!usersDbFile.exists()){
            System.out.println("No users database found : " + url);
            createUsersDB();
            return false;
        }
        System.out.println("Users database found : " + url);
        String query = "select * from users where type=" + Codes.ADMIN_USER + ";";
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (!rs.next()){
                System.out.println("No admin account");
                return false;
            }
            conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
      return true;      
    }
}
