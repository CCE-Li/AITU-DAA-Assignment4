package kz.aitu.daa.benchmark;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kz.aitu.daa.edit.EditDistance;
import kz.aitu.daa.edit.RecursiveEditDistance;
import kz.aitu.daa.edit.TableEditDistance;
import kz.aitu.daa.edit.TwoRowsEditDistance;
import kz.aitu.daa.generator.InputGenerator;
import kz.aitu.daa.metrics.Counter;
import kz.aitu.daa.metrics.CsvWriter;
import kz.aitu.daa.search.KmpSearch;
import kz.aitu.daa.search.NaiveSearch;
import kz.aitu.daa.search.StringSearch;

/**
 * The experiment. It first self-checks every reference value, then runs each
 * algorithm on a fixed set of seeds and sizes, takes the median of several runs
 * for the clock, records the counters and writes results/results.csv.
 */
public final class Benchmark {

    /** One seed for the whole experiment: the numbers are reproducible. */
    private static final long SEED = 42L;

    /** Cheap algorithms get 5 runs; the exponential recursion only gets 3. */
    private static final int RUNS = 5;
    private static final int RECURSIVE_RUNS = 3;

    /** Recursion cannot survive large n (exponential), so it gets its own sizes. */
    private static final int[] EDIT_SMALL_N = {3, 6, 9, 12};
    /** Table and two-rows are O(n*m), so they can go further. */
    private static final int[] EDIT_LARGE_N = {1000, 2000, 3000, 4000};

    private static final int[] SEARCH_N = {100_000, 200_000, 400_000, 800_000};
    private static final int SEARCH_M = 50;
    /** Smaller sizes for the input whose pattern grows with the text (n*m work). */
    private static final int[] SEARCH_SCALED_N = {2_000, 4_000, 8_000, 16_000};

    private static final String[] EDIT_INPUTS = {"random", "equal", "disjoint"};
    private static final String[] SEARCH_INPUTS = {"random", "worst", "overlap"};

    public static void main(String[] args) {
        ReferenceValues.selfCheck();
        warmup();

        CsvWriter csv = new CsvWriter("algorithm,input,n,m,time_ms,states,char_comparisons");
        runEditDistance(csv);
        runStringSearch(csv);

        Path out = Path.of("results", "results.csv");
        csv.write(out);
        System.out.println("wrote " + out.toAbsolutePath());
    }

    /** Lets the JIT compile the hot methods before anything is measured. */
    private static void warmup() {
        Counter sink = new Counter();
        Random random = new Random(1);

        // The recursion is exponential, so a few hundred short calls are enough.
        RecursiveEditDistance recursive = new RecursiveEditDistance();
        for (int i = 0; i < 2_000; i++) {
            InputGenerator.Pair pair = InputGenerator.editRandom(random, 8);
            sink.reset();
            recursive.distance(pair.a(), pair.b(), sink);
        }

        // Prime the O(n*m) loops at a size close to the measured range, with
        // random content so both the match and the mismatch branches compile.
        InputGenerator.Pair warmPair = InputGenerator.editRandom(random, 800);
        for (EditDistance edit : new EditDistance[]{new TableEditDistance(), new TwoRowsEditDistance()}) {
            for (int i = 0; i < 40; i++) {
                sink.reset();
                edit.distance(warmPair.a(), warmPair.b(), sink);
            }
        }

        StringSearch[] searches = {new NaiveSearch(), new KmpSearch()};
        String text = "a".repeat(20_000);
        String pattern = "a".repeat(49) + "b";
        for (StringSearch search : searches) {
            for (int i = 0; i < 50; i++) {
                sink.reset();
                search.search(text, pattern, sink);
            }
        }
    }

    private static void runEditDistance(CsvWriter csv) {
        Map<String, EditDistance> algorithms = new LinkedHashMap<>();
        algorithms.put("EditRecursive", new RecursiveEditDistance());
        algorithms.put("EditTable", new TableEditDistance());
        algorithms.put("EditTwoRows", new TwoRowsEditDistance());

        Random random = new Random(SEED);
        for (Map.Entry<String, EditDistance> entry : algorithms.entrySet()) {
            String name = entry.getKey();
            EditDistance algorithm = entry.getValue();
            boolean recursive = algorithm instanceof RecursiveEditDistance;
            int[] sizes = recursive ? EDIT_SMALL_N : EDIT_LARGE_N;
            int runs = recursive ? RECURSIVE_RUNS : RUNS;

            for (int n : sizes) {
                for (String input : EDIT_INPUTS) {
                    InputGenerator.Pair pair = switch (input) {
                        case "random" -> InputGenerator.editRandom(random, n);
                        case "equal" -> InputGenerator.editEqual(random, n);
                        default -> InputGenerator.editDisjoint(n);
                    };
                    Counter counter = new Counter();
                    double timeMs = timeEdit(algorithm, pair, counter, runs);
                    csv.row(name, input, n, n, String.format("%.4f", timeMs), counter.value(), 0);
                }
            }
        }
    }

    private static void runStringSearch(CsvWriter csv) {
        Map<String, StringSearch> algorithms = new LinkedHashMap<>();
        algorithms.put("NaiveSearch", new NaiveSearch());
        algorithms.put("KmpSearch", new KmpSearch());

        Random random = new Random(SEED);
        for (Map.Entry<String, StringSearch> entry : algorithms.entrySet()) {
            String name = entry.getKey();
            StringSearch algorithm = entry.getValue();

            for (int n : SEARCH_N) {
                for (String input : SEARCH_INPUTS) {
                    InputGenerator.SearchInput in = switch (input) {
                        case "random" -> InputGenerator.searchRandom(random, n, SEARCH_M);
                        case "worst" -> InputGenerator.searchWorst(n, SEARCH_M);
                        default -> InputGenerator.searchOverlap(n, SEARCH_M);
                    };
                    measureSearch(csv, algorithm, name, input, in, n, SEARCH_M);
                }
            }

            for (int n : SEARCH_SCALED_N) {
                InputGenerator.SearchInput in = InputGenerator.searchWorstScaled(n);
                measureSearch(csv, algorithm, name, "worst_scaled", in, n, n / 2);
            }
        }
    }

    private static void measureSearch(CsvWriter csv, StringSearch algorithm, String name,
            String input, InputGenerator.SearchInput in, int n, int m) {
        Counter counter = new Counter();
        double timeMs = timeSearch(algorithm, in, counter, RUNS);
        csv.row(name, input, n, m, String.format("%.4f", timeMs), 0, counter.value());
    }

    private static double timeEdit(EditDistance algorithm, InputGenerator.Pair pair, Counter counter, int runs) {
        counter.reset();
        algorithm.distance(pair.a(), pair.b(), counter); // warmup, not measured
        double[] times = new double[runs];
        for (int r = 0; r < runs; r++) {
            counter.reset();
            long start = System.nanoTime();
            algorithm.distance(pair.a(), pair.b(), counter);
            times[r] = (System.nanoTime() - start) / 1e6;
        }
        Arrays.sort(times);
        return times[runs / 2];
    }

    private static double timeSearch(StringSearch algorithm, InputGenerator.SearchInput in, Counter counter, int runs) {
        counter.reset();
        algorithm.search(in.text(), in.pattern(), counter); // warmup, not measured
        double[] times = new double[runs];
        for (int r = 0; r < runs; r++) {
            counter.reset();
            long start = System.nanoTime();
            algorithm.search(in.text(), in.pattern(), counter);
            times[r] = (System.nanoTime() - start) / 1e6;
        }
        Arrays.sort(times);
        return times[runs / 2];
    }
}
