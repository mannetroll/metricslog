package com.mannetroll.metrics.helper;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.mannetroll.metrics.util.LogKeys;

/**
 * A servlet filter that inserts various values retrieved from the incoming http
 * request into the MDC.
 * <p/>
 * The values are removed after the request is processed.
 *
 * @author mannetroll
 */
public class AccessMetricServletFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(AccessMetricServletFilter.class);
	private static final String DOT = ".";

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		StatusHttpServletResponseWrapper responseWrapper = new StatusHttpServletResponseWrapper(response);
		try {
			insertIntoMDC(request);
			chain.doFilter(request, responseWrapper);
			String status = String.valueOf(responseWrapper.getStatus());
			safePutValue(LogKeys.RESPONSE_STATUS, status);
			// append status code to metrics name
			String metrics_name = MDC.get(LogKeys.METRICS_NAME);
			setMetricsName(request, status, metrics_name);
			logger.info(LogKeys.ACCESS_FILTER_OK);
		} catch (IOException e) {
			// append status code to metrics name
			String metrics_name = MDC.get(LogKeys.METRICS_NAME);
			String status = String.valueOf(responseWrapper.getStatus());
			setMetricsName(request, status, metrics_name);
			logger.info(LogKeys.ACCESS_FILTER_IO, e);
			throw e;
		} catch (ServletException e) {
			// append status code to metrics name
			String metrics_name = MDC.get(LogKeys.METRICS_NAME);
			String status = String.valueOf(responseWrapper.getStatus());
			setMetricsName(request, status, metrics_name);
			logger.info(LogKeys.ACCESS_FILTER_SE, e);
			throw e;
		} catch (RuntimeException e) {
			// append status code to metrics name
			String metrics_name = MDC.get(LogKeys.METRICS_NAME);
			String status = String.valueOf(responseWrapper.getStatus());
			setMetricsName(request, status, metrics_name);
			logger.info(LogKeys.ACCESS_FILTER_RE, e);
			throw e;
		} finally {
			clearMDC();
		}

	}

	private void setMetricsName(HttpServletRequest request, String status, String metrics_name) {
		if (metrics_name != null) {
			safePutValue(LogKeys.METRICS_NAME, metrics_name + DOT + status);
		} else {
			safePutValue(LogKeys.METRICS_NAME, request.getRequestURI() + DOT + status);
		}
	}

	void insertIntoMDC(ServletRequest request) {
		MDC.put(LogKeys.NANOTIME, String.valueOf(System.nanoTime()));
		MDC.put(LogKeys.REQUEST_REMOTE_HOST, request.getRemoteHost());
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		safePutValue(LogKeys.REQUEST_URI, requestURI);
		StringBuffer requestURL = httpRequest.getRequestURL();
		if (requestURL != null) {
			safePutValue(LogKeys.REQUEST_URL, requestURL.toString());
		}
		safePutValue(LogKeys.REQUEST_METHOD, httpRequest.getMethod());
		safePutValue(LogKeys.REQUEST_QUERY, httpRequest.getQueryString());
		safePutValue(LogKeys.REQUEST_USER_AGENT, httpRequest.getHeader(LogKeys.HTTP_USER_AGENT));
		safePutValue(LogKeys.REQUEST_X_FORWARDED_FOR, httpRequest.getHeader(LogKeys.HTTP_X_FORWARDED_FOR));
		String xrequestid = httpRequest.getHeader(LogKeys.HTTP_X_REQUEST_ID);
		if (xrequestid == null) {
			xrequestid = UUID.randomUUID().toString().replaceAll("-", "");
		}
		MDC.put(LogKeys.X_REQUEST_ID, xrequestid);
	}

	private void safePutValue(String key, String value) {
		if (value != null) {
			MDC.put(key, value);
		}
	}

	void clearMDC() {
		Map<String, String> tmp = MDC.getCopyOfContextMap();
		if (tmp != null) {
			for (String key : tmp.keySet()) {
				MDC.remove(key);
			}
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	private static class StatusHttpServletResponseWrapper extends HttpServletResponseWrapper {
		private int status = 200; // Sync initial value with: org.apache.coyote.Response(Response.java:69)

		public StatusHttpServletResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void setStatus(int sc) {
			this.status = sc;
			super.setStatus(sc);
		}

		public int getStatus() {
			return status;
		}
	}
}
