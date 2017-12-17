package net.morimekta.providence.generator;

import net.morimekta.providence.generator.util.FileManager;

import java.io.PrintStream;
import java.util.Collection;

/**
 * Interface for handling generators in the providence compiler (pvdc).
 *
 * To make a generator module:
 * <ul>
 *     <li>Place following dependencies as <code>provided</code> scope.
 *     <li>Make a jar file bundle with the <code>maven-shade-plugin</code>.
 *     <li>Set the <code>Providence-Generator-Factory</code> property in the
 *         java <code>MANIFEST.MF</code> to point at the implementation of
 *         this interface.
 *     <li>See documentation for <code>pvdc</code> for where the jar file
 *         should be placed to be included in the available generators.
 * </ul>
 *
 * <h3>Dependencies</h3>
 *
 * <pre>{@code
 * <dependencies>
 *     <dependency>
 *         <groupId>net.morimekta.providence</groupId>
 *         <artifactId>providence-core</artifactId>
 *         <scope>provided</scope>
 *     </dependency>
 *     <dependency>
 *         <groupId>net.morimekta.providence</groupId>
 *         <artifactId>providence-reflect</artifactId>
 *         <scope>provided</scope>
 *     </dependency>
 *     <dependency>
 *         <groupId>net.morimekta.providence</groupId>
 *         <artifactId>providence-generator</artifactId>
 *         <scope>provided</scope>
 *     </dependency>
 *     <dependency>
 *         <groupId>com.google.guava</groupId>
 *         <artifactId>guava</artifactId>
 *         <scope>provided</scope>
 *     </dependency>
 *     <dependency>
 *         <groupId>net.morimekta.utils</groupId>
 *         <artifactId>io-util</artifactId>
 *         <scope>provided</scope>
 *     </dependency>
 * </dependencies>
 * }</pre>
 *
 * <h3>Manifest Transformer</h3>
 *
 * <pre>{@code
 * <transformers>
 *     <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
 *         <manifestEntries>
 *             <Providence-Generator-Factory>net.morimekta.providence.generator.format.js.JSGenerator</Providence-Generator-Factory>
 *         </manifestEntries>
 *     </transformer>
 * </transformers>
 * }</pre>
 */
public interface GeneratorFactory {
    /**
     * The name of the generator. This is what must be matched in the
     * <code>--gen</code> generator CLI argument.
     *
     * @return The name of the generator.
     */
    String generatorName();

    /**
     * A general description of the generator.
     * Should be in the range of 20-60 characters.
     *
     * @return The generator description.
     */
    String generatorDescription();

    /**
     * Print a listing of the generator options in a CLI like
     * point list. E.g.:
     * <pre>
     * - first          : The first option, does this.
     * - second         : The second option, does that.
     * </pre>
     *
     * @param out The print stream to write to.
     */
    default void printGeneratorOptionsHelp(PrintStream out) {
        out.println("No options available for the " + generatorName() + " generator.");
    }

    /**
     * Create the actual generator. The generator instance is usually the same
     * when generating many thrift programs, but does not need to be synchronized.
     *
     * @param manager The file manager to use for creating files.
     * @param generatorOptions The general generator options.
     * @param options List of string options to create generator-specific options.
     * @return The generator instance.
     */
    Generator createGenerator(FileManager manager,
                              GeneratorOptions generatorOptions,
                              Collection<String> options);
}
