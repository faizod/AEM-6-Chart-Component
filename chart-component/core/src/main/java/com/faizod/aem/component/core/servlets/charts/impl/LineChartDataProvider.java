/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets.charts.impl;

import com.day.cq.commons.TidyJSONWriter;
import com.faizod.aem.component.core.servlets.charts.ChartDataProvider;
import org.apache.sling.commons.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class LineChartDataProvider implements ChartDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(LineChartDataProvider.class);

    @Override
    public void writeChartData(Map<Double, Double> chartData, Writer writer) {

        // extract following code
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
}
