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
	public static String[][] getStringArray( ResultSet rs ){
            try{
                
                Vector<String> rows = new Vector<String>();
                Vector<String> rows1 = new Vector<String>();
                while( rs.next() ){
                    rows.addElement( rs.getObject(1).toString() );
                    rows1.addElement( rs.getObject(2).toString() );
                    }
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
}
