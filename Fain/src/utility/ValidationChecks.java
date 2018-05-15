/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author akshos
 */
public class ValidationChecks {
    private static final String codePattern = "^[a-zA-Z0-9_-]{3,30}$";
    private static final String namePattern = "^[a-zA-Z_ -]{3,30}$";
    private static final String idPattern = "^[0-9_-]{3,30}$";
    
    final static String DATE_FORMAT = "dd/MM/yy";
    
    public static boolean validateCode(String item){
        Pattern pattern = Pattern.compile(codePattern);
        Matcher matcher = pattern.matcher(item);
        return matcher.matches();
    }
    
    public static boolean validateId(String item){
        Pattern pattern = Pattern.compile(idPattern);
        Matcher matcher = pattern.matcher(item);
        return matcher.matches();
    }
    
    public static boolean validateName(String item){
        Pattern pattern = Pattern.compile(namePattern);
        Matcher matcher = pattern.matcher(item);
        return matcher.matches();
    }

    public static boolean isDateValid(String date) 
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
