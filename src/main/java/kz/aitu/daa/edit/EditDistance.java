package kz.aitu.daa.edit;

import kz.aitu.daa.metrics.Counter;

/**
 * One interface shared by all three edit-distance versions, so the benchmark and
 * the tests can run them in a single loop.
 *
 * <p>The edit distance is the smallest number of insertions, deletions and
 * replacements that turn {@code a} into {@code b}. All versions must agree on
 * every input, including empty strings.
 */
public interface EditDistance {

    /**
     * @param a       first string (may be empty)
     * @param b       second string (may be empty)
     * @param counter counted work: the number of subproblems/cells evaluated
     * @return the edit distance between {@code a} and {@code b}
     */
    int distance(String a, String b, Counter counter);

    /** Convenience overload without counting, used by the tests. */
    default int distance(String a, String b) {
        return distance(a, b, new Counter());
    }
}
