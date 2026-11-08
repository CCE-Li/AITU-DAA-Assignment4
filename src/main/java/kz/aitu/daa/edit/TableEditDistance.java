package kz.aitu.daa.edit;

import kz.aitu.daa.metrics.Counter;

/**
 * Version B - bottom-up dynamic programming over a full (n+1) x (m+1) table.
 *
 * <p>DP design:
 * <ul>
 *   <li>State:      dp[i][j] = edit distance between prefixes a[0..i) and b[0..j).</li>
 *   <li>Transition: a[i-1] == b[j-1] ? dp[i-1][j-1]
 *                   : 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]).</li>
 *   <li>Base case:  dp[i][0] = i and dp[0][j] = j.</li>
 * </ul>
 *
 * <p>Time O(n*m), extra memory O(n*m).
 */
public final class TableEditDistance implements EditDistance {

    @Override
    public int distance(String a, String b, Counter counter) {
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
            counter.increment();
        }
        for (int j = 1; j <= m; j++) {
            dp[0][j] = j;
            counter.increment();
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                counter.increment();
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[n][m];
    }
}
