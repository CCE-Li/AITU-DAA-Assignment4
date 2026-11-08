package kz.aitu.daa.benchmark;

import java.util.Arrays;
import java.util.List;

import kz.aitu.daa.edit.EditDistance;
import kz.aitu.daa.edit.RecursiveEditDistance;
import kz.aitu.daa.edit.TableEditDistance;
import kz.aitu.daa.edit.TwoRowsEditDistance;
import kz.aitu.daa.search.KmpSearch;
import kz.aitu.daa.search.NaiveSearch;

/**
 * The reference values from the assignment. {@link #selfCheck()} throws (and so
 * stops the whole experiment) if any of them is wrong.
 */
public final class ReferenceValues {

    private ReferenceValues() {
    }

    public static void selfCheck() {
        List<EditDistance> versions = List.of(
                new RecursiveEditDistance(),
                new TableEditDistance(),
                new TwoRowsEditDistance());
        for (EditDistance version : versions) {
            String name = version.getClass().getSimpleName();
            check(name + "(\"kitten\",\"sitting\")", 3, version.distance("kitten", "sitting"));
            check(name + "(\"flaw\",\"lawn\")", 2, version.distance("flaw", "lawn"));
            check(name + "(\"intention\",\"execution\")", 5, version.distance("intention", "execution"));
            check(name + "(\"\",\"abc\")", 3, version.distance("", "abc"));
            check(name + "(\"abc\",\"abc\")", 0, version.distance("abc", "abc"));
        }

        checkArray("lps(\"AAACAAAA\")", new int[]{0, 1, 2, 0, 1, 2, 3, 3}, KmpSearch.lps("AAACAAAA"));
        checkArray("lps(\"ABABCABAB\")", new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, KmpSearch.lps("ABABCABAB"));

        checkList("search(\"ABABDABACDABABCABAB\",\"ABABCABAB\")", List.of(10),
                new NaiveSearch().search("ABABDABACDABABCABAB", "ABABCABAB"));
        checkList("search(\"AAAAA\",\"AA\")", List.of(0, 1, 2, 3), new KmpSearch().search("AAAAA", "AA"));
        checkList("search(\"ABABABA\",\"ABA\")", List.of(0, 2, 4), new KmpSearch().search("ABABABA", "ABA"));

        System.out.println("[self-check] all reference values match");
    }

    private static void check(String what, int expected, int actual) {
        if (expected != actual) {
            throw new IllegalStateException("[self-check] " + what + ": expected " + expected + " but got " + actual);
        }
    }

    private static void checkArray(String what, int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new IllegalStateException("[self-check] " + what + ": expected " + Arrays.toString(expected)
                    + " but got " + Arrays.toString(actual));
        }
    }

    private static void checkList(String what, List<Integer> expected, List<Integer> actual) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException("[self-check] " + what + ": expected " + expected + " but got " + actual);
        }
    }
}
