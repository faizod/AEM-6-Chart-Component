/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.faizod.aem.component.core.servlets.datasources.impl;

import com.faizod.aem.component.core.exceptions.DatasourceException;
import com.faizod.aem.component.core.servlets.datasources.DatasourceParser;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
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

    // Solution for Excel-File with multiple columns
    @Override
    public Map<Object, List<Object>> parseMultiColumn(InputStream inputStream) {
        Map<Object, List<Object>> map = new LinkedHashMap<Object, List<Object>>();

        // read in the Excel file
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                List<Cell> cells = new ArrayList<Cell>();
                short lineMin = row.getFirstCellNum();
                short lineMax = row.getLastCellNum();

                for (short index = lineMin; index < lineMax; index++)
                    cells.add(row.getCell(index));

                Object label = "";
                switch (cells.get(0).getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        label = cells.get(0).getNumericCellValue();
                        break;
                    case Cell.CELL_TYPE_STRING:
                        label = "" + (cells.get(0).getStringCellValue());
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
            LOG.error("Unable to read datasource.", e);
            throw new DatasourceException("Unable to read datasource.", e);
        } catch (InvalidFormatException e) {
            LOG.error("File Format not supported.", e);
            throw new DatasourceException("File Format not supported.", e);
        }
        return map;
    }

    @Override
    public boolean validate(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }

        boolean valid = true;
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            int firstVisibleTab = workbook.getFirstVisibleTab();
            valid = firstVisibleTab >= 0;
        } catch (IOException e) {
            valid = false;
        } catch (InvalidFormatException e) {
            valid = false;
        } catch (EncryptedDocumentException e) {
            valid = false;
        }
        return valid;
    }
}
