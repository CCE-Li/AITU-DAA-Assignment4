package kz.aitu.daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import kz.aitu.daa.search.KmpSearch;
import kz.aitu.daa.search.NaiveSearch;
import kz.aitu.daa.search.StringSearch;

class SearchTest {

    private static final List<StringSearch> VERSIONS = List.of(new NaiveSearch(), new KmpSearch());

    @Test
    void referenceMatches() {
        for (StringSearch version : VERSIONS) {
            assertEquals(List.of(10), version.search("ABABDABACDABABCABAB", "ABABCABAB"));
            assertEquals(List.of(0, 1, 2, 3), version.search("AAAAA", "AA"));
            assertEquals(List.of(0, 2, 4), version.search("ABABABA", "ABA"));
        }
    }

    @Test
    void overlappingMatches() {
        for (StringSearch version : VERSIONS) {
            assertEquals(List.of(0, 1, 2, 3, 4), version.search("AAAAAA", "AA"));
            assertEquals(List.of(0, 1, 2), version.search("AAAAA", "AAA"));
        }
    }

    @Test
    void noMatchReturnsEmptyList() {
        for (StringSearch version : VERSIONS) {
            assertEquals(List.of(), version.search("abcdef", "gh"));
            assertEquals(List.of(), version.search("abc", "abcdef"));
        }
    }

    @Test
    void emptyPatternThrows() {
        for (StringSearch version : VERSIONS) {
            assertThrows(IllegalArgumentException.class, () -> version.search("abc", ""));
        }
    }
}
