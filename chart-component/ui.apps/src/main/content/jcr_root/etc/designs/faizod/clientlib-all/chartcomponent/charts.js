/*
 * This is the comment of the function
 */

(function ($) {
    'use strict';

    // Global Variables
    var chart = $('.faizod.chart');
    var content = $('.faizod.chart .chart-content');
    var requestData = content.data('chart-path');
    var SERVLET_PATH = '/bin/faizod/chart';

    var chartData = {};

    var requestParams = {
        nodePath: requestData
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

        chart.xAxis // Chart x-axis settings
            .axisLabel('Time (ms)');

        chart.yAxis // Chart y-axis settings
            .axisLabel('Voltage (v)');

        /* Done setting the chart up? Time to render it! */// You need data...

        d3.select('.chart-content svg')
            .datum(chartData)
            .call(chart);

        // Update the chart when window resizes.
        nv.utils.windowResize(function() {
            chart.update()
        });
        return chart;
    });



    /***************************************************************************
     * Simple test data generator
     */
    function sinAndCos() {
        var sin = [], sin2 = [], cos = [];

        // Data is represented as an array of {x,y} pairs.
        for (var i = 0; i < 100; i++) {
            sin.push({
                x : i,
                y : Math.sin(i / 10)
            });
            sin2.push({
                x : i,
                y : Math.sin(i / 10) * 0.25 + 0.5
            });
            cos.push({
                x : i,
                y : .5 * Math.cos(i / 10)
            });
        }

        // Line chart data should be sent as an array of series objects.
        return [ {
            values : sin, // values - represents the array of {x,y} data
            // points
            key : 'Sine Wave', // key - the name of the series.
            color : '#ff7f0e' // color - optional: choose your own line color.
        }, {
            values : cos,
            key : 'Cosine Wave',
            color : '#2ca02c'
        }, {
            values : sin2,
            key : 'Another sine wave',
            color : '#7777ff',
            area : true
            // area - set to true if you want this line to turn into a filled area
            // chart.
        } ];
    }


})(jQuery);
