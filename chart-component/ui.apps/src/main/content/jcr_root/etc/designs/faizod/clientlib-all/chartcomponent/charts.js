/*
 * This is the comment of the function
 */

(function ($) {
    'use strict';

    // Global Variables
    var chart = $('.faizod.chart');
    var content = $('.faizod.chart .chart-content');
    var contentPath = content.data('chart-path');

    function load() {
        alert("Loaded and Ready with path " + contentPath);
    }

})(jQuery);
