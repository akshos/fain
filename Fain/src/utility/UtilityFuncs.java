/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

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
}
