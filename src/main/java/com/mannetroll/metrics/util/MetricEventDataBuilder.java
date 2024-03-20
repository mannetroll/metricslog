package com.mannetroll.metrics.util;

import static com.mannetroll.metrics.util.MDCUtils.getMDCString;
import static com.mannetroll.metrics.util.MDCUtils.safePutValue;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.LogEvent;

import com.codahale.metrics.Snapshot;
import com.mannetroll.metrics.LogKeys;
import com.mannetroll.metrics.codahale.AppenderMetricsManager;
import com.mannetroll.metrics.codahale.AppenderTimer;

/**
 * @author mannetroll
 */
public class MetricEventDataBuilder {
	private static final String METRICS = "metrics";
	private final LoggingEventDataBuilder metricEventDataBuilder = new LoggingEventDataBuilder();

	public Map<String, Object> toJson(LogEvent event, String application, String namespace, boolean locationInfo,
			boolean b3TracingInfo, boolean sort) {
		Map<String, Object> map = metricEventDataBuilder.getMap(event, application, namespace, locationInfo,
				b3TracingInfo, sort);
		//
		// Add Metrics Event data
		String metrics_name = getMDCString(LogKeys.METRICS_NAME, event);
		// check if MDC info from AccessMetricServletFilter exists
		if (metrics_name != null) {
			AppenderTimer timer = (AppenderTimer) AppenderMetricsManager.getAppenderTimer(metrics_name);
			// Register start with nanotime OR elapsed with responsetime_ms
			String nanotimeStr = getMDCString(LogKeys.NANOTIME, event);
			String responsetime_msStr = getMDCString(LogKeys.RESPONSETIME_MS, event);
			if (nanotimeStr != null) {
				long nanotime = Long.parseLong(nanotimeStr);
				long responsetime_ns = timer.timestartstop(nanotime, System.nanoTime());
				addToJSON(map, timer, responsetime_ns, event);
			} else if (responsetime_msStr != null) {
				long responsetime_ms = Long.parseLong(responsetime_msStr);
				long responsetime_ns = timer.timestartstop(0, TimeUnit.MILLISECONDS.toNanos(responsetime_ms));
				addToJSON(map, timer, responsetime_ns, event);
			}
		}

		return map;
	}

	private void addToJSON(Map<String, Object> tmp, AppenderTimer timer, long responsetime_ns, LogEvent event) {
		tmp.put(LogKeys.TYPE, METRICS);
		tmp.put(LogKeys.HTTP_RESPONSE_STATUS_CODE, toInt(getMDCString(LogKeys.RESPONSE_STATUS, event)));
		safePutValue(tmp, LogKeys.DOMAIN, getMDCString(LogKeys.DOMAIN, event));
		safePutValue(tmp, LogKeys.URI, getMDCString(LogKeys.REQUEST_URI, event));
		safePutValue(tmp, LogKeys.URL, getMDCString(LogKeys.REQUEST_URL, event));
		safePutValue(tmp, LogKeys.REQUEST_BODY, getMDCString(LogKeys.REQUEST_BODY, event));
		safePutValue(tmp, LogKeys.RESPONSE_BODY, getMDCString(LogKeys.RESPONSE_BODY, event));
		safePutValue(tmp, LogKeys.HTTP_REQUEST_METHOD, getMDCString(LogKeys.HTTP_REQUEST_METHOD, event));
		safePutValue(tmp, LogKeys.QUERY, getMDCString(LogKeys.REQUEST_QUERY, event));
		safePutValue(tmp, LogKeys.USERAGENT, getMDCString(LogKeys.REQUEST_USER_AGENT, event));
		tmp.put(LogKeys.RESPONSETIME_MS, TimeUnit.NANOSECONDS.toMillis(responsetime_ns));
		tmp.put(LogKeys.COUNT, timer.getCount());
		tmp.put(LogKeys.MEANRATE, (float) timer.getMeanRate());
		tmp.put(LogKeys.ONEMINUTERATE, (float) timer.getOneMinuteRate());
		tmp.put(LogKeys.FIVEMINUTERATE, (float) timer.getFiveMinuteRate());
		tmp.put(LogKeys.FIFTEENMINUTERATE, (float) timer.getFifteenMinuteRate());
		Snapshot snapshot = timer.getSnapshot();
		tmp.put(LogKeys.MEAN, TimeUnit.NANOSECONDS.toMillis((long) snapshot.getMean()));
		tmp.put(LogKeys.MEDIAN, TimeUnit.NANOSECONDS.toMillis((long) snapshot.getMedian()));
		tmp.put(LogKeys.MAX, TimeUnit.NANOSECONDS.toMillis(snapshot.getMax()));
		tmp.put(LogKeys.MIN, TimeUnit.NANOSECONDS.toMillis(snapshot.getMin()));
		tmp.put(LogKeys.STD, TimeUnit.NANOSECONDS.toMillis((long) snapshot.getStdDev()));
		tmp.put(LogKeys._75THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get75thPercentile()));
		tmp.put(LogKeys._95THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get95thPercentile()));
		tmp.put(LogKeys._99THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get99thPercentile()));
		tmp.put(LogKeys._999THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get999thPercentile()));
		// Runtime
		tmp.put("memfree", Runtime.getRuntime().freeMemory());
	}

	private int toInt(String intStr) {
		try {
			return Integer.parseInt(intStr);
		} catch (Exception e) {
			return 0;
		}
	}

}
