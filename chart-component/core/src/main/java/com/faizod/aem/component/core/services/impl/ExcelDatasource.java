package com.faizod.aem.component.core.services.impl;

import com.day.cq.commons.TidyJSONWriter;
import com.faizod.aem.component.core.services.Datasource;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implements Datasource for Excel-Files.
 */
@Component
@Service
public class ExcelDatasource implements Datasource {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public void init() {

    }


    @Override
    public void readData(String contentNodePath) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
        ResourceResolver resolver = null;

        try {
            resolver = resourceResolverFactory.getServiceResourceResolver(param);

            Resource res = resolver.getResource(contentNodePath);
            ValueMap readMap = res.getValueMap();
            //ModifiableValueMap modMap = res.adaptTo(ModifiableValueMap.class);

            String value = readMap.get("sling:resourceTyp", String.class);
            if (value.endsWith("chartcomponent")) {
                Resource fileResource = res.getChild("file");
                File file = fileResource.adaptTo(File.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Double, Double> readData(String contentNodePath, ResourceResolver resourceResolver) {

        Resource res = resourceResolver.getResource(contentNodePath);
        ValueMap readMap = res.getValueMap();
        //ModifiableValueMap modMap = res.adaptTo(ModifiableValueMap.class);

        String value = readMap.get("sling:resourceType", String.class);
        if (res.getResourceType().endsWith("chartcomponent")) {
            Resource fileResource = res.getChild("file");
            InputStream is = fileResource.adaptTo(InputStream.class);

            return processFile(is);
        }
        return new HashMap<Double, Double>();
    }

    @Override
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
                /*
                writer.object();
                writer.key("label").value(entry.getKey());
                writer.key("value").value(entry.getValue());
                writer.endObject();*/
            }
            writer.endArray();
            writer.endObject();
            writer.endArray();

        } catch (JSONException e) {
            //LOG.error("", e);
        }

        return strWriter.toString();
    }

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
}
