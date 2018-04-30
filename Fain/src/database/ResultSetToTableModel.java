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
public class ResultSetToTableModel {
	public static TableModel getTableModel( ResultSet rs ){
            try{
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Vector<String> columnNames = new Vector<String>( columnCount );
                for( int i = 1; i <= columnCount; i++ ){
                        columnNames.add(metaData.getColumnLabel(i));
                }
                Vector<Vector<String>> rows = new Vector<Vector<String>>();
                while( rs.next() ){
                        Vector<String> newRow = new Vector<String>();
                        for( int i = 1; i <= columnCount; i++ ){
                                newRow.addElement( rs.getObject(i).toString() );
                        }
                        rows.addElement(newRow);
                }
                return new DefaultTableModel(rows, columnNames){ 
                        public boolean isCellEditable(int row, int column){ 
                                return false; 
                        } 
                };
            }catch( SQLException se ){
                    se.printStackTrace();
            }catch( Exception e ){
                    e.printStackTrace();
            }
            return null;
	}
}
