package com.faizod.aem.component.core.models;

import com.faizod.aem.component.core.services.Datasource;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.InputStream;

/**
 * @deprecated use the Servlet and a GET-Request to pull the data to draw a chart, this POJO is not used anymore.
 */
@Model(adaptables=Resource.class)
public class ChartModel {

    @Inject
    private SlingSettingsService settings;

    @Inject
    private Datasource datasource;

    private Resource resource;

    private String jsonChartData;

    @Inject @Named(value = "datasource/file")
    private InputStream inputStream;

    /**
     * Constructor with resource as parameter.
     */
    /*public ChartModel(Resource resource) {
        this.resource = resource;
    }*/

    @PostConstruct
    protected void init() {
        if (inputStream != null) {
            this.jsonChartData = this.datasource.convertData(inputStream);
        }
    }

    public String getJsonChartData() {
        return this.jsonChartData;
    }
}
