package com.faizod.aem.component.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 *
 */
public interface Datasource {

    void readData(String contentNodePath);

    Map<Double, Double> readData(String contentNodePath, ResourceResolver resourceResolver);

    String convertData(InputStream inputStream);

}
