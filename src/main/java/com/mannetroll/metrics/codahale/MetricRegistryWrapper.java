package com.mannetroll.metrics.codahale;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * @author mannetroll
 */
public class MetricRegistryWrapper {
    private MetricRegistry registry;

    public MetricRegistryWrapper(MetricRegistry registry) {
        this.registry = registry;
    }

    /**
     * Creates a new {@link Timer} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Timer}
     */
    public Timer appendertimer(String name) {
        return getOrAdd(name, AppenderMetricBuilder.APPENDERTIMERS);
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> T getOrAdd(String name, AppenderMetricBuilder<T> builder) {
        final Metric metric = registry.getMetrics().get(name);
        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                //System.out.println("name: " + name);
                return registry.register(name, builder.newMetric());
            } catch (IllegalArgumentException e) {
                final Metric added = registry.getMetrics().get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }
    /**
     * A quick and easy way of capturing the notion of default metrics.
     */
    private interface AppenderMetricBuilder<T extends Metric> {

        AppenderMetricBuilder<Timer> APPENDERTIMERS = new AppenderMetricBuilder<Timer>() {
            @Override
            public Timer newMetric() {
                return new AppenderTimer();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return AppenderTimer.class.isInstance(metric);
            }
        };

        T newMetric();

        boolean isInstance(Metric metric);
    }
}
