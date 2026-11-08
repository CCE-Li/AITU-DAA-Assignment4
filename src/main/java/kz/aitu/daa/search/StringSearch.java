package kz.aitu.daa.search;

import java.util.List;

import kz.aitu.daa.metrics.Counter;

/** One interface shared by the two string-search versions. */
public interface StringSearch {

    /**
     * @param text    the text to search in
     * @param pattern the pattern to find (must not be empty)
     * @param counter counted work: the number of character comparisons
     * @return start indices of all occurrences, increasing, including overlapping
     *         ones; an empty list when there is no match or when the pattern is
     *         longer than the text
     * @throws IllegalArgumentException if {@code pattern} is empty
     */
    List<Integer> search(String text, String pattern, Counter counter);

    /** Convenience overload without counting, used by the tests. */
    default List<Integer> search(String text, String pattern) {
        return search(text, pattern, new Counter());
    }
}
