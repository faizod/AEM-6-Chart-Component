package com.faizod.aem.component.core.services.impl;

import com.day.cq.commons.TidyJSONWriter;
import com.faizod.aem.component.core.services.Datasource;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.commons.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

/**
 * Implements Datasource for Excel-Files.
 *
 * @deprecated
 */
@Component
@Service
public class ExcelDatasource implements Datasource {

    @Activate
    public void init() {

    }

    // DEPRECATED
    /*@Override
    public String convertData(InputStream inputStream) {

        Map<Double, Double> map = processFile(inputStream);

        StringWriter strWriter = new StringWriter();

        // extract following code
        try {
            TidyJSONWriter writer = new TidyJSONWriter(strWriter);
            writer.array();
            writer.object();
            writer.key("key").value("Sample1");
            writer.key("color").value("#d67777");
            writer.key("values");
            writer.array();
            Iterator<Map.Entry<Double, Double>> iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Double, Double> entry = iter.next();
                writer.array();
                writer.value(entry.getKey());
                writer.value(entry.getValue());
                writer.endArray();
                *//*
                writer.object();
                writer.key("label").value(entry.getKey());
                writer.key("value").value(entry.getValue());
                writer.endObject();*//*
            }
            writer.endArray();
            writer.endObject();
            writer.endArray();

        } catch (JSONException e) {
            //LOG.error("", e);
        }
        return strWriter.toString();
    }*/

    @Override
    public String convertData(InputStream inputStream) {

        Map<String, List<Object>> map = processFileMultible(inputStream);

        StringWriter strWriter = new StringWriter();

        int dataColumns = map.get("labels").size();

        // extract following code
        try {
            TidyJSONWriter writer = new TidyJSONWriter(strWriter);
            writer.setTidy(true);
            writer.array();

            for (int index = 0; index < dataColumns; index++) {
                writer.object();
                writer.key("key").value(map.get("labels").get(index));
                writer.key("color").value(map.get("colors").get(index));
                writer.key("values");
                writer.array();
                Iterator<Map.Entry<String, List<Object>>> iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, List<Object>> entry = iter.next();
                    if (entry.getKey().equals("labels") || entry.getKey().equals("colors"))
                        continue;

                    writer.object();
                    writer.key("x").value(entry.getKey());
                    writer.key("y").value(entry.getValue().get(index));
                    writer.endObject();
                }
                writer.endArray();
                writer.endObject();
            }

            writer.endArray();

        } catch (JSONException e) {
            //LOG.error("", e);
        }

        return strWriter.toString();
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
    private Map<String, List<Object>> processFileMultible(InputStream inputStream) {
        Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();

        // Read the Excel file
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

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
            e.printStackTrace();
        }
        return map;
    }

}
