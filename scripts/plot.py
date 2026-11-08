#!/usr/bin/env python3
"""Builds the four benchmark plots from results/results.csv.

Every plot has a title and labelled axes with units. Three of the four use
log-log axes and one uses a ratio (comparisons per text character), as the
assignment requires.
"""

import csv
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent.parent
RESULTS_CSV = ROOT / "results" / "results.csv"
PLOTS_DIR = ROOT / "results" / "plots"


def read_rows():
    with RESULTS_CSV.open(encoding="utf-8") as handle:
        return list(csv.DictReader(handle))


def series(rows, algorithm, inp):
    points = [r for r in rows if r["algorithm"] == algorithm and r["input"] == inp]
    points.sort(key=lambda r: int(r["n"]))
    n = [int(r["n"]) for r in points]
    states = [int(r["states"]) for r in points]
    comparisons = [int(r["char_comparisons"]) for r in points]
    time_ms = [float(r["time_ms"]) for r in points]
    return n, states, comparisons, time_ms


def save(fig, name):
    PLOTS_DIR.mkdir(parents=True, exist_ok=True)
    path = PLOTS_DIR / name
    fig.tight_layout()
    fig.savefig(path, dpi=200)
    plt.close(fig)
    print("wrote", path.relative_to(ROOT))


def log_log(ax):
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.grid(True, which="both", alpha=0.3)


def plot_edit_states(rows):
    fig, ax = plt.subplots(figsize=(9, 6))
    lines = [
        ("EditTable", "disjoint", "-o", "Table, disjoint (n*m cells)"),
        ("EditTwoRows", "disjoint", "--s", "Two rows, disjoint (n*m cells)"),
        ("EditRecursive", "disjoint", "-^", "Recursive, worst case (no match)"),
        ("EditRecursive", "random", ":d", "Recursive, random"),
        ("EditRecursive", "equal", "-.v", "Recursive, equal strings (best)"),
    ]
    for algorithm, inp, style, label in lines:
        n, states, _, _ = series(rows, algorithm, inp)
        ax.plot(n, states, style, markersize=5, label=label)
    log_log(ax)
    ax.set_title("Edit distance: subproblems evaluated vs n")
    ax.set_xlabel("n (string length, characters)")
    ax.set_ylabel("subproblems evaluated (count)")
    ax.legend(fontsize=8, loc="upper left")
    save(fig, "edit_states.png")


def plot_edit_time(rows):
    fig, ax = plt.subplots(figsize=(9, 6))
    lines = [
        ("EditTable", "disjoint", "-o", "Table, disjoint"),
        ("EditTable", "random", "--o", "Table, random"),
        ("EditTwoRows", "disjoint", "-s", "Two rows, disjoint"),
        ("EditTwoRows", "random", "--s", "Two rows, random"),
    ]
    for algorithm, inp, style, label in lines:
        n, _, _, time_ms = series(rows, algorithm, inp)
        ax.plot(n, time_ms, style, markersize=5, label=label)
    log_log(ax)
    ax.set_title("Edit distance: median time vs n (table vs two rows)")
    ax.set_xlabel("n (string length, characters)")
    ax.set_ylabel("time (ms, median of 5 runs)")
    ax.legend(fontsize=8, loc="upper left")
    save(fig, "edit_time.png")


def plot_search_comparisons(rows):
    fig, ax = plt.subplots(figsize=(9, 6))
    lines = [
        ("NaiveSearch", "worst", "-o", "Naive, worst (m = 50)"),
        ("NaiveSearch", "overlap", "--o", "Naive, all-a (m = 50)"),
        ("NaiveSearch", "random", ":o", "Naive, random (m = 50)"),
        ("KmpSearch", "worst", "-^", "KMP, worst (m = 50)"),
        ("KmpSearch", "overlap", "--^", "KMP, all-a (m = 50)"),
        ("KmpSearch", "random", ":^", "KMP, random (m = 50)"),
        ("NaiveSearch", "worst_scaled", "-D", "Naive, worst (m = n/2, grows with n)"),
        ("KmpSearch", "worst_scaled", "-v", "KMP, worst (m = n/2, grows with n)"),
    ]
    for algorithm, inp, style, label in lines:
        n, _, comparisons, _ = series(rows, algorithm, inp)
        ax.plot(n, comparisons, style, markersize=5, label=label)
    log_log(ax)
    ax.set_title("String search: character comparisons vs n")
    ax.set_xlabel("n (text length, characters)")
    ax.set_ylabel("character comparisons (count)")
    ax.legend(fontsize=8, loc="upper left")
    save(fig, "search_comparisons.png")


def plot_search_cost_per_char(rows):
    fig, ax = plt.subplots(figsize=(9, 6))
    lines = [
        ("NaiveSearch", "worst", "-o", "Naive, worst (m = 50)"),
        ("NaiveSearch", "overlap", "--o", "Naive, all-a (m = 50)"),
        ("NaiveSearch", "random", ":o", "Naive, random (m = 50)"),
        ("KmpSearch", "worst", "-^", "KMP, worst (m = 50)"),
        ("KmpSearch", "overlap", "--^", "KMP, all-a (m = 50)"),
        ("KmpSearch", "random", ":^", "KMP, random (m = 50)"),
        ("NaiveSearch", "worst_scaled", "-D", "Naive, worst (m = n/2)"),
        ("KmpSearch", "worst_scaled", "-v", "KMP, worst (m = n/2)"),
    ]
    for algorithm, inp, style, label in lines:
        n, _, comparisons, _ = series(rows, algorithm, inp)
        ratios = [c / x for c, x in zip(comparisons, n)]
        ax.plot(n, ratios, style, markersize=5, label=label)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.grid(True, which="both", alpha=0.3)
    ax.set_title("String search: cost per text character (comparisons / n)")
    ax.set_xlabel("n (text length, characters)")
    ax.set_ylabel("character comparisons / n (ratio)")
    ax.legend(fontsize=8, loc="upper left")
    save(fig, "search_cost_per_char.png")


def main():
    rows = read_rows()
    plot_edit_states(rows)
    plot_edit_time(rows)
    plot_search_comparisons(rows)
    plot_search_cost_per_char(rows)


if __name__ == "__main__":
    main()
