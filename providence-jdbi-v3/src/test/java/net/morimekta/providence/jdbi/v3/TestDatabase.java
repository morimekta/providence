package net.morimekta.providence.jdbi.v3;

import net.morimekta.util.Binary;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import org.h2.tools.Server;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import static net.morimekta.testing.ResourceUtils.getResourceAsString;

/**
 * A H2 database resource rule.
 *
 * This setups a test database server in-memory that you
 * can connect to in tests.
 *
 * To use the test database you only do:
 *
 * <pre><code>
 * public class MyTest {
 *    {@literal @}Rule
 *     public TestDatabase db = new TestDatabase("/schema.sql");
 *
 *    {@literal @}Test
 *     public void testSomething() {
 *         // add data related to test.
 *         sut.useFeatureWithDBI(db.getDBI());
 *     }
 * }
 * </code></pre>
 *
 * To dump tables on failure, you can just add:
 *
 * <pre><code>
 *    {@literal @}Rule
 *     public TestDatabase db = new TestDatabase("/schema.sql")
 *             .dumpOnFailure("schema.table1");
 * </code></pre>
 *
 * And the content of the table[s] specified to dump will be printed to STD out
 * in a readable table format like:
 *
 * <pre>
 * key1           | key2
 * ---------------------
 * the value in 1 |   55
 * </pre>
 *
 */
public class TestDatabase extends TestWatcher implements AutoCloseable {
    // for printing
    private static final String COL_SEP        = " | ";
    private static final String HEADER_ROW_SEP = "-";

    private String[] testLoadSchemas;
    private String[] dumpTablesOnFailure;
    private String[] originalDumpTablesOnFailure;
    private Server   server;
    private Jdbi     dbi;
    private Handle   keepSchemaHandle;
    private String   name;

    /**
     * Create a H2 database resource, which will clear the DB and load the
     * given schemas for each test.
     *
     * @param schemas The database schema files to load on start.
     */
    TestDatabase(String... schemas) {
        testLoadSchemas = schemas;
        dumpTablesOnFailure = new String[]{};
        originalDumpTablesOnFailure = dumpTablesOnFailure;
        byte[] tmp = new byte[8];
        new Random().nextBytes(tmp);
        name = Binary.wrap(tmp).toBase64();

        try {
            DriverManager.registerDriver(new org.h2.Driver());


            // We be like, what is this craziness:
            //http://www.h2database.com/html/advanced.html#java_objects_serialization
            System.setProperty("h2.serializeJavaObject", "false");

            this.server = null;
            this.dbi = null;
            this.keepSchemaHandle = null;
        } catch (SQLException e) {
            throw new AssertionError("Failed to set up testing H2 database", e);
        }
    }

    /**
     * Start the H2 server.
     *
     * @throws SQLException On SQL exceptions.
     */
    private void start() throws SQLException {
        this.server = Server.createTcpServer("-tcp", "-tcpAllowOthers").start();
        this.dbi = Jdbi.create(getJdbcUri());
    }

    /**
     * Stop the H2 server.
     */
    private void stop() {
        if (keepSchemaHandle != null) {
            keepSchemaHandle.close();
            keepSchemaHandle = null;
        }
        if (server != null) {
            server.stop();
            server = null;
        }
        dbi = null;
        dumpTablesOnFailure = originalDumpTablesOnFailure;
    }

    /**
     * Dump tables on failure. This only works if the test db is set as a test
     * rule.
     *
     * @param tables The tables to dump to std err.
     * @return The test db instance.
     */
    TestDatabase dumpOnFailure(String... tables) {
        TreeSet<String> tablesToDump = new TreeSet<>();
        Collections.addAll(tablesToDump, dumpTablesOnFailure);
        Collections.addAll(tablesToDump, tables);
        this.dumpTablesOnFailure = tablesToDump.toArray(new String[0]);

        // If the server is NOT started, keep this as the original,
        // so we can go back to this start after a test.
        if (server == null) {
            this.originalDumpTablesOnFailure = dumpTablesOnFailure;
        }
        return this;
    }

    /**
     * Get a DBI instance connected to the H2 server.
     *
     * @return The DBI instance.
     */
    Jdbi getDBI() {
        return this.dbi;
    }

    /**
     * Get the DBI URI to connect to the db server.
     *
     * @return The JDBC URI.
     */
    private String getJdbcUri() {
        return String.format("jdbc:h2:tcp://localhost:%d/mem:%s;MODE=MYSQL", getServerPort(), name);
    }

    /**
     * Method to load schemas into the database.
     *
     * @param schemas String array with the schemas to load.
     */
    private void loadSchema(String... schemas) {
        // Keep at least one handle around until closed, as H2 removes all
        // content when the last connection closes.
        if (keepSchemaHandle == null) {
            keepSchemaHandle = this.dbi.open();
        }

        for (String schema : schemas) {
            String script = getResourceAsString(schema);
            Path pathToSchema = FileSystems.getDefault().getPath(schema);
            String schemaName = pathToSchema.getFileName().toString();
            schemaName = schemaName.substring(0, schemaName.indexOf("."));
            keepSchemaHandle.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName.toUpperCase());
            script = "USE " + schemaName.toUpperCase() + "; " + script;
            keepSchemaHandle.createScript(script).execute();
        }
    }

    /**
     * Method to dump current database tables to temporary files.
     *
     * <code>
     *     TestDatabase db = new TestDatabase("/schema.sql").start();
     *     ... // do your code
     *     db.dump("schema.content", "schema.other"); // dumps each table to std err.
     * </code>
     * @param tables Tables to dump to STD err.
     * @throws IOException If unable to read table or print content.
     */
    private void dump(String... tables) throws IOException {
        for (String table : tables) {
            dumpInternal(table);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("server", this.server)
                .add("dbi", this.dbi)
                .toString();
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    protected void starting(Description description) {
        try {
            start();
            loadSchema(testLoadSchemas);
        } catch (SQLException e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    @Override
    protected void failed(Throwable e, Description description) {
        try {
            this.dump(dumpTablesOnFailure);
        } catch (IOException ie) {
            e.addSuppressed(ie);
        }
    }

    @Override
    protected void finished(Description description) {
        stop();
    }

    @VisibleForTesting
    private void dumpInternal(String table) throws IOException {
        List<List<String>> tableValues = new ArrayList<>();

        if (dbi == null) {
            System.err.println("Trying to dump '" + table + "' of not started database");
            return;
        }

        try (Handle h = this.dbi.open()) {
            List<Map<String, Object>> rs = h.createQuery("SELECT * FROM " + table.toUpperCase())
                                            .mapToMap()
                                            .list();

            List<String> cols = new ArrayList<>();
            for (Map<String, Object> rowResult : rs) {
                if (cols.isEmpty()) {
                    List<String> header = new ArrayList<>();
                    for (String key : new TreeSet<>(rowResult.keySet())) {
                        cols.add(key);
                        header.add(key);
                    }
                    tableValues.add(header);
                }
                List<String> row = new ArrayList<>();
                for (String key : cols) {
                    row.add(String.valueOf(rowResult.get(key)));
                }
                tableValues.add(row);
            }
        }

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.err, "UTF-8"));
        writer.println("Table Dump: " + table);
        prettyPrintTable(writer, tableValues);
        writer.flush();
    }

    private void prettyPrintTable(Writer out, List<List<String>> table) throws IOException {
        ArrayList<Integer> columnWidths = new ArrayList<>();
        for (List<String> row : table) {
            while (row.size() > columnWidths.size()) {
                columnWidths.add(0);
            }
            for (int col = 0; col < row.size(); ++col) {
                columnWidths.set(col, Math.max(row.get(col).length(), columnWidths.get(col)));
            }
        }

        boolean header = true;
        for (List<String> row : table) {
            int col = 0;
            for (; col < row.size(); ++col) {
                if (col != 0) {
                    out.write(COL_SEP);
                }
                String value = row.get(col);
                out.write(Strings.padStart(value, columnWidths.get(col), ' '));
            }
            for (; col < columnWidths.size(); ++col) {
                out.write(COL_SEP);
                out.write(Strings.repeat(" ", columnWidths.get(col)));
            }

            if (header) {
                out.write(System.lineSeparator());

                for (col = 0; col < columnWidths.size(); ++col) {
                    if (col > 0) {
                        out.write(Strings.repeat(HEADER_ROW_SEP, COL_SEP.length()));
                    }
                    out.write(Strings.repeat(HEADER_ROW_SEP, columnWidths.get(col)));
                }
                header = false;
            }
            out.write(System.lineSeparator());
        }
    }

    private int getServerPort() {
        return server.getPort();
    }
}
