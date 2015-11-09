/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets.charts.impl;

import com.day.cq.commons.TidyJSONWriter;
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

    @Override
    public void writeChartData(Map<Double, Double> chartData, Writer writer) {

        try {
            TidyJSONWriter jsonWriter = new TidyJSONWriter(writer);
            jsonWriter.array();
            jsonWriter.object();
            // TODO: no static values here!, must be provided by the caller!
            jsonWriter.key("key").value("Sample1");
            jsonWriter.key("color").value("#d67777");
            jsonWriter.key("values");
            jsonWriter.array();
            Iterator<Map.Entry<Double, Double>> iter = chartData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Double, Double> entry = iter.next();

                jsonWriter.object();
                jsonWriter.key("x").value(entry.getKey());
                jsonWriter.key("y").value(entry.getValue());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
            jsonWriter.endArray();

        } catch (JSONException e) {
            LOG.error("Unable to write json data. ", e);
        }
    }

    @Override
    public void writeMultiColumnChartData(Map<String, List<Object>> chartData, Resource resource, Writer writer) {

        int dataColumns = chartData.get("labels").size();

        ValueMap properties = resource.getValueMap();

        String xAxisLabel = properties.get("xAxisLabel", "");
        String yAxisLabel = properties.get("yAxisLabel" , "");

        Boolean showXAxis = Boolean.valueOf(properties.get("showXAxis", "false"));
        Boolean showYAxis = Boolean.valueOf(properties.get("showYAxis", "false"));
        Boolean showLegend = Boolean.valueOf(properties.get("showLegend", "false"));
        Boolean guideline = Boolean.valueOf(properties.get("guideline", "false"));

        Map<Integer, Configuration> configurations = parseConfig(properties.get("config", "[]"));

        try {
            TidyJSONWriter jsonWriter = new TidyJSONWriter(writer);
            jsonWriter.setTidy(true);
            jsonWriter.object();
            jsonWriter.key("showXAxis").value(showXAxis);
            jsonWriter.key("xAxisLabel").value(xAxisLabel);
            jsonWriter.key("showYAxis").value(showYAxis);
            jsonWriter.key("yAxisLabel").value(yAxisLabel);
            jsonWriter.key("showLegend").value(showLegend);
            jsonWriter.key("useGuideline").value(guideline);

            jsonWriter.key("lines").array();
            for(int index = 0;index < dataColumns;index++){

                Configuration config = configurations.get(index);

                jsonWriter.object();
                if (config != null && config.getName() != null && !config.getName().isEmpty()) {
                    jsonWriter.key("key").value(config.getName());
                } else {
                    jsonWriter.key("key").value(chartData.get("labels").get(index));
                }
                if (config != null && config.getColor() != null && !config.getColor().isEmpty()) {
                    jsonWriter.key("color").value(config.getColor());
                } else {
                    jsonWriter.key("color").value(chartData.get("colors").get(index));
                }
                if (config != null && config.isArea()) {
                    jsonWriter.key("area").value("true");
                }
                jsonWriter.key("values");
                jsonWriter.array();
                Iterator<Map.Entry<String, List<Object>>> iter = chartData.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, List<Object>> entry = iter.next();
                    if(entry.getKey().equals("labels") || entry.getKey().equals("colors"))
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

        } catch (JSONException e) {
            LOG.error("Unable to write json data. ", e);
        }
    }

    private Map<Integer, Configuration> parseConfig(String jsonConfig) {

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
            LOG.error("JSON Config is not a valid JSON Array.");
        }
        return configurations;
    }

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
