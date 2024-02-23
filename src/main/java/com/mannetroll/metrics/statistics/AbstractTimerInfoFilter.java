package com.mannetroll.metrics.statistics;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author mannetroll
 */
public abstract class AbstractTimerInfoFilter implements Filter {
    private Logger logger = Logger.getLogger(AbstractTimerInfoFilter.class.getName());
    private static int calls = 0;
    private static final String NL = " <br> ";
    private final AbstractTimerInfoStats statistics;
    private final String name;
    private static final String ETAGCACHE = "EtagCache";

    /**
     * Default constructor
     * @param statistics
     */
    public AbstractTimerInfoFilter(AbstractTimerInfoStats statistics, String name) {
        this.statistics = statistics;
        this.name = name;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        //
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Level level = Level.FINE;
        if (logger.isLoggable(level)) {
            logger.log(level, "################################################################### " + name);
            logger.log(level, (calls++) + ": Request: " + getUrl(request));
        }
        double timestamp = System.currentTimeMillis();
        StatusHttpServletResponseWrapper responseWrapper = new StatusHttpServletResponseWrapper(response);
        try {
            addIpToResponse(responseWrapper);
            // Do Filter Chain
            filterChain.doFilter(servletRequest, responseWrapper);
        } catch (ServletException e) {
            String msg = "X-ServletException: ";
            Throwable rootCause = e.getRootCause();
            if (rootCause != null) {
                msg += rootCause.getClass().getName() + ": " + NL + rootCause.getMessage() + NL;
                logger.log(Level.WARNING, "ServletException: " + rootCause.getMessage(), rootCause);
            } else {
                msg += "RootCause = null";
            }
            msg += getUrlInfo(request);
            statistics.addCall(msg, System.currentTimeMillis() - timestamp, System.currentTimeMillis());
            throw e;
        } catch (IOException e) {
            String msg = "X-IOException: " + NL + e.getMessage() + NL + getUrlInfo(request);
            statistics.addCall(msg, System.currentTimeMillis() - timestamp, System.currentTimeMillis());
            throw e;
        } catch (RuntimeException e) {
            String msg = "X-" + e.getClass().getName() + ": " + NL + e.getMessage() + NL + getUrlInfo(request);
            statistics.addCall(msg, System.currentTimeMillis() - timestamp, System.currentTimeMillis());
            throw e;
        }
        double filterTime = System.currentTimeMillis() - timestamp;
        String key = responseWrapper.getStatus() + responseWrapper.getEtagcache() + request.getRequestURI();
        if (statistics.isAddQueryToKey()) { //let thin urls be collected //&& !key.contains("thin")) {
            String query = request.getQueryString();
            if (query != null) {
                if (query.contains("ticket") || query.contains("user")) {
                    key += "?*********";
                } else {
                    key += "?" + query;
                }
            }
        }
        long now = System.currentTimeMillis();
        statistics.addCall(key, filterTime, now);
        statistics.addTotalTime(filterTime, 0, now);
        if (logger.isLoggable(level)) {
            logger.log(level, "key: " + key);
            logger.log(level, "elapsed: " + filterTime);
        }
        if (key.startsWith("304")) {
            statistics.addNotModified();
        }
        if (key.startsWith("200C")) {
            statistics.addEtagCached();
        }
        if (key.startsWith("200R")) {
            statistics.addRendered();
        }
    }

    private void addIpToResponse(HttpServletResponse response) {
        response.setHeader("X-SEMC-HOST-NAME", getHostName());
    }

    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Throwable th) {
            // Akamai EdgeJava will throw exception, with IP as message
            return th.getMessage();
        }
    }

    private String getUrl(HttpServletRequest request) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(request.getRequestURI());
        String queryString = request.getQueryString();
        if (queryString != null) {
            stringBuffer.append("?");
            stringBuffer.append(queryString);
        }
        return stringBuffer.toString();
    }

    private String getUrlInfo(HttpServletRequest request) {
        StringBuffer stringBuffer = new StringBuffer(request.getMethod()).append(" ");
        stringBuffer.append(getUrl(request)).append(NL);
        stringBuffer.append("host: ").append(request.getHeader("host")).append(NL);
        stringBuffer.append("cookie: ").append(request.getHeader("cookie")).append(NL);
        stringBuffer.append("referer: ").append(request.getHeader("referer")).append(NL);
        stringBuffer.append("fromes: ").append(request.getHeader("fromes")).append(NL);
        stringBuffer.append("True-Client-IP: ").append(request.getHeader("True-Client-IP")).append(NL);
        return stringBuffer.toString();
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    private static class StatusHttpServletResponseWrapper extends HttpServletResponseWrapper {
        private int status;
        private String etagcache = "";
        public StatusHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        @Override
        public void setHeader(String name, String value) {
            if (ETAGCACHE.equalsIgnoreCase(name)) {
                etagcache = value;
            }
            super.setHeader(name, value);
        }

        public String getEtagcache() {
            return etagcache;
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