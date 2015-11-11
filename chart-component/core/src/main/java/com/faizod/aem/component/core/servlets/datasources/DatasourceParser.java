/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 * All rights reserved.
 */
package com.faizod.aem.component.core.servlets.datasources;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Parser for Datasource.
 */
public interface DatasourceParser {

    Map<String, List<Object>> parseMultiColumn(InputStream inputStream);

    boolean validate(InputStream inputStream);

}
