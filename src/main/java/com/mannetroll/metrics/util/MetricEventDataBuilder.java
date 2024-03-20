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
		tmp.put(LogKeys.HTTP_RESPONSE_STATUS_CODE, toInt(getMDCString(LogKeys.HTTP_RESPONSE_STATUS_CODE, event)));
		safePutValue(tmp, LogKeys.HTTP_REQUEST_METHOD, getMDCString(LogKeys.HTTP_REQUEST_METHOD, event));
		safePutValue(tmp, LogKeys.DOMAIN, getMDCString(LogKeys.DOMAIN, event));
		safePutValue(tmp, LogKeys.URL_PATH, getMDCString(LogKeys.URL_PATH, event));
		safePutValue(tmp, LogKeys.URL_FULL, getMDCString(LogKeys.URL_FULL, event));
		safePutValue(tmp, LogKeys.URL_QUERY, getMDCString(LogKeys.URL_QUERY, event));
		safePutValue(tmp, LogKeys.USER_AGENT_NAME, getMDCString(LogKeys.USER_AGENT_NAME, event));
		tmp.put(LogKeys.RESPONSETIME_MS, TimeUnit.NANOSECONDS.toMillis(responsetime_ns));
		tmp.put(LogKeys.METRICS_COUNT, timer.getCount());
		tmp.put(LogKeys.METRICS_MEANRATE, (float) timer.getMeanRate());
		tmp.put(LogKeys.METRICS_ONEMINUTERATE, (float) timer.getOneMinuteRate());
		tmp.put(LogKeys.METRICS_FIVEMINUTERATE, (float) timer.getFiveMinuteRate());
		tmp.put(LogKeys.METRICS_FIFTEENMINUTERATE, (float) timer.getFifteenMinuteRate());
		Snapshot snapshot = timer.getSnapshot();
		tmp.put(LogKeys.METRICS_MEAN, TimeUnit.NANOSECONDS.toMillis((long) snapshot.getMean()));
		tmp.put(LogKeys.METRICS_MEDIAN, TimeUnit.NANOSECONDS.toMillis((long) snapshot.getMedian()));
		tmp.put(LogKeys.METRICS_MAX, TimeUnit.NANOSECONDS.toMillis(snapshot.getMax()));
		tmp.put(LogKeys.METRICS_MIN, TimeUnit.NANOSECONDS.toMillis(snapshot.getMin()));
		tmp.put(LogKeys.METRICS_STD, TimeUnit.NANOSECONDS.toMillis((long) snapshot.getStdDev()));
		tmp.put(LogKeys.METRICS_75THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get75thPercentile()));
		tmp.put(LogKeys.METRICS_95THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get95thPercentile()));
		tmp.put(LogKeys.METRICS_99THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get99thPercentile()));
		tmp.put(LogKeys.METRICS_999THPERCENTILE, TimeUnit.NANOSECONDS.toMillis((long) snapshot.get999thPercentile()));
	}

	private int toInt(String intStr) {
		try {
			return Integer.parseInt(intStr);
		} catch (Exception e) {
			return 0;
		}
	}

}
