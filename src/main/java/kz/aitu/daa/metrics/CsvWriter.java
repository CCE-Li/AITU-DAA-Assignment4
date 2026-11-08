package kz.aitu.daa.metrics;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Collects rows and writes them as a UTF-8 CSV file. */
public final class CsvWriter {

    private final List<String> lines = new ArrayList<>();

    public CsvWriter(String header) {
        lines.add(header);
    }

    public void row(Object... cells) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(cells[i]);
        }
        lines.add(line.toString());
    }

    public void write(Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
