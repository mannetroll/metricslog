/*
 #
 # Copyright (C) 2010-2014 Anders Håål, Ingenjorsbyn AB
 #
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 2 of the License, or
 # (at your option) any later version.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <http://www.gnu.org/licenses/>.
 #
 */

package com.mannetroll.metrics.codahale;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * @author mannetroll
 */
public class AppenderMetricsManager {
    private static MetricRegistry metricRegistry;
    private static MetricRegistryWrapper wrapper;

    public static MetricRegistry getRegistry() {
        if (metricRegistry == null) {
            AppenderMetricsManager.metricRegistry = new MetricRegistry();
            AppenderMetricsManager.wrapper = new MetricRegistryWrapper(metricRegistry);
        }
        return metricRegistry;
    }

    public static void setMetricRegistry(MetricRegistry metricRegistry) {
        AppenderMetricsManager.metricRegistry = metricRegistry;
        AppenderMetricsManager.wrapper = new MetricRegistryWrapper(metricRegistry);
    }

    public static Timer getAppenderTimer(String timerName) {
        getRegistry();
        return wrapper.appendertimer("timer" + timerName.replace('/', '.').toLowerCase());
    }

    public static Timer getTimer(Class<?> clazz, String timerName) {
        return getRegistry().timer(MetricRegistry.name(clazz, timerName));
    }

    public static Counter getCounter(Class<?> clazz, String counterName) {
        return getRegistry().counter(MetricRegistry.name(clazz, counterName));
    }

    public static Meter getMeter(Class<?> clazz, String meterName) {
        return getRegistry().meter(MetricRegistry.name(clazz, meterName));
    }

}
