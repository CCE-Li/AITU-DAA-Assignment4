package kz.aitu.daa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import kz.aitu.daa.edit.EditDistance;
import kz.aitu.daa.edit.RecursiveEditDistance;
import kz.aitu.daa.edit.TableEditDistance;
import kz.aitu.daa.edit.TwoRowsEditDistance;

class EditDistanceTest {

    private static final List<EditDistance> VERSIONS = List.of(
            new RecursiveEditDistance(),
            new TableEditDistance(),
            new TwoRowsEditDistance());

    @Test
    void referenceValues() {
        for (EditDistance version : VERSIONS) {
            assertEquals(3, version.distance("kitten", "sitting"));
            assertEquals(2, version.distance("flaw", "lawn"));
            assertEquals(5, version.distance("intention", "execution"));
            assertEquals(3, version.distance("", "abc"));
            assertEquals(0, version.distance("abc", "abc"));
        }
    }

    @Test
    void emptyStrings() {
        for (EditDistance version : VERSIONS) {
            assertEquals(0, version.distance("", ""));
            assertEquals(4, version.distance("", "abcd"));
            assertEquals(4, version.distance("abcd", ""));
        }
    }

    @Test
    void threeVersionsAgreeOn100RandomStrings() {
        Random random = new Random(7);
        for (int i = 0; i < 100; i++) {
            String a = randomString(random);
            String b = randomString(random);
            int expected = VERSIONS.get(0).distance(a, b);
            for (EditDistance version : VERSIONS) {
                assertEquals(expected, version.distance(a, b),
                        () -> "versions disagree on \"" + a + "\" / \"" + b + "\"");
            }
        }
    }

    private static String randomString(Random random) {
        int length = random.nextInt(8);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(3)));
        }
        return sb.toString();
    }
}
