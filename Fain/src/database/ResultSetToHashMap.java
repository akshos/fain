/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author akshos
 */
public class ResultSetToHashMap {
    public static HashMap<String, String> getHashMap(ResultSet rs){
        try{
            if(rs.next()){
                HashMap<String, String> map = new HashMap();
                do{
                    map.put(rs.getString(1), rs.getString(2));
                }while(rs.next());
                return map;
            }else{
                System.out.println("No Accounts");
                return null;
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
}
