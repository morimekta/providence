package net.morimekta.providence.maven.util;

import net.morimekta.util.Strings;
import net.morimekta.util.io.IOUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for making and handling the
 */
public class ProvidenceAssemble {
    public static final String TYPE = "zip";
    public static final String CLASSIFIER = "providence";

    public static void generateProvidencePackageFile(Set<File> inputFiles,
                                                     Artifact artifact,
                                                     File target) throws MojoFailureException {
        String path = Strings.join("/",
                                   artifact.getGroupId().replaceAll("[.]", "/"),
                                   artifact.getArtifactId());


        try (FileOutputStream fos = new FileOutputStream(target, false);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            String tmpPath = "";
            for (String dir : path.split("[/]")) {
                tmpPath = tmpPath + dir + "/";
                zos.putNextEntry(new ZipEntry(path));
                zos.closeEntry();
            }

            for (File thriftFile : inputFiles) {
                Path thriftPath = Paths.get(thriftFile.getAbsolutePath());

                ZipEntry entry = new ZipEntry(path + "/" + thriftPath.getFileName().toString());
                entry.setSize(Files.size(thriftPath));
                entry.setLastModifiedTime(Files.getLastModifiedTime(thriftPath));

                try (FileInputStream fis = new FileInputStream(thriftFile);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    zos.putNextEntry(entry);
                    IOUtils.copy(bis, zos);
                }
            }

            zos.flush();

        } catch (IOException ie) {
            throw new MojoFailureException("Unable to write providence assembly: " + ie.getMessage(), ie);
        }
    }

    public static void addDependencyInclude(File workingDir,
                                            Set<File> includes,
                                            Artifact artifact)
            throws MojoExecutionException {
        // TODO: Figure out if this is the right way to name the output directories.
        File outputDir = new File(workingDir, artifact.getGroupId().replaceAll("[.]", File.separator) + File.separator + artifact.getArtifactId());
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new MojoExecutionException("Unable to create output dir " + outputDir);
            }
        }

        try (FileInputStream fis = new FileInputStream(artifact.getFile());
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zis = new ZipInputStream(bis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    zis.closeEntry();
                    continue;
                }

                File of = new File(outputDir, new File(entry.getName()).getName());

                try (FileOutputStream fos = new FileOutputStream(of, false);
                     BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    IOUtils.copy(zis, bos);
                }

                zis.closeEntry();
            }

            includes.add(outputDir);
        } catch (IOException e) {
            throw new MojoExecutionException("" + e.getMessage(), e);
        }
    }
}
