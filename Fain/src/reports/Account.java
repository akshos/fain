/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

/**
 *
 * @author akshos
 */
public class Account{
    private double credit;
    private double debit;
    private double openingBal;
    private double closingBal;
    String accountHead;

    public Account(){
        openingBal = credit = debit = closingBal = 0.0;
        accountHead = "";
    }
    
    public Account(String head){
        accountHead = head;
        openingBal = credit = debit = closingBal = 0.0;
    }

    public Account(String head, double opBal){
        accountHead = head;
        openingBal = opBal;
        credit = debit = closingBal = 0.0;
    }
    
    public Account(String head, String opBal){
        accountHead = head;
        openingBal = Double.parseDouble(opBal);
        credit = debit = closingBal = 0.0;
    }

    public double getCredit(){
        return credit;
    }
    public void setCredit(double value){
        credit = value;
    }
    public void addCredit(double value){
        credit += value;
    }

    public double getDebit(){
        return debit;
    }
    public void setDebit(double value){
        debit = value;
    }
    public void addDebit(double value){
        debit += value;
    }

    public double getOpeningBal(){
        return openingBal;
    }
    public void setOpeningBal(double value){
        openingBal = value;
    }
    public void setOpeningBal(String value){
        openingBal = Double.parseDouble(value);
    }
    
    public double getClosingBal(){
        return closingBal;
    }
    public void setClosingBal(double value){
        closingBal = value;
    }
    public double calculateClosingBal(){
        closingBal = debit - credit;
        return closingBal;
    }
    
    public String getAccountHead(){
        return accountHead;
    }
}