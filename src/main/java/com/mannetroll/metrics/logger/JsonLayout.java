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
import com.mannetroll.metrics.util.LoggingEventDataBuilder;

/**
 * @author mannetroll
 */
@Plugin(name = "JsonLayout", category = "Core", elementType = "layout", printObject = true)
public class JsonLayout extends AbstractStringLayout {

	private final boolean locationInfo;
	private final boolean b3TracingInfo;
	private final String application;
	private final String namespace;
	private final boolean sort;

	protected JsonLayout(boolean locationInfo, String application, String namespace, Charset charset,
			boolean b3TracingInfo, boolean sort) {
		super(charset);
		this.locationInfo = locationInfo;
		this.application = application;
		this.namespace = namespace;
		this.b3TracingInfo = b3TracingInfo;
		this.sort = sort;
	}

	@PluginFactory
	public static JsonLayout createLayout(@PluginAttribute(value = "locationInfo") boolean locationInfo,
			@PluginAttribute(value = "application") String application, @PluginAttribute("namespace") String namespace,
			@PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset,
			@PluginAttribute(value = "b3TracingInfo") boolean b3TracingInfo,
			@PluginAttribute(value = "sort") boolean sort) {
		return new JsonLayout(locationInfo, application, namespace, charset, b3TracingInfo, sort);
	}

	@Override
	public String toSerializable(LogEvent event) {
		try {
			LoggingEventDataBuilder logData = new LoggingEventDataBuilder();
			Map<String, Object> map = logData.getMap(event, application, namespace, locationInfo, b3TracingInfo, sort);

			return JsonUtil.toJson(map);
		} catch (Exception e) {
			StatusLogger.getLogger().error("Could not write JSON: " + e.getMessage(), e);
		}

		return "";
	}
}
