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

    private boolean show = true;

    @PostConstruct
    protected void init() {
        if ((this.data == null || this.data.isEmpty()) && this.inputStream == null) {
            this.show = false;
        }
    }

    public boolean getShow() {
        return this.show;
    }
}
