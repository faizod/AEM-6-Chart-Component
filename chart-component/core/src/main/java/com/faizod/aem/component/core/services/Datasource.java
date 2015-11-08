package com.faizod.aem.component.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @deprecated
 */
public interface Datasource {

    String convertData(InputStream inputStream);

}
