/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Statement;

/**
 *
 * @author lenovo
 */
public final class MasterDB {
    public static void insert(Statement stmt, String accountNo, String accountHead, double openingBal, double closingBal, String category ){
        String branchTable ="insert into master values('"   +accountNo  + "','"
                                                            +accountHead+ "',"
                                                            +openingBal + ","
                                                            +closingBal + ",'"
                                                            +category   + "')";   
    }
}

