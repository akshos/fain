/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import database.DBConnection;
import fain.APLatex;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.ScrollPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author akshos
 */
public class UtilityFuncs {
    public static void setTableFont(JTable table){
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 24));
        table.setFont(new Font("Dialog", Font.BOLD, 24));
    }
    
    public static void click(Component target, int x, int y)
    {
        MouseEvent press, release, click;
        Point point;
        long time;

        point = new Point(x, y);

        SwingUtilities.convertPointToScreen(point, target);

        time    = System.currentTimeMillis();
        press   = new MouseEvent(target, MouseEvent.MOUSE_PRESSED,  time, 0, x, y, point.x, point.y, 1, false, MouseEvent.BUTTON1);
        release = new MouseEvent(target, MouseEvent.MOUSE_RELEASED, time, 0, x, y, point.x, point.y, 1, false, MouseEvent.BUTTON1);
        click   = new MouseEvent(target, MouseEvent.MOUSE_CLICKED,  time, 0, x, y, point.x, point.y, 1, false, MouseEvent.BUTTON1);

        target.dispatchEvent(press);
        target.dispatchEvent(release);
        target.dispatchEvent(click);
    }
    
    public static String dateUserToSql(String date){
        try{
            String parts[] = date.split("/");
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static String dateSqlToUser(String date){
        if(date.trim().isEmpty())
            return "";
        try{
            String parts[] = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static int selectOption(JInternalFrame form, String title, String[][] optionData){
        if(optionData == null){
            return -1;
        }
        int len = optionData[0].length;
        String[] cboxData = new String[len];
        for(int i = 0; i < len; i++){
            cboxData[i] = optionData[0][i] + " : " + optionData[1][i];
        }
        JComboBox optionList = new JComboBox(cboxData);
        optionList.setFont(new java.awt.Font("Dialog", 1, 24));
        final JComponent[] inputs = new JComponent[]{
            optionList,
        };
        optionList.requestFocus();
        int res = JOptionPane.showConfirmDialog(form, inputs, "SELECT " + title, JOptionPane.PLAIN_MESSAGE);
        if(res == JOptionPane.OK_OPTION){
            int index = optionList.getSelectedIndex();
            return index;
        }
        return -1;
    }

}
