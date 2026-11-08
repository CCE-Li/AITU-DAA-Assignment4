package kz.aitu.daa.metrics;

/** A single mutable operation counter, incremented from inside the algorithms. */
public final class Counter {

    private long value;

    public void increment() {
        value++;
    }

    public long value() {
        return value;
    }

    public void reset() {
        value = 0L;
    }
}
