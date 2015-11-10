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

    /**
     * Called when the chart is created or updated.
     */
    function updateComponent(element) {
        console.log("Update Component");
        var input = $(element);
        var componentId = $(element).attr('id');
        var content = $(input[0]).find('div.chart-content');
        var contentPath = content.data('chart-path');

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
        }).done(function (data) {
            drawChart(data, componentId);
        }).fail(function (jqXHR, textStatus) {
            debugger;
            console.log("Bad request");
            // TODO: Error Handling, dont display anything or so!
            return;
        });
    }

    /**
     * Draws the chart.
     */
    function drawChart(chartData, componentId) {

        // chart setup
        nv.addGraph(function () {
            var chart = nv.models.lineChart()
                    .margin({left: 40})
                    .showLegend(chartData.showLegend)
                    .showYAxis(chartData.showYAxis)
                    .showXAxis(chartData.showXAxis)
                    .noData("There is no Data to display.")
                    .options({
                        transitionDuration: 350,
                        useInteractiveGuideline: chartData.useGuideline
                    })
                ;

            if (chartData.showXAxis && chartData.xAxisLabel) {
                chart.xAxis // Chart x-axis settings
                    .axisLabel(chartData.xAxisLabel);
            }

            if (chartData.showYAxis && chartData.yAxisLabel) {
                chart.yAxis // Chart y-axis settings
                    .axisLabel(chartData.yAxisLabel);
            }
            /* Done setting the chart up? Time to render it! */

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
        console.log("Initial paint component");
        updateComponent(this);
    });

})(jQuery);
