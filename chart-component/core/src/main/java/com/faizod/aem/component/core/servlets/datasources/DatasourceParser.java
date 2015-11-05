/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets.datasources;

import java.io.InputStream;
import java.util.Map;

/**
 *
 */
public interface DatasourceParser {

    Map<Double, Double> parse(InputStream is);

}
