package kz.aitu.daa.generator;

import java.util.Random;

/**
 * Reproducible input generator. Every method takes the caller's {@link Random},
 * so the whole experiment is fixed by a single seed (see {@code Benchmark.SEED}):
 * the same command always produces the same numbers.
 */
public final class InputGenerator {

    private InputGenerator() {
    }

    /** Two strings for the edit-distance experiment. */
    public record Pair(String a, String b) {
    }

    /** A text and a pattern for the string-search experiment. */
    public record SearchInput(String text, String pattern) {
    }

    /** Small alphabet, so that equal characters (and matches) happen often. */
    private static final char[] DNA = {'a', 'b', 'c', 'd'};

    private static String randomString(Random random, int length, char[] alphabet) {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = alphabet[random.nextInt(alphabet.length)];
        }
        return new String(chars);
    }

    // --- Edit distance ---------------------------------------------------

    /** Average case: two independent random strings of length n. */
    public static Pair editRandom(Random random, int n) {
        return new Pair(randomString(random, n, DNA), randomString(random, n, DNA));
    }

    /** Best case for the recursion: the two strings are equal, so it runs in O(n). */
    public static Pair editEqual(Random random, int n) {
        String s = randomString(random, n, DNA);
        return new Pair(s, s);
    }

    /**
     * Worst case: "aaa..a" against "bbb..b". No character ever matches, so the
     * recursion explores all three moves at every step and its call count grows
     * like the Delannoy numbers (exponentially).
     */
    public static Pair editDisjoint(int n) {
        return new Pair("a".repeat(n), "b".repeat(n));
    }

    // --- String search ---------------------------------------------------

    /** Average case: random text and a random pattern over the same alphabet. */
    public static SearchInput searchRandom(Random random, int n, int m) {
        return new SearchInput(randomString(random, n, DNA), randomString(random, m, DNA));
    }

    /**
     * Worst case for Naive: an "aaa..a" text and a pattern of a's ending in one
     * different character. Every window compares up to m characters before it
     * fails, so Naive does about n*m comparisons.
     */
    public static SearchInput searchWorst(int n, int m) {
        return new SearchInput("a".repeat(n), "a".repeat(m - 1) + "b");
    }

    /** Many overlapping matches: an "aaa..a" text and an all-a pattern. */
    public static SearchInput searchOverlap(int n, int m) {
        return new SearchInput("a".repeat(n), "a".repeat(m));
    }

    /**
     * Worst case with a pattern that grows together with the text:
     * text = a^n and pattern = a^(n/2 - 1) + b. Naive now does about n^2/4
     * comparisons while KMP stays linear, so this input exposes the n*m term
     * that a fixed pattern length hides. Requires an even {@code n >= 4}.
     */
    public static SearchInput searchWorstScaled(int n) {
        int m = n / 2;
        return new SearchInput("a".repeat(n), "a".repeat(m - 1) + "b");
    }
}
