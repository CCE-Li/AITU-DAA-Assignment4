package kz.aitu.daa.search;

import java.util.ArrayList;
import java.util.List;

import kz.aitu.daa.metrics.Counter;

/**
 * Knuth-Morris-Pratt search. The text index never moves backwards: when a
 * mismatch happens, the LPS array says how much of the pattern still matches.
 */
public final class KmpSearch implements StringSearch {

    /**
     * Prefix function (LPS = longest proper prefix that is also a suffix) for
     * every prefix of {@code p}. Public so that tests can call it directly.
     *
     * @param p the pattern (may be empty; then the result is empty)
     * @return lps[i] = length of the longest proper prefix of p[0..i] that is also
     *         a suffix of p[0..i]
     */
    public static int[] lps(String p) {
        int m = p.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern, Counter counter) {
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m > n) {
            return matches;
        }
        int[] lps = lps(pattern);
        int i = 0; // index in text, never decreases
        int j = 0; // index in pattern
        while (i < n) {
            counter.increment();
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    matches.add(i - j);
                    j = lps[j - 1]; // keep overlapping matches
                }
            } else if (j > 0) {
                j = lps[j - 1]; // fall back in the pattern, not in the text
            } else {
                i++;
            }
        }
        return matches;
    }
}
