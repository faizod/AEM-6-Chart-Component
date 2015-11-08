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
import org.apache.sling.commons.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;
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

        try {
            TidyJSONWriter jsonWriter = new TidyJSONWriter(writer);
            jsonWriter.setTidy(true);
            jsonWriter.object();
            jsonWriter.key("xAxisLabel").value(xAxisLabel);
            jsonWriter.key("yAxisLabel").value(yAxisLabel);

            jsonWriter.key("lines").array();
            for(int index = 0;index < dataColumns;index++){
                jsonWriter.object();
                jsonWriter.key("key").value(chartData.get("labels").get(index));
                jsonWriter.key("color").value(chartData.get("colors").get(index));
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
}
