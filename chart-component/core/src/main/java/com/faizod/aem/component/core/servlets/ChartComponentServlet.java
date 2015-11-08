/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets;

import com.faizod.aem.component.core.servlets.charts.ChartDataProvider;
import com.faizod.aem.component.core.servlets.charts.impl.LineChartDataProvider;
import com.faizod.aem.component.core.servlets.datasources.DatasourceParser;
import com.faizod.aem.component.core.servlets.datasources.impl.ExcelDatasourceParser;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Servlet for the faizod Chart Component.
 * <p>
 * Only supports a GET request which returns the ready to use chart data as json.
 */
@SlingServlet(paths = {"/bin/faizod/chart"}, methods = {"GET"})
public class ChartComponentServlet extends SlingSafeMethodsServlet {

    private final static Logger LOG = LoggerFactory.getLogger(ChartComponentServlet.class);

    private static final String PARAM_PATH = "nodePath";

    private static final String PROP_DATASOURCE_TYPE = "datasourceType";
    private static final String PROP_FILENAME = "fileName";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        LOG.info("Handle Request...");

        ChartDataProvider dataProvider = null;
        DatasourceParser datasourceParser = null;

        Map<String, List<Object>> chartData;

        ResourceResolver resourceResolver = request.getResourceResolver();
        // extracts the components path
        String path = request.getParameter(PARAM_PATH);

        if (path == null || path.isEmpty()) {
            LOG.warn("No Chart component defined.");
            errorResponse(response);
            return;
        }

        Resource resource = resourceResolver.getResource(path);
        ValueMap valueMap = resource.getValueMap();

        String datasourceType = valueMap.get(PROP_DATASOURCE_TYPE, String.class);

        // read in datasource, can be a live chart datasource
        if (datasourceType.toLowerCase().equals("excel")) {
            datasourceParser = new ExcelDatasourceParser();
        }
        // TODO implement all parser


        Resource fileResource = resource.getChild("datasource/file");
        if (fileResource == null) {
            LOG.warn("No Datasource fdefined.");
            errorResponse(response);
            return;
        }
        InputStream inputStream = fileResource.adaptTo(InputStream.class);

        chartData = datasourceParser.parseMultiColumn(inputStream);

        // convert data into a json for the specified chart type
        String chartType = valueMap.get("graphType", "line");
        if (chartType.toLowerCase().equals("line")) {
            dataProvider = new LineChartDataProvider();
        }
        dataProvider.writeMultiColumnChartData(chartData, resource, response.getWriter());
    }

    private void processRequest() {

    }

    private void errorResponse(SlingHttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
