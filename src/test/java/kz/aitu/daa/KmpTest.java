package kz.aitu.daa;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import kz.aitu.daa.search.KmpSearch;
import kz.aitu.daa.search.NaiveSearch;

class KmpTest {

    @Test
    void lpsReferenceArrays() {
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, KmpSearch.lps("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, KmpSearch.lps("ABABCABAB"));
    }

    @Test
    void lpsEdgeCases() {
        assertArrayEquals(new int[]{0}, KmpSearch.lps("A"));
        assertArrayEquals(new int[]{0, 0, 0}, KmpSearch.lps("ABC"));
        assertArrayEquals(new int[]{0, 1, 2, 3}, KmpSearch.lps("AAAA"));
    }

    @Test
    void kmpMatchesNaiveOn100RandomTexts() {
        Random random = new Random(13);
        NaiveSearch naive = new NaiveSearch();
        KmpSearch kmp = new KmpSearch();
        for (int i = 0; i < 100; i++) {
            String text = randomString(random, 1 + random.nextInt(60), 3);
            String pattern = randomString(random, 1 + random.nextInt(6), 2);
            assertEquals(naive.search(text, pattern), kmp.search(text, pattern),
                    () -> "text=\"" + text + "\" pattern=\"" + pattern + "\"");
        }
    }

    private static String randomString(Random random, int length, int alphabet) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(alphabet)));
        }
        return sb.toString();
    }
}
