/*
 *  Copyright (c) 2015 faizod GmbH & Co. KG
 *  Großenhainer Straße 101, D-01127 Dresden, Germany
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Clientside JS for the LiveChart Component.
 */
(function ($) {
    'use strict';

    // Global Variables
    var chart = $('.faizod.chart');
    var SERVLET_PATH = '/bin/faizod/chart';

    var ERROR_MSG = 'No content to display. Please check the configuration.';

    /**
     * Called when the chart is created or updated.
     */
    function updateComponent(element) {
        var componentId = $(element).attr('id');
        var content = $(element).find('div.chart-content');
        var contentPath = content.data('chart-path');

        var errorMsg = $(element).find('div.chart-error-message');

        // Pulls the chart data from ChartDataServlet by a simple GET-Request.
        var requestParams = {
            nodePath: contentPath
        };
        $.ajax({
            method: 'GET',
            url: SERVLET_PATH,
            data: requestParams,
            async: false,
            dataType: 'json'
        }).success(function (data) {
            content.removeClass('hidden');
            if (!errorMsg.hasClass('hidden')) {
                errorMsg.addClass('hidden');
            }
            drawChart(data, componentId);
        }).fail(function (jqXHR, textStatus) {
            errorMsg.text(ERROR_MSG);
            if (errorMsg.hasClass('hidden')) {
                errorMsg.removeClass('hidden');
            }
            content.addClass('hidden');
        });
    }

    /**
     * Draws the chart.
     */
    function drawChart(chartData, componentId) {

        // German localization
        var d3_locale_deDE = d3.locale({
            "decimal": ",",
            "thousands": ".",
            "grouping": [3],
            "currency": ["", " €"],
            "dateTime": "%a %b %e %X %Y",
            "date": "%d.%m.%Y",
            "time": "%H:%M:%S",
            "periods": ["AM", "PM"],
            "days": ["Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"],
            "shortDays": ["Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"],
            "months": ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"],
            "shortMonths": ["Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"]
        });
        d3.format = d3_locale_deDE.numberFormat;

        nv.addGraph(function () {
            // chart setup and configuration
            var chart = nv.models.lineChart()
                    .margin({left: chartData.marginLeft})
                    .showLegend(chartData.showLegend)
                    .showYAxis(chartData.showYAxis)
                    .showXAxis(chartData.showXAxis)
                    .noData("There is no Data to display.")
                    .options({
                        transitionDuration: 350,
                        useInteractiveGuideline: chartData.useGuideline
                    })
                ;

            chart.xAxis // Chart x-axis settings
                .axisLabel(chartData.xAxisLabel)
                // tickValues coming soon
                /*.tickValues(chartData.lines[0].values.map(function (d) {
                    return d.x;
                }))*/
                .tickFormat(function (d) {
                    return d3.format(chartData.xAxisFormat)(d);
                });

            chart.yAxis // Chart y-axis settings
                .axisLabel(chartData.yAxisLabel)
                .axisLabelDistance(55)
                .tickFormat(function (d) {
                    return d3.format(chartData.yAxisFormat)(d);
                });

            // render the chart
            d3.select('#' + componentId + ' .chart-content svg')
                .datum(chartData.lines)
                .call(chart);

            // Update the chart when window resizes.
            nv.utils.windowResize(function () {
                chart.update()
            });
            return chart;
        });
    }

    chart.each(function () {
        updateComponent(this);
    });

})(jQuery);
