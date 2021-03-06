/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package users;

import utility.Codes;
import database.UsersDB;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author akshos
 */
public class User {
    private String username;
    private int loginStatus;
    private int userType;
    UsersDB usersDB;
    
    private static final String usernamePattern = "^[a-zA-Z0-9_-]{3,15}$";
    private static final String passwordPattern = "^[a-zA-Z0-9_@$#-]{3,15}$";
    
    public User(){
        this.username = "";
        this.loginStatus = Codes.UNAUTHORIZED;
        this.usersDB = new UsersDB();
    }
    
    public User(String username){
        this.username = username;
        this.loginStatus = Codes.UNAUTHORIZED;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public int getLoginStatus(){
        return this.loginStatus;
    }
        
    public int authenticate(String password){
        String[] data = UsersDB.getPasswordandType(username);
        if(data == null){
            System.out.println("No username " + username);
            return Codes.UNAUTHORIZED;
        }
        String storedHash = data[0];
        int userType = Integer.valueOf(data[1]);
        String currHash = UsersDB.encode(password);
        if(currHash.compareTo(storedHash) != 0){
            System.out.println("Passwords dont match for " + username);
            return Codes.UNAUTHORIZED;
        }
        System.out.println("Authentication success for " + username);
        this.loginStatus = Codes.AUTHORIZED;
        this.userType = userType;
        return this.loginStatus;
    }
    
    public static boolean validUsername(String username){
        Pattern pattern = Pattern.compile(usernamePattern);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
    
    public static boolean validPassword(String password){
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
