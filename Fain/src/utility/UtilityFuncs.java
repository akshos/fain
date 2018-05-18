/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.awt.Font;
import javax.swing.JTable;

/**
 *
 * @author akshos
 */
public class UtilityFuncs {
    public static void setTableFont(JTable table){
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 24));
        table.setFont(new Font("Dialog", Font.BOLD, 24));
    }
}
