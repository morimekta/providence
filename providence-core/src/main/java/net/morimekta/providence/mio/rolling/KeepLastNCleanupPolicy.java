package net.morimekta.providence.mio.rolling;

import net.morimekta.providence.mio.RollingFileMessageWriter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KeepLastNCleanupPolicy implements RollingFileMessageWriter.CleanupPolicy {
    private final int     keepLastN;
    private final Pattern filePattern;

    public KeepLastNCleanupPolicy(int keepLastN,
                                  String filePattern) {
        this(keepLastN, Pattern.compile(filePattern));
    }

    public KeepLastNCleanupPolicy(int keepLastN,
                                  Pattern filePattern) {
        this.keepLastN = keepLastN;
        this.filePattern = filePattern;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getFilesToDelete(@Nonnull List<String> candidateFiles,
                                         @Nonnull String currentFileName) {
        List<String> out = new ArrayList<>();
        List<String> candidatesBeforeFiles = candidateFiles
                .stream()
                .filter(f -> filePattern.matcher(f).matches())
                .filter(f -> f.compareTo(currentFileName) < 0)
                .sorted()
                .collect(Collectors.toList());
        List<String> candidatesAfterFiles = candidateFiles
                .stream()
                .filter(f -> filePattern.matcher(f).matches())
                .filter(f -> f.compareTo(currentFileName) > 0)
                .sorted()
                .collect(Collectors.toList());

        // including N as this list does not contain the current file.
        if (candidatesAfterFiles.size() > 0 &&
            candidatesAfterFiles.size() + candidatesBeforeFiles.size() >= keepLastN) {
            // First remove files sorted AFTER the current file.
            int remove = (candidatesBeforeFiles.size() + candidatesAfterFiles.size()) - keepLastN + 1;
            out.addAll(candidatesAfterFiles.stream()
                                           .limit(remove)
                                           .collect(Collectors.toList()));
        }

        // including N as this list does not contain the current file.
        if (candidatesBeforeFiles.size() >= keepLastN) {
            int remove = candidatesBeforeFiles.size() - keepLastN + 1;
            out.addAll(candidatesBeforeFiles.subList(0, remove));
        }

        return out;
    }
}
