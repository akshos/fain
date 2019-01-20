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
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author akshos
 */
public class SalesBill {
    private static final String FILENAME = "salesbill.xls";
    private static final String OUTPUTFILE = "salesbill_output.xls";
    private static final int ENTRYBASEROW = 19;
    private static final int TOTALBASEROW = 21;
    private static final int TOTALCOL = 10;
    private static final int CGSTRATECOL = 8;
    private static final int SGSTRATECOL = 10;
    private static final int IGSTRATECOL = 12;
    private static final int DEFAULTROWCOUNT = 2;
    private static final int CELLWIDTHCOUNT = 15;
    
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
    
    public static void addExtraRows(HSSFWorkbook workbook, int rowCount){
        try{
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName("Arial");
            
            HSSFCellStyle style=workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setFont(font);
            
            HSSFCellStyle styleNumeric=workbook.createCellStyle();
            styleNumeric.setBorderBottom(BorderStyle.THIN);
            styleNumeric.setBorderTop(BorderStyle.THIN);
            styleNumeric.setBorderRight(BorderStyle.THIN);
            styleNumeric.setBorderLeft(BorderStyle.THIN);
            styleNumeric.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
            styleNumeric.setFont(font);
            
            HSSFCellStyle styleCenter=workbook.createCellStyle();
            styleCenter.setAlignment(HorizontalAlignment.CENTER);
            styleCenter.setBorderBottom(BorderStyle.THIN);
            styleCenter.setBorderTop(BorderStyle.THIN);
            styleCenter.setBorderRight(BorderStyle.THIN);
            styleCenter.setBorderLeft(BorderStyle.THIN);
            styleCenter.setFont(font);
            
            
            HSSFSheet sheet = workbook.getSheetAt(0);
            Row row;
            Cell cell;
            int rows = sheet.getLastRowNum();
            sheet.shiftRows(ENTRYBASEROW+1, rows, rowCount);
            for(int i = 1; i <= rowCount; i++){
                row = sheet.createRow(ENTRYBASEROW+i);
                row.setHeight(sheet.getRow(ENTRYBASEROW).getHeight());

                //Sl No
                cell = row.createCell(0);
                cell.setCellStyle(styleCenter);
                //Name of Product
                cell = row.createCell(1);
                cell.setCellStyle(style);
                cell = row.createCell(2);
                cell.setCellStyle(style);
                //HSN COde
                cell = row.createCell(3);
                cell.setCellStyle(styleCenter);
                //Qty
                cell = row.createCell(4);
                cell.setCellStyle(style);
                //Rate to Total
                for(int j = 5; j < 15; j++){
                    cell = row.createCell(j);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellStyle(styleNumeric);
                    
                }
                
                //Cell Formulas
                //Taxable Value (H)
                cell = row.getCell(7);
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula("G" + (ENTRYBASEROW+1+i));
                //CGST Amount (J)
                cell = row.getCell(9);
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula("H" + (ENTRYBASEROW+1+i) + " * I" + (ENTRYBASEROW+1+i) + " / 100" );
                //SGST Amount (L)
                cell = row.getCell(11);
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula("H" + (ENTRYBASEROW+1+i) + " * K" + (ENTRYBASEROW+1+i) + " / 100" );
                //IGST Amount (N)
                cell = row.getCell(13);
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula("H" + (ENTRYBASEROW+1+i) + " * M" + (ENTRYBASEROW+1+i) + " / 100" );
                //Total (O)
                cell = row.getCell(14);
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula("H" + (ENTRYBASEROW+1+i) + "+J" + (ENTRYBASEROW+1+i) + "+L" + (ENTRYBASEROW+1+i) + "+N" + (ENTRYBASEROW+1+i));
                
                sheet.addMergedRegion(
                        new CellRangeAddress(ENTRYBASEROW+i, ENTRYBASEROW+i, 1, 2));
                
                
            }
            //sheet.setActiveCell(new CellAddress(0, 0));
        }catch(Exception e){
            e.printStackTrace();
        }
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
            //Name
            sheet.getRow(0).getCell(0).setCellValue(salesHeader.getName());
            //Address
            sheet.getRow(2).getCell(0).setCellValue(salesHeader.getAddress());
            //GSTIN
            sheet.getRow(2).getCell(13).setCellValue(salesHeader.getKgst());
            //RB Reg No
            sheet.getRow(3).getCell(13).setCellValue(salesHeader.getRbno());
            //Phone Number
            sheet.getRow(3).getCell(0).setCellValue(salesHeader.getPhone1() + ", " + salesHeader.getPhone2());
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
            
            int extraRows = 0;
            if(salesEntries.size() > DEFAULTROWCOUNT){
                extraRows = salesEntries.size() - DEFAULTROWCOUNT;
                addExtraRows(workbook, extraRows);
            }
            
            sheet = workbook.getSheetAt(0);
            
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
            sheet.getRow(TOTALBASEROW + extraRows).getCell(TOTALCOL).setCellValue(totalAmount);
            //Add: CGST
            sheet.getRow(TOTALBASEROW + extraRows+1).getCell(TOTALCOL).setCellValue(totalCGST);
            //Add: SGST
            sheet.getRow(TOTALBASEROW + extraRows+2).getCell(TOTALCOL).setCellValue(totalSGST);
            //Add: IGST
            sheet.getRow(TOTALBASEROW + extraRows+3).getCell(TOTALCOL).setCellValue(totalIGST);
            
            //Total Amount GST
            double totalGST = totalCGST+totalSGST+totalIGST;
            sheet.getRow(TOTALBASEROW + extraRows+4).getCell(TOTALCOL).setCellValue(totalGST);
            
            //Total Amount
            totalAmount += totalGST;
            sheet.getRow(TOTALBASEROW + extraRows+5).getCell(TOTALCOL).setCellValue(totalAmount);
            
            //Rounded Total 
            double grandTotal = Math.round(totalAmount);
            double roundOff = grandTotal - totalAmount;
            //Round Off
            sheet.getRow(TOTALBASEROW + extraRows+6).getCell(TOTALCOL).setCellValue(roundOff);
            //Grand Total
            sheet.getRow(TOTALBASEROW + extraRows+7).getCell(TOTALCOL).setCellValue(grandTotal);
            
            //Grand Total in wordings
            String wordings = CommonFuncs.numberToWords((int)grandTotal);
            sheet.getRow(TOTALBASEROW + extraRows).getCell(0).setCellValue("Total Invoice Amount in Words: " + wordings + " Only");
            
            
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
        
        String name;
        String address;
        String phone1;
        String phone2;
        String kgst;
        String rbno;
        
        String transportationMode;
        String vehicleNo;
        String challanNumber;
        String invoiceDate;
        String timeOfSupply;
        String invoiceNumber;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone1() {
            return phone1;
        }

        public void setPhone1(String phone1) {
            this.phone1 = phone1;
        }

        public String getPhone2() {
            return phone2;
        }

        public void setPhone2(String phone2) {
            this.phone2 = phone2;
        }

        public String getKgst() {
            return kgst;
        }

        public void setKgst(String kgst) {
            this.kgst = kgst;
        }

        public String getRbno() {
            return rbno;
        }

        public void setRbno(String rbno) {
            this.rbno = rbno;
        }
        
        
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
