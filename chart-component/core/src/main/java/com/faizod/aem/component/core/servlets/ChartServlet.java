package com.faizod.aem.component.core.servlets;

import com.day.cq.commons.TidyJSONWriter;
import com.faizod.aem.component.core.services.Datasource;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Servlet for the faizod Chart Component.
 */
@SlingServlet(paths = {"/bin/faizod/chart"}, methods = {"GET"})
public class ChartServlet extends SlingSafeMethodsServlet {

    private static final String PARAM_PATH = "nodePath";

    @Reference
    private Datasource datasource;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getParameter(PARAM_PATH);
        // more parameter if needed
        // missing parameter:
        //  - chart type!
        //  - name
        //  - color (or a default color)
        // Q: Multi-Column
        //

        Map<Double, Double> values = new HashMap<Double, Double>();

        if (path != null) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            values = datasource.readData(path, resourceResolver);
        }

        // extract following code
        try {
            TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
            writer.array();
            writer.object();
            writer.key("key").value("Sample1");
            writer.key("color").value("#d67777");
            writer.key("values");
            writer.array();
            Iterator<Map.Entry<Double, Double>> iter = values.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Double, Double> entry = iter.next();
                writer.array();
                writer.value(entry.getKey());
                writer.value(entry.getValue());
                writer.endArray();
                /*
                writer.object();
                writer.key("label").value(entry.getKey());
                writer.key("value").value(entry.getValue());
                writer.endObject();*/
            }
            writer.endArray();
            writer.endObject();
            writer.endArray();

        } catch (JSONException e) {
            //LOG.error("", e);
        }
    }
}
