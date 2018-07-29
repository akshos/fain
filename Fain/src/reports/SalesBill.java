/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author akshos
 */
public class SalesBill {
    private static final String FILENAME = "salesbill.xls";
    private static final String OUTPUTFILE = "salesbill_output.xls";
    private static final int ENTRYBASEROW = 19;
    private static final int TOTALBASEROW = 23;
    private static final int TOTALCOL = 10;
    private static final int CGSTRATECOL = 8;
    private static final int SGSTRATECOL = 10;
    private static final int IGSTRATECOL = 12;
    
    public static double getGSTRate(HSSFSheet sheet, int col){
        try{
            Row row = sheet.getRow(ENTRYBASEROW);
            Cell cell = row.getCell(col);
            if(cell.getCellTypeEnum() == CellType.NUMERIC){
                double rate = cell.getNumericCellValue();
                return rate;
            }else{
                System.out.println("Invalid GST Rate on column " + col);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public static String getHsnCode(HSSFSheet sheet){
        try{
            Cell cell = sheet.getRow(ENTRYBASEROW).getCell(3);
            if(cell.getCellTypeEnum() == CellType.NUMERIC) {
                BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
                return decimal.toPlainString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
    
    public static double calculateTax(double amount, double rate){
        return (amount * (rate / 100));
    }
      
    public static void createSalesBill(SalesHeader salesHeader, List<SalesEntry> salesEntries){
        try{
            File file = new File(FILENAME);
            FileInputStream fin = new FileInputStream(file);
            
            HSSFWorkbook workbook = new HSSFWorkbook(fin);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Cell cell = null;
            Row row = null;
            
            //Write header information
            //Invoice Number
            sheet.getRow(4).getCell(2).setCellValue(salesHeader.getInvoiceNumber());
            //Chalan No
            sheet.getRow(8).getCell(2).setCellValue(salesHeader.getChallanNumber());
            //Invoice Date
            sheet.getRow(9).getCell(2).setCellValue(salesHeader.getInvoiceDate());
            //Transportation Mode
            sheet.getRow(7).getCell(10).setCellValue(salesHeader.getTransportationMode());
            //Vehicle No:
            sheet.getRow(8).getCell(10).setCellValue(salesHeader.getVehicleNo());
            //Time of Supply:
            sheet.getRow(9).getCell(10).setCellValue(salesHeader.getTimeOfSupply());        
            
            salesEntries.forEach(System.out::println);
            double totalAmount = 0;
            double totalCGST = 0;
            double totalSGST = 0;
            double totalIGST = 0;
            double tax;
            double amount;
            double currTotal;
            String hsnCode = getHsnCode(sheet);
            
            for(SalesEntry entry: salesEntries){
                row = sheet.getRow(ENTRYBASEROW+entry.getSlno()-1);
                //Sl No
                row.getCell(0).setCellValue(entry.getSlno());
                //Name of Product/Service
                row.getCell(1).setCellValue("NATURAL RUBBER LATEX");
                //HSN Code
                row.getCell(3).setCellValue(hsnCode);
                //Qty
                row.getCell(4).setCellValue(entry.getQnty());
                //Rate
                row.getCell(5).setCellValue(entry.getRate());
                //Amount
                amount = entry.getAmount();
                row.getCell(6).setCellValue(amount);
                //Taxable Value
                row.getCell(7).setCellValue(amount);
                
                totalAmount += amount;
                currTotal = amount;
                                
                //Calculate and set CGST Amount
                tax = calculateTax(amount, getGSTRate(sheet, CGSTRATECOL));
                row.getCell(CGSTRATECOL).setCellValue(getGSTRate(sheet, CGSTRATECOL));
                row.getCell(CGSTRATECOL+1).setCellValue(tax);
                totalCGST += tax;
                currTotal += tax;
                System.out.print("CGST: " + tax);
                
                //Calculate and set SGST Amount
                tax = calculateTax(amount, getGSTRate(sheet, SGSTRATECOL));
                row.getCell(SGSTRATECOL).setCellValue(getGSTRate(sheet, SGSTRATECOL));
                row.getCell(SGSTRATECOL+1).setCellValue(tax);
                totalSGST += tax;
                System.out.print("\tSGST: " + tax);
                currTotal += tax;
                
                //Calculate and set IGST Amount
                tax = calculateTax(amount, getGSTRate(sheet, IGSTRATECOL));
                row.getCell(IGSTRATECOL).setCellValue(getGSTRate(sheet, IGSTRATECOL));
                row.getCell(IGSTRATECOL+1).setCellValue(tax);
                totalIGST += tax;
                currTotal += tax;
                System.out.println("\tIGST: " + tax);
                
                //Set current Row Total
                row.getCell(14).setCellValue(currTotal);
                
            }
            
            //Total Amount
            sheet.getRow(TOTALBASEROW).getCell(TOTALCOL).setCellValue(totalAmount);
            //Add: CGST
            sheet.getRow(TOTALBASEROW+1).getCell(TOTALCOL).setCellValue(totalCGST);
            //Add: SGST
            sheet.getRow(TOTALBASEROW+2).getCell(TOTALCOL).setCellValue(totalSGST);
            //Add: IGST
            sheet.getRow(TOTALBASEROW+3).getCell(TOTALCOL).setCellValue(totalIGST);
            
            //Total Amount GST
            double totalGST = totalCGST+totalSGST+totalIGST;
            sheet.getRow(TOTALBASEROW+4).getCell(TOTALCOL).setCellValue(totalGST);
            
            //Total Amount
            totalAmount += totalGST;
            sheet.getRow(TOTALBASEROW+5).getCell(TOTALCOL).setCellValue(totalAmount);
            
            //Rounded Total 
            double grandTotal = Math.round(totalAmount);
            double roundOff = grandTotal - totalAmount;
            //Round Off
            sheet.getRow(TOTALBASEROW+6).getCell(TOTALCOL).setCellValue(roundOff);
            //Grand Total
            sheet.getRow(TOTALBASEROW+7).getCell(TOTALCOL).setCellValue(grandTotal);
            
            //Grand Total in wordings
            String wordings = CommonFuncs.numberToWords((int)grandTotal);
            sheet.getRow(TOTALBASEROW).getCell(0).setCellValue("Total Invoice Amount in Words: " + wordings + " Only");
            
            
            fin.close();
            
            file = new File(OUTPUTFILE);
            FileOutputStream fout = new FileOutputStream(file);
            workbook.write(fout);
            fout.close();
            
            ViewPdf.openPdfViewer(OUTPUTFILE);
            
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static class SalesHeader{

        String transportationMode;
        String vehicleNo;
        String challanNumber;
        String invoiceDate;
        String timeOfSupply;
        String invoiceNumber;
        
        public String getInvoiceDate() {
            return this.invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }
        
        public String getInvoiceNumber() {
            return this.invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public String getChallanNumber() {
            return this.challanNumber;
        }

        public void setChallanNumber(String challanNumber) {
            this.challanNumber = challanNumber;
        }

        public String getTransportationMode() {
            return this.transportationMode;
        }

        public void setTransportationMode(String transportationMode) {
            this.transportationMode = transportationMode;
        }

        public String getVehicleNo() {
            return this.vehicleNo;
        }

        public void setVehicleNo(String vehicleNo) {
            this.vehicleNo = vehicleNo;
        }

        public String getTimeOfSupply() {
            return this.timeOfSupply;
        }

        public void setTimeOfSupply(String timeOfSupply) {
            this.timeOfSupply = timeOfSupply;
        }
        
    }
    
    public static class SalesEntry{
        
        private int slno;
        double qnty;
        double rate;
        double amount;


        
        public int getSlno() {
            return slno;
        }

        public void setSlno(int slno) {
            this.slno = slno;
        }

        public double getQnty() {
            return qnty;
        }

        public void setQnty(double qnty) {
            this.qnty = qnty;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
        
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Sl No: ").append(this.getSlno());
            sb.append("\tQnty: ").append(this.getQnty());
            sb.append("\tRate: ").append(this.getRate());
            sb.append("\tAmount: ").append(this.getRate());
            return sb.toString();
        }
        
    }
    
    public static void main(String args[]) {
        List<SalesBill.SalesEntry> entryList = new ArrayList();
        SalesBill.SalesEntry salesEntry;
        
        salesEntry = new SalesBill.SalesEntry();
        salesEntry.setSlno(1);
        salesEntry.setQnty(125);
        salesEntry.setRate(101.80);
        salesEntry.setAmount(12725.00);
        entryList.add(salesEntry);
        
        salesEntry = new SalesBill.SalesEntry();
        salesEntry.setSlno(2);
        salesEntry.setQnty(1958);
        salesEntry.setRate(102.80);
        salesEntry.setAmount(201282.40);
        entryList.add(salesEntry);
        
        SalesHeader header = new SalesHeader();
        header.setChallanNumber("2");
        header.setInvoiceDate("date");
        header.setInvoiceNumber("2");
        header.setTimeOfSupply("time");
        header.setTransportationMode("transportation mode");
        header.setVehicleNo("vehicle no");
        
        SalesBill.createSalesBill(header, entryList);
    }
}
