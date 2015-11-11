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
package com.faizod.aem.component.core.servlets.charts.impl;

import com.day.cq.commons.TidyJSONWriter;
import com.faizod.aem.component.core.exceptions.ConfigurationException;
import com.faizod.aem.component.core.servlets.charts.ChartDataProvider;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the ChartDataProvider interface for line charts.
 */
public class LineChartDataProvider implements ChartDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(LineChartDataProvider.class);

    // keys
    private static final String KEY_LABELS = "labels";
    private static final String KEY_COLORS = "colors";

    private static final String PROP_X_AXIS_LABEL = "xAxisLabel";
    private static final String PROP_Y_AXIS_LABEL = "yAxisLabel";
    private static final String PROP_SHOW_X_AXIS = "showXAxis";
    private static final String PROP_SHOW_Y_AXIS = "showYAxis";
    private static final String PROP_SHOW_LEGEND = "showLegend";
    private static final String PROP_USE_GUIDELINE = "useGuideline";

    private static final String PROP_X_AXIS_FORMAT = "xAxisFormat";
    private static final String PROP_Y_AXIS_FORMAT = "yAxisFormat";

    private static final String VALUE_FALSE = "false";

    @Override
    public void writeMultiColumnChartData(Map<Object, List<Object>> chartData, Resource resource, Writer writer) {

        int dataColumns = chartData.get(KEY_LABELS).size();

        ValueMap properties = resource.getValueMap();

        String xAxisLabel = properties.get(PROP_X_AXIS_LABEL, "");
        String yAxisLabel = properties.get(PROP_Y_AXIS_LABEL, "");

        String xAxisFormat = properties.get(PROP_X_AXIS_FORMAT, "");
        String yAxisFormat = properties.get(PROP_Y_AXIS_FORMAT, "");

        Boolean showXAxis = Boolean.valueOf(properties.get(PROP_SHOW_X_AXIS, VALUE_FALSE));
        Boolean showYAxis = Boolean.valueOf(properties.get(PROP_SHOW_Y_AXIS, VALUE_FALSE));
        Boolean showLegend = Boolean.valueOf(properties.get(PROP_SHOW_LEGEND, VALUE_FALSE));
        Boolean guideline = Boolean.valueOf(properties.get(PROP_USE_GUIDELINE, VALUE_FALSE));

        Map<Integer, Configuration> configurations = mapConfig(properties.get("config", "[]"));

        try {
            TidyJSONWriter jsonWriter = new TidyJSONWriter(writer);
            jsonWriter.setTidy(true);
            jsonWriter.object();
            jsonWriter.key(PROP_SHOW_X_AXIS).value(showXAxis);
            jsonWriter.key(PROP_X_AXIS_LABEL).value(xAxisLabel);
            jsonWriter.key(PROP_SHOW_Y_AXIS).value(showYAxis);
            jsonWriter.key(PROP_Y_AXIS_LABEL).value(yAxisLabel);
            jsonWriter.key(PROP_SHOW_LEGEND).value(showLegend);
            jsonWriter.key(PROP_USE_GUIDELINE).value(guideline);

            jsonWriter.key(PROP_X_AXIS_FORMAT).value(xAxisFormat);
            jsonWriter.key(PROP_Y_AXIS_FORMAT).value(yAxisFormat);

            jsonWriter.key("lines").array();
            for(int index = 0;index < dataColumns;index++){

                Configuration config = configurations.get(index);

                jsonWriter.object();
                if (config != null && config.getName() != null && !config.getName().isEmpty()) {
                    jsonWriter.key("key").value(config.getName());
                } else {
                    jsonWriter.key("key").value(chartData.get(KEY_LABELS).get(index));
                }
                if (config != null && config.getColor() != null && !config.getColor().isEmpty()) {
                    jsonWriter.key("color").value(config.getColor());
                } else {
                    jsonWriter.key("color").value(chartData.get(KEY_COLORS).get(index));
                }
                if (config != null && config.isArea()) {
                    jsonWriter.key("area").value("true");
                }
                jsonWriter.key("values");
                jsonWriter.array();
                Iterator<Map.Entry<Object, List<Object>>> iter = chartData.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Object, List<Object>> entry = iter.next();
                    if(entry.getKey().equals(KEY_LABELS) || entry.getKey().equals(KEY_COLORS))
                        continue;

                    jsonWriter.object();
                    jsonWriter.key("x").value(entry.getKey());
                    jsonWriter.key("y").value(entry.getValue().get(index));
                    jsonWriter.endObject();
                }
                jsonWriter.endArray();
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();

        } catch (Exception e) {
            LOG.error("Unable to write json data. ", e);
            throw new ConfigurationException("Unable to write json data. ", e);
        }
    }

    private Map<Integer, Configuration> mapConfig(String jsonConfig) {

        Map<Integer, Configuration> configurations = new HashMap<Integer, Configuration>();
        try {
            JSONArray array = new JSONArray(jsonConfig);
            for (int i=0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String color = obj.getString("color");
                boolean area = obj.getBoolean("area");

                configurations.put(i, new Configuration(name, color, area));
            }
        } catch (JSONException e) {
            LOG.error("JSON Config is not a valid JSON Array.", e);
            throw new ConfigurationException("JSON Config is not a valid JSON Array.", e);
        }
        return configurations;
    }

    /**
     * Stores the configuration for this chart.
     */
    private class Configuration {
        String name;
        String color;
        boolean area;

        public Configuration(String name, String color, boolean area) {
            this.name = name;
            this.color = color;
            this.area = area;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public boolean isArea() {
            return area;
        }

        public void setArea(boolean area) {
            this.area = area;
        }
    }
}
