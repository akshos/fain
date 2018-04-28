/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author akshos
 */
public final class Tables {
    public static final String masterTable = "create table master("
            + "accountNo    varchar(20) primary key,"
            + "accountHead  varchar(50) ,"
            + "openingBal   real,"
            + "closingBal   real,"
            + "category varchar(20));";
    
    public static final String transactionTable = "create table transaction("
            + "transactionID    integer primary key autoincrement,"
            + "date             date,"
            + "debit            varchar(20),"
            + "credit           varchar(20),"
            + "amount           real,"
            + "narration        varchar(50));";
    public static final String stockTable = "create table stock("
            + "itemCode     varchar(20) primary key,"
            + "itemName     varchar(20),"
            + "currentStock integer,"
            + "rate         real,"
            + "purchaseAC   varchar(20),"
            + "saleAC       varchar(20),"
            + "stockAC      varchar(20));";
    public static final String purchaseLatexTable = "create table purchaseLatex("
            + "purchaseLatexCode    varchar(20) primary key,"
            + "branch               varchar(20),"
            + "date                 date,"
            + "prBill               varchar(20),"
            + "party                varchar(20),"//customer code
            + "quantity             real,"
            + "drc                  real,"
            + "dryRubber            real,"
            + "rate                 real,"
            + "value                real);";
    public static final String purchaseTable = "create table purchase("
            + "purchaseCode     varchar(20) primary key,"
            + "branch           varchar(20),"
            + "date             varchar(20),"
            + "billNo           varchar(20),"
            + "party            varchar(20),"
            + "itemCode         varchar(20),"
            + "itemName         varchar(20),"
            + "quantity         real,"
            + "value            real);";
    public static final String salesTable = "create table sales("
            + "salesCode    varchar(20) primary key,"
            + "branch       varchar(20),"
            + "date         date,"
            + "billNo       varchar(20),"
            + "party        varchar(20),"
            + "barrelNoFrom interger,"
            + "barrelNoTo   integer,"
            + "quantity     real,"
            + "drc          real,"
            + "dryRubber    real,"
            + "rate         real,"
            + "value        real);";
    public static final String consumptionTable = "create table consumption("
            + "consumptionCode      varchar(20) primary key,"
            + "branch               varchar(20),"
            + "date                 date,"
            + "refNo                date,"
            + "itemCode             varchar(20),"
            + "itemName             varchar(20),"
            + "narration            varchar(50),"
            + "quantity             real);";
    public static final String branchTable ="create table branch("
            + "branchCode   varchar(20) primary key,"
            + "name         varchar(50),"
            + "address      varchar(200),"
            + "kgst         varchar(20),"
            + "rbno         varchar(20));";
    public static final String customerTable ="create table customer("
            + "customerCode varchar(20) primary key,"
            + "name         varchar(50),"
            + "address      varchar(200),"
            + "branch       varchar(20),"
            + "kgst         varchar(20),"
            + "rbno         varchar(20));";
    
    public static void createTables(Statement stmt){
        System.out.println("Creating tables");
        try{
            stmt.execute(masterTable);
            stmt.execute(transactionTable);
            stmt.execute(stockTable);
            stmt.execute(purchaseLatexTable);
            stmt.execute(purchaseTable);
            stmt.execute(salesTable);
            stmt.execute(consumptionTable);
            stmt.execute(branchTable);
            stmt.execute(customerTable);
        }
        catch(SQLException se){
            se.printStackTrace();
        }
    }
    
}