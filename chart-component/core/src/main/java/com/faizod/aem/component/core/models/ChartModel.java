/*
 * Copyright (c) 2015 faizod GmbH & Co. KG
 * Großenhainer Straße 101, D-01127 Dresden, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.faizod.aem.component.core.models;

import com.faizod.aem.component.core.servlets.datasources.DatasourceParser;
import com.faizod.aem.component.core.servlets.datasources.impl.ExcelDatasourceParser;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;

/**
 * Model POJO for chart component.
 */
@Model(adaptables=Resource.class)
public class ChartModel {

    @Inject @Named(value = "datasource/file") @Optional
    private InputStream inputStream;

    @Inject @Named(value = "data") @Optional
    private String data;

    private boolean configured = true;

    private boolean valid = false;

    private DatasourceParser datasourceParser;

    @PostConstruct
    protected void init() {
        if ((this.data == null || this.data.isEmpty()) && this.inputStream == null) {
            this.configured = false;
        }

        if (inputStream != null) {
            datasourceParser = new ExcelDatasourceParser();
            this.valid = datasourceParser.validate(inputStream);
        }
    }

    public boolean isConfigured() {
        return this.configured;
    }

    public boolean isValid() {
        return valid;
    }
}
