/*
 * This is the comment of the function
 */

(function ($) {
    'use strict';

    // Global Variables
    var chart = $('.faizod.chart');
    var SERVLET_PATH = '/bin/faizod/chart';

    /**
     * Pulls the chart data from ChartDataServlet by a simple GET-Request.
     */
    function loadData(contentPath) {
        var chartData = {};

        var requestParams = {
            nodePath: contentPath
        };

        $.ajax({
            method: 'GET',
            url: SERVLET_PATH,
            data: requestParams,
            async: false,
            dataType: 'json'
        }).done(function( data ) {
            chartData = data;
        });

        return chartData;
    }

    function updateComponent(element) {
        console.log("Update Component");
        var input = $(element);
        var componentId = $(element).attr('id');
        var content = $(input[0]).find('div.chart-content');
        var contentPath = content.data('chart-path');

        debugger;

        var chartData = loadData(contentPath);

        /*
         * These lines are all chart setup. Pick and choose which chart features you
         * want to utilize.
         */
        nv.addGraph(function() {
            var chart = nv.models.lineChart().margin({left : 100})
                    .useInteractiveGuideline(true)
                    .transitionDuration(350)
                    .showLegend(true)
                    .showYAxis(true)
                    .showXAxis(true)
                ;

            if (chartData.xAxisLabel) {
                chart.xAxis // Chart x-axis settings
                    .axisLabel(chartData.xAxisLabel);
            }

            if (chartData.yAxisLabel) {
                chart.yAxis // Chart y-axis settings
                    .axisLabel(chartData.yAxisLabel);
            }
            /* Done setting the chart up? Time to render it! */

            d3.select('#'+componentId+' .chart-content svg')
                .datum(chartData.lines)
                .call(chart);

            // Update the chart when window resizes.
            nv.utils.windowResize(function() {
                chart.update()
            });
            return chart;
        });
    }

    chart.each(function() {
        console.log("Initial paint component");
        updateComponent(this);
    });

})(jQuery);
