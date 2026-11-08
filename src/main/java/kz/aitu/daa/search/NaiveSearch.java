package kz.aitu.daa.search;

import java.util.ArrayList;
import java.util.List;

import kz.aitu.daa.metrics.Counter;

/**
 * Naive search: try every start position and compare character by character.
 *
 * <p>The next window always starts one position after the previous one, so
 * overlapping matches are reported too. An empty pattern is rejected; a pattern
 * longer than the text has no occurrence and returns an empty list.
 */
public final class NaiveSearch implements StringSearch {

    @Override
    public List<Integer> search(String text, String pattern, Counter counter) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        for (int start = 0; start + m <= n; start++) {
            int k = 0;
            while (k < m) {
                counter.increment();
                if (text.charAt(start + k) != pattern.charAt(k)) {
                    break;
                }
                k++;
            }
            if (k == m) {
                matches.add(start);
            }
        }
        return matches;
    }
}
