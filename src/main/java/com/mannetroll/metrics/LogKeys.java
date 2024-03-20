package com.mannetroll.metrics;

/**
 * @author mannetroll
 */
public class LogKeys {
	//
	// ecs.version: 1.2.0
	//
	public static final String AT_TIMESTAMP = "@timestamp";
	public static final String LOG_LEVEL = "log.level";
	public static final String LOG_LOGGER = "log.logger";
	public static final String LOG_ORIGIN_FILE = "log.origin.file";
	public static final String MESSAGE = "message";
	public static final String PROCESS_THREAD_NAME = "process.thread.name";
	public static final String PROCESS_UPTIME = "process.uptime";
	public static final String ERROR_MESSAGE = "error.message";
	public static final String ERROR_STACK_TRACE = "error.stack_trace";
	public static final String ERROR_TYPE = "error.type";
	//
	public static final String HTTP_REQUEST_METHOD = "http.request.method";
	public static final String HTTP_RESPONSE_STATUS_CODE = "http.response.status_code";
	public static final String USER_AGENT_NAME = "user_agent.name";
	public static final String URL_FULL = "url.full";
	public static final String URL_QUERY = "url.query";
	public static final String URL_PATH = "url.path";
	//
	public static final String APPLICATION = "application";
	public static final String SYSTEMNAME = "systemname";
	public static final String ELAPSED = "elapsed";
	//
	public static final String _999THPERCENTILE = "999thpercentile";
	public static final String _99THPERCENTILE = "99thpercentile";
	public static final String _95THPERCENTILE = "95thpercentile";
	public static final String _75THPERCENTILE = "75thpercentile";
	public static final String STD = "std";
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String MEDIAN = "median";
	public static final String MEAN = "mean";
	public static final String FIFTEENMINUTERATE = "fifteenminuterate";
	public static final String FIVEMINUTERATE = "fiveminuterate";
	public static final String ONEMINUTERATE = "oneminuterate";
	public static final String MEANRATE = "meanrate";
	public static final String COUNT = "count";
	public static final String RESPONSETIME_MS = "responsetime_ms";
	public static final String METHOD = "method";
	public static final String REQUEST_BODY = "request_body";
	public static final String RESPONSE_BODY = "response_body";
	public static final String TYPE = "type";
	public static final String DOMAIN = "domain";
	public static final String DELTAMINUTES = "delta_minutes";
	//
	public static final String NANOTIME = "nanotime";
	public static final String METRICS_NAME = "metrics_name";
	//
	public static final String REQUEST_REMOTE_HOST = "request_remote_host";
	public static final String REQUEST_X_FORWARDED_FOR = "request_x_forwarded_for";
	public static final String ACCESS_FILTER_RE = "AccessFilter.RuntimeException";
	public static final String ACCESS_FILTER_SE = "AccessFilter.ServletException";
	public static final String ACCESS_FILTER_IO = "AccessFilter.IOException";
	public static final String ACCESS_FILTER_OK = "AccessFilter.OK";
	public static final String X_REQUEST_ID = "xrequestid";
	//
	public static final String HTTP_USER_AGENT = "User-Agent";
	public static final String HTTP_X_REQUEST_ID = "X-Request-ID";
	public static final String HTTP_X_FORWARDED_FOR = "X-Forwarded-For";
	//
	public static final String B3_X_TRACEID = "X-B3-TraceId";
	public static final String B3_X_SPANID = "X-B3-SpanId";
	public static final String B3_X_PARENTSPANID = "X-B3-ParentSpanId";
	//
	public static final String JAVA_METHOD = "java_method";
	public static final String JAVA_CEMEVENT = "java_cemevent";
	public static final String JAVA_SHIPMENTID = "java_shipmentid";
	public static final String JAVA_ITEMID = "java_itemid";
}
