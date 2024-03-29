package com.mannetroll.metrics.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;

import com.mannetroll.metrics.LogKeys;

/**
 * @author mannetroll
 */
public class LoggingEventDataBuilder {
	private static final String COMMON = "common";
	private static final long uptime = System.currentTimeMillis();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getMap(LogEvent event, String application, String namespace, boolean locationInfo,
			boolean b3TracingInfo, boolean sort) {
		final Map<String, Object> map;
		if (sort) {
			map = new TreeMap<>();
		} else {
			map = new LinkedHashMap<>();
		}

		// this GMT timestamp will propagate all the way to elastic
		map.put(LogKeys.AT_TIMESTAMP, ZonedDateTime.now(ZoneOffset.UTC).toString());
		map.put(LogKeys.LOG_LEVEL, event.getLevel().toString());
		map.put(LogKeys.LOG_TYPE, COMMON);
		map.put(LogKeys.PROCESS_UPTIME, (System.currentTimeMillis() - uptime));

		String start = MDCUtils.getMDCString(LogKeys.METRICS_NANOTIME, event);
		if (start != null) {
			long nanotime = Long.parseLong(start);
			long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanotime);
			map.put(LogKeys.METRICS_ELAPSED, elapsed);
		}

		Message message = event.getMessage();
		if (message instanceof MapMessage) {
			map.putAll(((MapMessage) message).getData());
		} else if (message instanceof ObjectMessage) {
			Object parameter = ((ObjectMessage) message).getParameter();
			if (parameter instanceof Map) {
				stringify((Map<String, Object>) parameter, map);
			}
		} else {
			map.put(LogKeys.MESSAGE, message.getFormattedMessage());
		}

		String xrequestid = MDCUtils.getMDCString(LogKeys.X_REQUEST_ID, event);
		if (xrequestid == null) {
			xrequestid = UUID.randomUUID().toString().replaceAll("-", "");
			ThreadContext.put(LogKeys.X_REQUEST_ID, xrequestid);
		}
		map.put(LogKeys.X_REQUEST_ID, xrequestid);

		map.put(LogKeys.PROCESS_THREAD_NAME, event.getThreadName());

		if (event.getLoggerName() != null) {
			map.put(LogKeys.LOG_LOGGER, event.getLoggerName());
		}

		if (application != null) {
			map.put(LogKeys.APPLICATION, application);
			if (namespace != null) {
				Map<String, Object> tmp = new LinkedHashMap<>();
				tmp.put(LogKeys.SYSTEMNAME, application);
				map.put(namespace, tmp);
			}
		}

		Throwable throwable = event.getThrown();
		if (throwable != null) {
			map.put(LogKeys.ERROR_MESSAGE, throwable.getMessage());
			map.put(LogKeys.ERROR_STACK_TRACE, getStackTrace(throwable));
			map.put(LogKeys.ERROR_TYPE, throwable.getClass().getName());
		}

		StackTraceElement stackTraceElement = event.getSource();
		if (locationInfo && stackTraceElement != null) {
			map.put(LogKeys.LOG_ORIGIN_FILE, stackTraceElement.toString());
		}

		// B3 Tracing
		if (b3TracingInfo) {
			MDCUtils.safePutValue(map, LogKeys.B3_X_TRACEID, MDCUtils.getMDCString(LogKeys.B3_X_TRACEID, event));
			MDCUtils.safePutValue(map, LogKeys.B3_X_SPANID, MDCUtils.getMDCString(LogKeys.B3_X_SPANID, event));
			MDCUtils.safePutValue(map, LogKeys.B3_X_PARENTSPANID,
					MDCUtils.getMDCString(LogKeys.B3_X_PARENTSPANID, event));
		}
		return map;
	}

	private void stringify(Map<String, Object> parameter, Map<String, Object> outputMap) {
		for (Map.Entry<String, Object> entry : parameter.entrySet()) {
			if (entry != null && entry.getValue() != null) {
				if (isPrimitiveOrString(entry.getValue())) {
					outputMap.put(entry.getKey(), entry.getValue());
				} else if (entry.getValue() instanceof Collection<?>) {
					Collection<?> col = (Collection<?>) entry.getValue();
					Set<String> objectTypes = new HashSet<>();
					col.forEach(o -> objectTypes.add(o.getClass().getName()));
					if (allPrimitivesOrSimilar(col, objectTypes)) {
						outputMap.put(entry.getKey(), entry.getValue());
					} else {
						outputMap.put(entry.getKey(), entry.getValue().toString());
					}
				} else {
					outputMap.put(entry.getKey(), entry.getValue().toString());
				}
			}
		}
	}

	private boolean allPrimitivesOrSimilar(Collection<?> col, Set<String> objectTypes) {
		return col.stream().allMatch(this::isANumber)
				|| (objectTypes.size() == 1 && col.stream().allMatch(this::isPrimitiveOrString));
	}

	private boolean isANumber(Object obj) {
		return obj instanceof Number;
	}

	private boolean isPrimitiveOrString(Object obj) {
		return ClassUtils.isPrimitiveOrWrapper(obj.getClass()) || obj instanceof String;
	}

	private static String getStackTrace(Throwable t) {
		if (t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			t.printStackTrace(pw);
			pw.flush();
			sw.flush();
			return sw.toString();
		} else {
			return "null";
		}
	}

}
