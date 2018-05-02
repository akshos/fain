/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author akshos
 */
public class Validation {
    private static final String codePattern = "^[a-zA-Z0-9_-]{3,30}$";
    private static final String namePattern = "^[a-zA-Z_-]{3,30}$";
    private static final String idPattern = "^[0-9_-]{3,30}$";
    
    public boolean validateCode(String item){
        Pattern pattern = Pattern.compile(codePattern);
        Matcher matcher = pattern.matcher(item);
        return matcher.matches();
    }
    
    public boolean validateId(String item){
        Pattern pattern = Pattern.compile(codePattern);
        Matcher matcher = pattern.matcher(item);
        return matcher.matches();
    }
    
    public boolean validateName(String item){
        Pattern pattern = Pattern.compile(codePattern);
        Matcher matcher = pattern.matcher(item);
        return matcher.matches();
    }
}
