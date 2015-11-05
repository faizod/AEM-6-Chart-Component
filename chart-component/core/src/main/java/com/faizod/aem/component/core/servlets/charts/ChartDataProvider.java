/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets.charts;

import java.io.Writer;
import java.util.Map;

/**
 *
 */
public interface ChartDataProvider {

    void writeChartData(Map<Double, Double> chartData, Writer writer);

}
