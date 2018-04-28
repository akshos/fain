/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fain;

import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 *
 * @author akshos
 */
public class Preferences {
    static private String windowSizeFileName = "windowSize.properties";
    static private String databasePropertiesFileName = "database.properties";
    
    private static Properties windowSizeProperties = null;
    private static Properties databaseProperties = null;
    
    public static void loadAllProperties(){
        loadWindowSizeProperties();
        loadDatabaseProperties();
    }
    
    public static void storeAllProperties(){
        storePropertiesFile(windowSizeFileName, windowSizeProperties);
        storePropertiesFile(databasePropertiesFileName, databaseProperties);
    }
    
    private static void storePropertiesFile(String fileName, Properties props){
        File propFile = new File(fileName);
        FileWriter writer = null;
        try{
            writer = new FileWriter(propFile);
            props.store(writer, null);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
        
    //
    //Window Size Properties Begin
    //
    private static Properties getDefaultWindowSizes(){
        Properties defaultProps = new Properties();
        defaultProps.setProperty("AMaster_width", "790");
        defaultProps.setProperty("AMaster_height", "300");
        defaultProps.setProperty("ATransaction_width", "790");
        defaultProps.setProperty("ATransaction_height", "310");
        defaultProps.setProperty("AStock_width", "790");
        defaultProps.setProperty("AStock_height", "360");
        defaultProps.setProperty("APLatex_width", "790");
        defaultProps.setProperty("APLatex_height", "450");
        defaultProps.setProperty("APOthers_width", "790");
        defaultProps.setProperty("APOthers_height", "410");
        defaultProps.setProperty("ASLatex_width", "790");
        defaultProps.setProperty("ASLatex_height", "530");
        defaultProps.setProperty("AConsumption_width", "790");
        defaultProps.setProperty("AConsumption_height", "380");
        defaultProps.setProperty("EMaster_width", "790");
        defaultProps.setProperty("EMaster_height", "470");
        
        return defaultProps;
    }
    
    private static void createDefaultWindowSizeFile(){
        Properties defaultProps = getDefaultWindowSizes();
        File windowSizeFile = new File("windowSize.properties");
        try{
            System.out.println("Creating deafult window size file");
            FileWriter writer = new FileWriter(windowSizeFile);
            defaultProps.store(writer, null);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static void loadWindowSizeProperties(){
        File windowSizeFile = new File(windowSizeFileName);
        if (!windowSizeFile.exists()){
            createDefaultWindowSizeFile();
            windowSizeFile = new File(windowSizeFileName);
        }
        try{
            FileReader reader = new FileReader(windowSizeFile);
            windowSizeProperties = new Properties();
            windowSizeProperties.load(reader);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static java.awt.Dimension getInternalFrameDimension(javax.swing.JInternalFrame frame){
        Dimension dim = null;          
        String keyBase = frame.getClass().getSimpleName();
        if(!windowSizeProperties.containsKey(keyBase+"_width") || !windowSizeProperties.containsKey(keyBase+"_height")){
            return null;
        }
        int width = Integer.valueOf(windowSizeProperties.getProperty(keyBase+"_width"));
        int height = Integer.valueOf(windowSizeProperties.getProperty(keyBase+"_height"));
        System.out.println("Loaded " + keyBase + " size");
        dim = new Dimension(width, height);
        return dim;
    }
    
    public static void storeInternalFrameDimension(javax.swing.JInternalFrame frame){
        String keyBase = frame.getClass().getSimpleName();
        Dimension dim = frame.getSize();
        int width = (int)dim.getWidth();
        int height = (int)dim.getHeight();
        windowSizeProperties.setProperty(keyBase+"_width", String.valueOf(width));
        windowSizeProperties.setProperty(keyBase+"_height", String.valueOf(height));
        System.out.println("Saved " + keyBase + " size");
    }
    //
    //Window Size Properties End
    //
    
    //
    //Database Properties Start
    //
    private static Properties getDefatultDatabaseProperties(){
        Properties props = new Properties();
        props.setProperty("dblocation", "db/");
        props.setProperty("dbname", "none");
        return props;
    }
    
    private static void createDefaultDatabasePropertiesFile(){
        Properties defaultProps = getDefatultDatabaseProperties();
        File databasePropertiesFile = new File(databasePropertiesFileName);
        try{
            System.out.println("Creating deafult database properties file");
            FileWriter writer = new FileWriter(databasePropertiesFile);
            defaultProps.store(writer, null);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static void loadDatabaseProperties(){
        File databasePropertiesFile = new File(databasePropertiesFileName);
        if (!databasePropertiesFile.exists()){
            createDefaultDatabasePropertiesFile();
            databasePropertiesFile = new File(databasePropertiesFileName);
        }
        try{
            FileReader reader = new FileReader(databasePropertiesFile);
            databaseProperties = new Properties();
            databaseProperties.load(reader);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static String getDatabaseLocation(){
        String location = "";
        if(databaseProperties != null){
            location = databaseProperties.getProperty("dblocation");
        }
        else{
            System.out.println("ERROR : getDatabaseLocation : databaseProperties is null");
        }
        return location;
    }
    
    public static void storeDatabaseLocation(String location){
        if(databaseProperties != null){
            databaseProperties.setProperty("dblocation", location);
        }
        else{
            System.out.println("ERROR : setDatabaseLocation : databaseProperties is null");
        }
    }
    
    public static String getDatabaseName(){
        String name = "";
        if(databaseProperties != null){
            name = databaseProperties.getProperty("dbname");
        }
        else{
            System.out.println("ERROR : getDatabaseName: databaseProperties is null");
        }
        return name;
    }
    
    public static void setDatabaseName(String dbName){
        if(databaseProperties != null){
            databaseProperties.setProperty("dbname", dbName);
        }
        else{
            System.out.println("ERROR : setDatabaseName: databaseProperties is null");
        }
    }
            
    //
    //Database Properties End
    //
}
