package com.mannetroll.metrics.logger;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.status.StatusLogger;

import com.mannetroll.metrics.util.JsonUtil;
import com.mannetroll.metrics.util.MetricEventDataBuilder;

/**
 * @author mannetroll
 */
@Plugin(name = "MetricJsonLayout", category = "Core", elementType = "layout", printObject = true)
public class MetricJsonLayout extends AbstractStringLayout {

	private final boolean locationInfo;
	private final boolean b3TracingInfo;
	private final String application;
	private final String namespace;
	private final boolean sort;
	private final MetricEventDataBuilder metricsData = new MetricEventDataBuilder();

	protected MetricJsonLayout(boolean locationInfo, String application, String namespace, Charset charset,
			boolean b3TracingInfo, boolean sort) {
		super(charset);
		this.locationInfo = locationInfo;
		this.b3TracingInfo = b3TracingInfo;
		this.application = application;
		this.namespace = namespace;
		this.sort = sort;
	}

	@PluginFactory
	public static MetricJsonLayout createLayout(@PluginAttribute(value = "locationInfo") boolean locationInfo,
			@PluginAttribute(value = "application") String application, @PluginAttribute("namespace") String namespace,
			@PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset,
			@PluginAttribute(value = "b3TracingInfo") boolean b3TracingInfo,
			@PluginAttribute(value = "sort") boolean sort) {
		return new MetricJsonLayout(locationInfo, application, namespace, charset, b3TracingInfo, sort);
	}

	@Override
	public String toSerializable(LogEvent event) {
		try {
			Map<String, Object> logData = metricsData.toJson(event, application, namespace, locationInfo, b3TracingInfo,
					sort);

			return JsonUtil.toJson(logData);
		} catch (Exception e) {
			StatusLogger.getLogger().error("Could not write JSON: " + e.getMessage(), e);
		}

		return "";
	}
}
