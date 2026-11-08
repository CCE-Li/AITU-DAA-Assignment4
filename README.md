# DAA Assignment 4 - Dynamic Programming and String Algorithms

Java implementation of the City Search & Spell-Check scenario: three versions of
the edit distance (dynamic programming), Naive and KMP string search, operation
counters that are reproducible on any machine, a self-checking experiment that
reproduces the assignment's reference values exactly, and JUnit 5 tests that
cross-check every algorithm against an independent implementation.

| Algorithm | Class |
|---|---|
| Edit distance A - plain recursion (slow on purpose) | `edit/RecursiveEditDistance` |
| Edit distance B - bottom-up `(n+1)x(m+1)` table, `O(n*m)` time and memory | `edit/TableEditDistance` |
| Edit distance C - two rows only, `O(n*m)` time, `O(m)` memory | `edit/TwoRowsEditDistance` |
| Naive string search (all matches, overlapping included) | `search/NaiveSearch` |
| KMP string search (LPS / prefix function, no backward moves) | `search/KmpSearch` |

All three edit-distance versions share one interface (`EditDistance`), and both
search versions share one interface (`StringSearch`), so the experiment and the
tests run each family in a single loop. Each version carries its **state,
transition and base case** written in the class comment.

## Requirements

* JDK 21 or newer
* Maven 3.9 or newer
* Python 3 with `matplotlib` (only for `scripts/plot.py`)

## Build, test, experiment

```bash
mvn test                 # 10 JUnit 5 tests
mvn -q compile exec:java # self-check + experiment -> results/results.csv
py -3.13 scripts/plot.py # results/plots/*.png (log axes + one ratio plot)
```

The experiment is seeded (`Benchmark.SEED = 42`): the same command always
produces the same `results.csv`. Before any measurement it **self-checks every
reference value from section 7 of the assignment** and stops with an error if one
of them is wrong:

| Input | Expected |
|---|---|
| `distance("kitten", "sitting")` | 3 |
| `distance("flaw", "lawn")` | 2 |
| `distance("intention", "execution")` | 5 |
| `distance("", "abc")` / `distance("abc", "abc")` | 3 / 0 |
| `lps("AAACAAAA")` | `[0,1,2,0,1,2,3,3]` |
| `lps("ABABCABAB")` | `[0,0,1,2,0,1,2,3,4]` |
| `search("ABABDABACDABABCABAB", "ABABCABAB")` | `[10]` |
| `search("AAAAA", "AA")` | `[0,1,2,3]` |
| `search("ABABABA", "ABA")` | `[0,2,4]` |

## Counters

Wall-clock time depends on the laptop, so every run also records a counter that
the algorithm increments itself:

* **`states`** (edit distance) - one increment per subproblem/cell evaluated. It
  is what separates the exponential recursion from the `O(n*m)` table.
* **`char_comparisons`** (string search) - one increment per character
  comparison. It is what separates Naive's `O(n*m)` from KMP's `O(n+m)`.

`results/results.csv` has one row per run with the columns
`algorithm,input,n,m,time_ms,states,char_comparisons` (`time_ms` is the median of
5 runs, or of 3 for the exponential recursion). The unused counter is `0`.

## Inputs and sizes

The experiment mixes average, best and worst cases, each with a purpose:

| Algorithm | Inputs | Sizes (n) |
|---|---|---|
| Edit distance (recursion) | `random`, `equal` (best), `disjoint` = `a^n` vs `b^n` (worst) | 3, 6, 9, 12 |
| Edit distance (table, two-rows) | `random`, `equal`, `disjoint` | 1000, 2000, 3000, 4000 |
| String search (`m = 50`) | `random`, `worst` = `a^n` vs `a^(m-1)b`, `all-a` = `a^n` vs `a^m` | 100 000, 200 000, 400 000, 800 000 |
| String search (`m = n/2`) | `worst` with a growing pattern | 2 000, 4 000, 8 000, 16 000 |

## Layout

```
scripts/plot.py              builds the four PNG plots from results/results.csv
src/main/java/kz/aitu/daa/
  edit/                      EditDistance, RecursiveEditDistance, TableEditDistance, TwoRowsEditDistance
  search/                    StringSearch, NaiveSearch, KmpSearch
  metrics/                   Counter (operation counter), CsvWriter
  generator/                 InputGenerator (exact seeded generator)
  benchmark/                 Benchmark (main), ReferenceValues
src/test/java/kz/aitu/daa/    10 JUnit 5 tests
results/results.csv          one row per run (69 rows)
results/plots/               edit_states.png, edit_time.png, search_comparisons.png,
                             search_cost_per_char.png
REPORT.md                    Experiment Design Card, plots + takeaways, prediction vs
                             result, short answers, a paragraph on time
```

## Tests

`mvn test` runs 10 tests: the reference values above; empty strings for all three
edit-distance versions; 100 random pairs on which the three versions must return
the same number; the two reference LPS arrays plus LPS edge cases; the reference
matches including overlapping ones; the two edge cases (empty pattern throws,
pattern longer than the text returns an empty list); and 100 random texts on which
Naive and KMP must return the same list.

## Git workflow

* `main` - only working code, tagged `v1.0`
* small steps with conventional messages: `feat(edit): ...`, `feat(kmp): ...`,
  `test(...)`, `docs(report): ...`

## AI-use acknowledgement

A general-purpose AI coding assistant (CommandCode) was used to draft the
implementation, the tests, the benchmark and this documentation. Every result was
reproduced locally by running `mvn test` and `mvn -q compile exec:java`; the
self-check passes and all reference values match. The analysis in `REPORT.md` is
based on the generated counters, not on assumptions.

## Report

See [REPORT.md](REPORT.md) for the Experiment Design Card, the plots with their
takeaways, the prediction-vs-result comparison, the short answers and the
paragraph on time.
