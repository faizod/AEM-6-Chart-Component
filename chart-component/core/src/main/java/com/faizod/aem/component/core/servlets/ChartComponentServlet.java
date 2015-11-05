/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets;

import com.day.cq.commons.TidyJSONWriter;
import com.faizod.aem.component.core.services.Datasource;
import com.faizod.aem.component.core.servlets.charts.ChartDataProvider;
import com.faizod.aem.component.core.servlets.charts.impl.LineChartDataProvider;
import com.faizod.aem.component.core.servlets.datasources.DatasourceParser;
import com.faizod.aem.component.core.servlets.datasources.impl.ExcelDatasourceParser;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Servlet for the faizod Chart Component.
 *
 * Only supports a GET request which returns the ready to use chart data as json.
 */
@SlingServlet(paths = {"/bin/faizod/chart"}, methods = {"GET"})
public class ChartComponentServlet extends SlingSafeMethodsServlet {

    private static final String PARAM_PATH = "nodePath";

    private static final String PROP_DATASOURCE_TYPE = "datasourceType";
    private static final String PROP_FILENAME = "fileName";

    @Reference
    private Datasource datasource;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        ChartDataProvider dataProvider = null;
        DatasourceParser datasourceParser = null;

        Map<Double, Double> values = new HashMap<Double, Double>();

        ResourceResolver resourceResolver = request.getResourceResolver();
        // extracts the components path
        String path = request.getParameter(PARAM_PATH);

        if (path == null || path.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Resource resource = resourceResolver.getResource(path);
        ValueMap valueMap = resource.getValueMap();

        String dsType = valueMap.get(PROP_DATASOURCE_TYPE, String.class);

        // read in datasource, can be a live chart datasource
        if (dsType.toLowerCase().equals("excel")) {
            datasourceParser = new ExcelDatasourceParser();
        }
        // TODO implement all parser

        // TODO move it to top, check nodetype above
        // String value = valueMap.get("sling:resourceType", String.class);
        if (resource.getResourceType().endsWith("chartcomponent")) {
        }
        Resource fileResource = resource.getChild("file");
        InputStream inputStream = fileResource.adaptTo(InputStream.class);

        values = datasourceParser.parse(inputStream);

        // convert data into a json for the specified chart type
        String chartType = "line";
        if (chartType.toLowerCase().equals("line")) {
            dataProvider = new LineChartDataProvider();
        }
        dataProvider.writeChartData(values, response.getWriter());


        // more parameter if needed
        // missing parameter:
        //  - chart type!
        //  - name
        //  - color (or a default color)
        // Q: Multi-Column
        //

//        Map<Double, Double> values = new HashMap<Double, Double>();
//
//        if (path != null) {
//            ResourceResolver resourceResolver = request.getResourceResolver();
//            values = datasource.readData(path, resourceResolver);
//        }
//
//        // extract following code
//        try {
//            TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
//            writer.array();
//            writer.object();
//            writer.key("key").value("Sample1");
//            writer.key("color").value("#d67777");
//            writer.key("values");
//            writer.array();
//            Iterator<Map.Entry<Double, Double>> iter = values.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry<Double, Double> entry = iter.next();
//                writer.array();
//                writer.value(entry.getKey());
//                writer.value(entry.getValue());
//                writer.endArray();
//                /*
//                writer.object();
//                writer.key("label").value(entry.getKey());
//                writer.key("value").value(entry.getValue());
//                writer.endObject();*/
//            }
//            writer.endArray();
//            writer.endObject();
//            writer.endArray();
//
//        } catch (JSONException e) {
//            //LOG.error("", e);
//        }
    }
}
