package net.morimekta.providence.mio.rolling;

import net.morimekta.providence.mio.RollingFileMessageWriter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

/**
 *
 */
public class SizeBasedRollingPolicy implements RollingFileMessageWriter.RollingPolicy {
    public SizeBasedRollingPolicy(File directory,
                                  long rollOnFileSizeInBytes,
                                  String fileNameFormat) {
        this.directory = directory;
        this.rollOnFileSize = rollOnFileSizeInBytes;
        this.fileNameFormat = fileNameFormat;
        this.currentFileId = 1;
        this.currentFile = new File(directory, String.format(fileNameFormat, currentFileId));
        try {
            // Initially skip the files 1..N which has already reached the size trigger.
            while (currentFile.exists() &&
                   Files.size(currentFile.toPath()) >= rollOnFileSizeInBytes) {
                ++currentFileId;
                this.currentFile = new File(directory, String.format(fileNameFormat, currentFileId));
            }
        } catch (IOException e) {
            // Must be file system error, almost impossible to test.
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public void maybeUpdateCurrentFile(@Nonnull RollingFileMessageWriter.CurrentFileUpdater onRollFile,
                                       boolean initialCall) throws IOException {
        if (initialCall) {
            onRollFile.updateCurrentFile(currentFile.getName());
        } else if (Files.size(currentFile.toPath()) >= rollOnFileSize) {
            // rely on the file for the file size. This can lead to
            // slow roll checking if a networking file system is used.
            ++currentFileId;
            currentFile = new File(directory, String.format(fileNameFormat, currentFileId));
            // When rolling enforce continuity, and delete possible existing files.
            Files.deleteIfExists(currentFile.toPath());
            onRollFile.updateCurrentFile(currentFile.getName());
        }
    }

    private final long     rollOnFileSize;
    private final File     directory;
    private final String   fileNameFormat;

    private long currentFileId;
    private File currentFile;
}
