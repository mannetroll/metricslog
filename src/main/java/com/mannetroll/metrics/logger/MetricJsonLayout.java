package com.mannetroll.metrics.logger;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.status.StatusLogger;

import com.mannetroll.metrics.util.JsonUtil;
import com.mannetroll.metrics.util.MetricEventDataBuilder;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author mannetroll
 */
@Plugin(name = "MetricJsonLayout", category = "Core", elementType = "layout", printObject = true)
public class MetricJsonLayout extends AbstractStringLayout {

    private final boolean locationInfo;
    private final boolean b3TracingInfo;
    private final boolean osInfo;
    private final String application;
    private final String namespace;        
    private final MetricEventDataBuilder metricsData = new MetricEventDataBuilder();

    protected MetricJsonLayout(boolean locationInfo, boolean osInfo, String application, String namespace, Charset charset, boolean b3TracingInfo) {
        super(charset);
        this.locationInfo = locationInfo;
        this.b3TracingInfo = b3TracingInfo;
        this.osInfo = osInfo;
        this.application = application;
        this.namespace = namespace;        
    }

    @PluginFactory
    public static MetricJsonLayout createLayout(@PluginAttribute(value = "locationInfo") boolean locationInfo,
                                                @PluginAttribute(value = "osInfo", defaultBoolean = true) Boolean osInfo,
                                                @PluginAttribute("application") String application,
                                                @PluginAttribute("namespace") String namespace,
                                                @PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset,
                                                @PluginAttribute(value = "b3TracingInfo") boolean b3TracingInfo) {
        return new MetricJsonLayout(locationInfo, osInfo, application, namespace, charset ,b3TracingInfo);
    }

    @Override
    public String toSerializable(LogEvent event) {
        try {
            Map<String, Object> logData = metricsData.toJson(event, application, namespace, locationInfo, osInfo, b3TracingInfo);

            return JsonUtil.toJson(logData);
        } catch (Exception e) {
            StatusLogger.getLogger().error("Could not write JSON: " + e.getMessage(), e);
        }

        return "";
    }
}
