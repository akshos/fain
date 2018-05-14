/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.*;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author akshos
 */
public class ResultSetToStringArray {
    public static String[] getStringArray1col(ResultSet rs){
        try{
            Vector<String> rows = new Vector<String>();
            if(rs.next()){
                do{
                    rows.addElement( rs.getObject(1).toString() );
                }while( rs.next() );
            }else return null;
            String[] array =rows.toArray(new String[rows.size()]);
            return array;
        }catch( SQLException se ){
                se.printStackTrace();
        }catch( Exception e ){
                e.printStackTrace();
        }
        return null;
    }
    
    public static String[][] getStringArray2col( ResultSet rs ){
        try{

            Vector<String> rows = new Vector<String>();
            Vector<String> rows1 = new Vector<String>();
            if(rs.next()){
                do{
                    rows.addElement( rs.getObject(1).toString() );
                    rows1.addElement( rs.getObject(2).toString() );
                }while( rs.next() );
            }else return null;
            String[] id = rows.toArray(new String[rows.size()]);
            String[] name=rows1.toArray(new String[rows1.size()]);
            String[][] array = new String[2][];
            array[0]=id;
            array[1]=name;
            return array;
        }catch( SQLException se ){
                se.printStackTrace();
        }catch( Exception e ){
                e.printStackTrace();
        }
        return null;
    }
    
    public static String[][] getStringArray3col(ResultSet rs){
        try{
            Vector<String> rows = new Vector<String>();
            Vector<String> rows1 = new Vector<String>();
            Vector<String> rows2 = new Vector<String>();
            if(rs.next()){
                do{
                    rows.addElement( rs.getString(1) );
                    rows1.addElement( rs.getString(2) );
                    rows2.addElement(rs.getString(3));
                }while(rs.next());
            }else return null;
                
            String[] id = rows.toArray(new String[rows.size()]);
            String[] name = rows1.toArray(new String[rows1.size()]);
            String[] addr = rows2.toArray(new String[rows2.size()]);
            String[][] array = new String[3][];
            array[0]=id;
            array[1]=name;
            array[2]=addr;
            return array;
        }catch( SQLException se ){
                se.printStackTrace();
        }catch( Exception e ){
                e.printStackTrace();
        }
        return null;
    }
    
    public static String[] getRowAsStringArray(ResultSet rs){
        try{
            if(rs.next()){
                int colCount = rs.getMetaData().getColumnCount();
                String rowData[] = new String[colCount];
                for(int i = 0; i < colCount; i++){
                    rowData[i] = rs.getString(i+1);
                }
                return rowData;
            }
            return null;
        }catch(SQLException se){
            se.printStackTrace();
        }
        return null;
    }
}
