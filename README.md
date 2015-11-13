# faizod.AEM-Chart Component

    * AEM 6 Component
    * helps you to create complexe and great looking business charts in zero time
    * provides a chart types which are fully configurable


## Features

Until now the following features are implemented:

    * import chart data from excel files, supported formats are Excel 2007 and Excel 2010
    * draws line charts
        * (customizable labels for x and y axis)
        * customizable formating of the axis labels
        * interactive guidelines
        * visualization of each line can be configured, for example: single lines can be drawn as areas (Flächendarstellung), the color and label of each line can be individually defined

## Installation

    * short description how to build and install our bundle (over the package explorer, with screenshot)
    * reuse already existing build manual

### How to build

To build all the modules run in the project root directory the following command with Maven at leased 3.1.x:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## Quick Start

    comming soon...

## Plans for the Future

plans for the future

    * more chart types (bar, pie chart, stacked Area)
        *line with bar chart
    * more import formats (json, csv, live!?)
    * animated charts?
 
## 3rd Party Libraries

    * Apache POI - the Java API for Microsoft Documents, Version 3.13 https://poi.apache.org/
    * D3.js - Data-Driven Documents https://github.com/mbostock/d3/
    * NVD3.js - A reusable D3 charting library https://github.com/novus/nvd3

## License

Apache License, Version 2.0

    http://www.apache.org/licenses/LICENSE-2.0
