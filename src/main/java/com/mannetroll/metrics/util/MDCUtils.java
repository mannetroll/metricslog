package com.mannetroll.metrics.util;

import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;

/**
 * @author mannetroll
 */
public class MDCUtils {

	public static String getMDCString(String key, LogEvent event) {
		Object object = event.getContextData().getValue(key);
		if (object instanceof String) {
			return (String) object;
		}
		return null;
	}

	public static Long getMDCLong(String key, LogEvent event) {
		Object object = event.getContextData().getValue(key);
		// Stored in ThreadContext as String
		if (object instanceof String) {
			String tmp = (String) object;
			try {
				return Long.parseLong(tmp);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static void safePutValue(Map<String, Object> tmp, String key, Object value) {
		if (value != null) {
			tmp.put(key, value);
		}
	}
}
