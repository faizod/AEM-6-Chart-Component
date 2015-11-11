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
package com.faizod.aem.component.core.servlets;

import com.faizod.aem.component.core.exceptions.ConfigurationException;
import com.faizod.aem.component.core.exceptions.DatasourceException;
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
 * Retrieves and aggregates the chart data and writes the aggregated data as json into the response.
 * <p>
 * Only supports GET requests.
 */
@SlingServlet(paths = {"/bin/faizod/chart"}, methods = {"GET"})
public class ChartComponentServlet extends SlingSafeMethodsServlet {

    private final static Logger LOG = LoggerFactory.getLogger(ChartComponentServlet.class);

    private static final String PARAM_PATH = "nodePath";

    // Names of general properties
    private static final String PROP_DATASOURCE_TYPE = "datasourceType";
    private static final String PROP_CHART_TYPE = "chartType";

    // Supported

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        LOG.info("Handle Request...");

        ChartDataProvider dataProvider;
        DatasourceParser datasourceParser;

        Map<Object, List<Object>> chartData;

        ResourceResolver resourceResolver = request.getResourceResolver();
        // extracts the components path
        String path = request.getParameter(PARAM_PATH);

        try {
            if (path == null || path.isEmpty()) {
                LOG.warn("No Chart component found.");
                throw new ConfigurationException("No Chart component found.");
            }

            Resource resource = resourceResolver.getResource(path);
            ValueMap valueMap = resource.getValueMap();

            String datasourceType = valueMap.get(PROP_DATASOURCE_TYPE, String.class);
            // read in datasource
            if (datasourceType.toLowerCase().equals("excel")) {
                datasourceParser = new ExcelDatasourceParser();
            } else {
                // defaults to Excel
                datasourceParser = new ExcelDatasourceParser();
            }

            Resource fileResource = resource.getChild("datasource/file");
            if (fileResource == null) {
                LOG.warn("No Datasource defined.");
                throw new DatasourceException("");
            }
            InputStream inputStream = fileResource.adaptTo(InputStream.class);

            chartData = datasourceParser.parseMultiColumn(inputStream);

            String chartType = valueMap.get(PROP_CHART_TYPE, String.class);
            // convert data into a json for the specified chart type
            if (chartType.toLowerCase().equals("line")) {
                dataProvider = new LineChartDataProvider();
            } else {
                // defaults to line chart
                dataProvider = new LineChartDataProvider();
            }

            dataProvider.writeMultiColumnChartData(chartData, resource, response.getWriter());
        } catch (DatasourceException e) {
            errorResponse(response, HttpServletResponse.SC_NOT_FOUND);
        } catch (ConfigurationException e) {
            errorResponse(response, HttpServletResponse.SC_CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            errorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void errorResponse(SlingHttpServletResponse response, int status) {
        response.setStatus(status);
    }
}
