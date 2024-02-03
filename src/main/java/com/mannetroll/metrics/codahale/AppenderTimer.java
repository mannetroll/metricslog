package com.mannetroll.metrics.codahale;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Clock;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

/**
 * A timer metric which aggregates timing durations and provides duration statistics, plus
 * throughput statistics via {@link Meter}.
 * 
 * @author mannetroll
 */
public class AppenderTimer extends Timer {
    /**
     * A timing context.
     *
     * @see AppenderTimer#time()
     */
    public static class AppenderContext {
        private final AppenderTimer timer;
        private final Clock clock;
        private final long startTime;

        private AppenderContext(AppenderTimer timer, Clock clock, long startTime) {
            this.timer = timer;
            this.clock = clock;
            this.startTime = startTime;
        }

        /**
         * Updates the timer with the difference between current and start time. Call to this method will
         * not reset the start time. Multiple calls result in multiple updates.
         * @return the elapsed time in nanoseconds
         */
        public long stop() {
            final long elapsed = clock.getTick() - startTime;
            timer.update(elapsed, TimeUnit.NANOSECONDS);
            return elapsed;
        }

        /**
         * Updates the timer with the difference between stop and start time. Call to this method will
         * not reset the start time. Multiple calls result in multiple updates.
         * @return the elapsed time in nanoseconds
         */
        public long stop(long stopTime) {
            final long elapsed = stopTime - startTime;
            timer.update(elapsed, TimeUnit.NANOSECONDS);
            return elapsed;
        }

    }

    private final Meter meter;
    private final Histogram histogram;
    private final Clock clock;

    /**
     * Creates a new {@link AppenderTimer} using an {@link ExponentiallyDecayingReservoir} and the default
     * {@link Clock}.
     */
    public AppenderTimer() {
        this(new ExponentiallyDecayingReservoir());
    }

    /**
     * Creates a new {@link AppenderTimer} that uses the given {@link Reservoir}.
     *
     * @param reservoir the {@link Reservoir} implementation the timer should use
     */
    public AppenderTimer(Reservoir reservoir) {
        this(reservoir, Clock.defaultClock());
    }

    /**
     * Creates a new {@link AppenderTimer} that uses the given {@link Reservoir} and {@link Clock}.
     *
     * @param reservoir the {@link Reservoir} implementation the timer should use
     * @param clock  the {@link Clock} implementation the timer should use
     */
    public AppenderTimer(Reservoir reservoir, Clock clock) {
        this.meter = new Meter(clock);
        this.clock = clock;
        this.histogram = new Histogram(reservoir);
    }

    /**
     * Adds a recorded duration.
     *
     * @param duration the length of the duration
     * @param unit     the scale unit of {@code duration}
     */
    @Override
    public void update(long duration, TimeUnit unit) {
        update(unit.toNanos(duration));
    }

    /**
     * Times and records the duration of event.
     *
     * @param event a {@link Callable} whose {@link Callable#call()} method implements a process
     *              whose duration should be timed
     * @param <T>   the type of the value returned by {@code event}
     * @return the value returned by {@code event}
     * @throws Exception if {@code event} throws an {@link Exception}
     */
    public <T> T time(Callable<T> event, long startTime) throws Exception {
        try {
            return event.call();
        } finally {
            update(clock.getTick() - startTime);
        }
    }

    public long timestop(long startTime) {
        return (new AppenderContext(this, clock, startTime)).stop();
    }

    public long timestartstop(long startTime, long stopTime) {
        final long elapsed = stopTime - startTime;
        this.update(elapsed, TimeUnit.NANOSECONDS);
        return elapsed;
    }

    @Override
    public long getCount() {
        return histogram.getCount();
    }

    @Override
    public double getFifteenMinuteRate() {
        return meter.getFifteenMinuteRate();
    }

    @Override
    public double getFiveMinuteRate() {
        return meter.getFiveMinuteRate();
    }

    @Override
    public double getMeanRate() {
        return meter.getMeanRate();
    }

    @Override
    public double getOneMinuteRate() {
        return meter.getOneMinuteRate();
    }

    @Override
    public Snapshot getSnapshot() {
        return histogram.getSnapshot();
    }

    private void update(long duration) {
        if (duration >= 0) {
            histogram.update(duration);
            meter.mark();
        }
    }
}
