package com.faizod.aem.component.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;

/**
 *
 */
@Model(adaptables=Resource.class)
public class ChartModel {

    @PostConstruct
    protected void init() {

    }



}
