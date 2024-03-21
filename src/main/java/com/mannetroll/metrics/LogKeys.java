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
	public static final String HTTP_REQUEST_REMOTE_HOST = "http.request.remote_host";
	public static final String HTTP_REQUEST_X_FORWARDED_FOR = "http.request.x_forwarded_for";
	public static final String HTTP_RESPONSE_STATUS_CODE = "http.response.status_code";
	public static final String HTTP_RESPONSE_TIME_MS = "http.response.time_ms";
	public static final String USER_AGENT_NAME = "user_agent.name";
	public static final String URL_FULL = "url.full";
	public static final String URL_QUERY = "url.query";
	public static final String URL_PATH = "url.path";
	//
	public static final String APPLICATION = "application";
	public static final String SYSTEMNAME = "systemname";
	//
	public static final String METRICS_NAME = "metrics.name";
	public static final String METRICS_999THPERCENTILE = "metrics.999thpercentile";
	public static final String METRICS_99THPERCENTILE = "metrics.99thpercentile";
	public static final String METRICS_95THPERCENTILE = "metrics.95thpercentile";
	public static final String METRICS_75THPERCENTILE = "metrics.75thpercentile";
	public static final String METRICS_STD = "metrics.std";
	public static final String METRICS_MIN = "metrics.min";
	public static final String METRICS_MAX = "metrics.max";
	public static final String METRICS_MEDIAN = "metrics.median";
	public static final String METRICS_MEAN = "metrics.mean";
	public static final String METRICS_FIFTEENMINUTERATE = "metrics.fifteenminuterate";
	public static final String METRICS_FIVEMINUTERATE = "metrics.fiveminuterate";
	public static final String METRICS_ONEMINUTERATE = "metrics.oneminuterate";
	public static final String METRICS_MEANRATE = "metrics.meanrate";
	public static final String METRICS_COUNT = "metrics.count";
	public static final String METRICS_NANOTIME = "metrics.nanotime";
	public static final String METRICS_ELAPSED = "metrics.elapsed";
	//
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
	public static final String METHOD = "method";
	public static final String TYPE = "type";
	public static final String DOMAIN = "domain";
	public static final String JAVA_METHOD = "java_method";
}
