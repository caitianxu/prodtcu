package com.cjsz.tech.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil {

//    private static POIFSFileSystem fs;
//
//    private static HSSFWorkbook wb;
//
//    private static HSSFSheet sheet;
//
//    private static HSSFRow row;

//    public static String[] readExcelTitle(InputStream is) {
//        try {
//            fs = new POIFSFileSystem(is);
//            wb = new HSSFWorkbook(fs);
//            sheet = wb.getSheetAt(0);
//            row = sheet.getRow(0);
//            int total_cols = row.getPhysicalNumberOfCells();
//            String titles[] = new String[total_cols];
//            System.out.println("总列数:" + total_cols);
//            for (int i = 0; i < total_cols; i++) {
//                String value = getCellFormatValue(row.getCell(i));
//                System.out.println(value + " ");
//                titles[i] = value;
//            }
//            return titles;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

    public static Map<Integer, List<String>> readExcelContent(InputStream is,String suffix) {
        Map<Integer, List<String>> content = new HashMap<Integer, List<String>>();
        List<String> cellContent = null;
        try {
//          fs = new POIFSFileSystem(is);
            Workbook wb =null;
            if(suffix.equals("xls")){
                wb = new HSSFWorkbook(is);
            }else{
                wb = new XSSFWorkbook(is);
            }

            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(0);
            int total_cols = row.getPhysicalNumberOfCells();
            int totol_rows = sheet.getLastRowNum();
            for (int i = 1; i <= totol_rows; i++) {
                row = sheet.getRow(i);
                cellContent = new ArrayList<String>();
                int j = 0;
                while (j < total_cols) {
                    Cell cell = row.getCell((short) j);
                    if(cell!=null){
                        cellContent.add(getCellFormatValue(row.getCell((short) j)).trim());
                    }else{
                        cellContent.add("");
                    }
                    j++;
                }
                content.put(i, cellContent);
            }
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCellFormatValue(Cell cell) {
        String cellValue = "";
        if (null != cell) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
//                    DecimalFormat df = new DecimalFormat("0");
//                    cellValue = df.format(cell.getNumericCellValue());
                    cellValue = cell.getNumericCellValue()+"";
                    break;

                case Cell.CELL_TYPE_FORMULA: {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cellValue = sdf.format(date);
                    } else {
                        cellValue = String.valueOf((int)cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                default:
                    cellValue = " ";
            }

        }
        return cellValue;
    }
}
