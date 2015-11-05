/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets.datasources.impl;

import com.faizod.aem.component.core.servlets.datasources.DatasourceParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Parser for Excle datasources.
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
}
