package kz.aitu.daa.edit;

import kz.aitu.daa.metrics.Counter;

/**
 * Version A - plain recursion on the last characters, no memory.
 *
 * <p>DP design:
 * <ul>
 *   <li>State:      D(i, j) = edit distance between prefixes a[0..i) and b[0..j).</li>
 *   <li>Transition: a[i-1] == b[j-1] ? D(i, j) = D(i-1, j-1)
 *                   : D(i, j) = 1 + min(D(i-1, j), D(i, j-1), D(i-1, j-1)).</li>
 *   <li>Base case:  D(i, 0) = i and D(0, j) = j (insert or delete the whole prefix).</li>
 * </ul>
 *
 * <p>Slow on purpose: the same subproblem is solved again and again, so the number
 * of calls grows exponentially on inputs where characters rarely match.
 */
public final class RecursiveEditDistance implements EditDistance {

    @Override
    public int distance(String a, String b, Counter counter) {
        return distance(a, a.length(), b, b.length(), counter);
    }

    private int distance(String a, int i, String b, int j, Counter counter) {
        counter.increment();
        if (i == 0) {
            return j;
        }
        if (j == 0) {
            return i;
        }
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return distance(a, i - 1, b, j - 1, counter);
        }
        int delete = distance(a, i - 1, b, j, counter);
        int insert = distance(a, i, b, j - 1, counter);
        int replace = distance(a, i - 1, b, j - 1, counter);
        return 1 + Math.min(delete, Math.min(insert, replace));
    }
}
