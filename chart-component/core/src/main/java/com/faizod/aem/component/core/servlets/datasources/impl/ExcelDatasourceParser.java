/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets.datasources.impl;

import com.faizod.aem.component.core.servlets.datasources.DatasourceParser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Parser for Excel datasources.
 */
public class ExcelDatasourceParser implements DatasourceParser {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelDatasourceParser.class);

    @Override
    public Map<Double, Double> parse(InputStream is) {

        Map<Double, Double> map = new HashMap<Double, Double>();

        // read in Excel file
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);

            // CellRefernce or iterate over rows?
            //FIXME: Example for Table with two columns, first column label, second column value
            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                Cell labelCell = row.getCell(1);
                Cell valueCell = row.getCell(2);

                Double label;
                Double value;

                switch (labelCell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        label = Double.valueOf(labelCell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        label = labelCell.getNumericCellValue();
                        break;
                    default:
                        label = 0.0d;
                }

                switch (valueCell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        value = Double.valueOf(valueCell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        value = valueCell.getNumericCellValue();
                        break;
                    default:
                        value = 0.0d;
                }
                map.put(label, value);
            }
        } catch (IOException e) {
            LOG.error("Excle file not readable. ", e);
        }
        return map;
    }

    // Solution which reads only the first 2 columns
    private Map<Double, Double> processFile(InputStream inputStream) {
        Map<Double, Double> map = new HashMap<Double, Double>();

        // Read the Excel file
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            // CellRefernce or iterate over rows?
            //FIXME: Example for Table with two columns, first column label, second column value
            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                Cell labelCell = row.getCell(1);
                Cell valueCell = row.getCell(2);

                Double label;
                Double value;

                switch (labelCell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        label = Double.valueOf(labelCell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        label = labelCell.getNumericCellValue();
                        break;
                    default:
                        label = 0.0d;
                }

                switch (valueCell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        value = Double.valueOf(valueCell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        value = valueCell.getNumericCellValue();
                        break;
                    default:
                        value = 0.0d;
                }
                map.put(label, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Solution which dynamically reads multiple columns
    @Override
    public Map<String, List<Object>> parseMultiColumn(InputStream inputStream) {
        Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();

        // read in the Excel file
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // CellRefernce or iterate over rows?
            //FIXME: Example for Table with two columns, first column label, second column value
            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                List<Cell> cells = new ArrayList<Cell>();
                short lineMin = row.getFirstCellNum();
                short lineMax = row.getLastCellNum();

                for (short index = lineMin; index < lineMax; index++)
                    cells.add(row.getCell(index));

                String label = "";
                switch (cells.get(0).getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        label = "" + (cells.get(0).getNumericCellValue());
                        break;
                    case Cell.CELL_TYPE_STRING:
                        label = cells.get(0).getStringCellValue();
                        break;
                    default:
                        break;
                }

                List<Object> values = new ArrayList<Object>();

                for (short index = 1; index < (lineMax - lineMin); index++) {
                    Object value;

                    switch (cells.get(index).getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            value = cells.get(index).getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            value = cells.get(index).getNumericCellValue();
                            break;
                        default:
                            value = new Object();
                            break;
                    }
                    values.add(value);
                }
                map.put(label, values);
            }
        } catch (IOException e) {
            LOG.error("");
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return map;
    }
}
