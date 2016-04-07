package com.vdlm.test.excel;

import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.vdlm.config.WebSecurityConfigurationAware;

public class TestPoi extends WebSecurityConfigurationAware {
	@Test
    public void test1(){
        try {
            Workbook wb = WorkbookFactory.create(new File("C:/Users/Think/Desktop/test.xls"));
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            System.out.println(cell.getCellType());
            System.out.println(cell.getStringCellValue());
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getCellValue(Cell c) {
        String o = null;
        switch (c.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            o = ""; break;
        case Cell.CELL_TYPE_BOOLEAN:
            o = String.valueOf(c.getBooleanCellValue()); break;
        case Cell.CELL_TYPE_FORMULA:
            o = String.valueOf(c.getCellFormula()); break;
        case Cell.CELL_TYPE_NUMERIC:
            o = String.valueOf(c.getNumericCellValue()); break;
        case Cell.CELL_TYPE_STRING:
            o = c.getStringCellValue(); break;
        default:
            o = null;
            break;
        }
        return o;
    }
    
    @Test
    public void testList01() {
        try {
            Workbook wb = WorkbookFactory.create(new File("C:/Users/Think/Desktop/test.xls"));
            Sheet sheet = wb.getSheetAt(0);
            //获取一共多少行
            System.out.println(sheet.getLastRowNum());
            Row row = null;
            for(int i=0;i<sheet.getLastRowNum();i++) {
                row = sheet.getRow(i);
                //获取一行多少列
                for(int j=row.getFirstCellNum();j<row.getLastCellNum();j++) {
                    //getCellValue可以根据不同的类型获取一个String类型的值
                    System.out.print(getCellValue(row.getCell(j))+"--");
                }
                System.out.println();
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void writerPoi(){
        Workbook wb = new HSSFWorkbook();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("C:/Users/Think/Desktop/test.xls");
            Sheet sheet = wb.createSheet("测试01");
            Row row = sheet.createRow(0);
            row.setHeightInPoints(20);//设置行高
            CellStyle cs = wb.createCellStyle();//设置样式
            cs.setAlignment(CellStyle.ALIGN_CENTER);
            cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            cs.setFillBackgroundColor((short)9877);
            Cell c = row.createCell(0);
            c.setCellValue("标识");
            c.setCellStyle(cs);
            c = row.createCell(1);
            c.setCellValue("用户名");
            c.setCellStyle(cs);
            wb.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
                
    }
}
