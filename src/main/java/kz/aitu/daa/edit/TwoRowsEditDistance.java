package kz.aitu.daa.edit;

import kz.aitu.daa.metrics.Counter;

/**
 * Version C - the same recurrence kept in two rows only.
 *
 * <p>DP design:
 * <ul>
 *   <li>State:      two arrays, {@code prev} (row i-1) and {@code curr} (row i).</li>
 *   <li>Transition: same as the table version, reading the two rows.</li>
 *   <li>Base case:  prev[j] = j (row 0) and curr[0] = i (column 0).</li>
 * </ul>
 *
 * <p>Time O(n*m) and the same answers as the table version, but extra memory is
 * O(m) instead of O(n*m). It is the memory that is saved, not the time.
 */
public final class TwoRowsEditDistance implements EditDistance {

    @Override
    public int distance(String a, String b, Counter counter) {
        int n = a.length();
        int m = b.length();
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];
        for (int j = 0; j <= m; j++) {
            prev[j] = j;
            counter.increment();
        }
        for (int i = 1; i <= n; i++) {
            curr[0] = i;
            counter.increment();
            for (int j = 1; j <= m; j++) {
                counter.increment();
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(prev[j - 1],
                            Math.min(prev[j], curr[j - 1]));
                }
            }
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }
        return prev[m];
    }
}
