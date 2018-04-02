package net.morimekta.providence.storage.jdbi;

import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;
import net.morimekta.test.providence.storage.jdbc.OptionalFields;
import org.jdbi.v3.core.Handle;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Types;
import java.time.Clock;

import static net.morimekta.providence.storage.jdbi.ProvidenceJdbi.columnsFromAllFields;
import static net.morimekta.providence.storage.jdbi.ProvidenceJdbi.forMessage;
import static net.morimekta.providence.storage.jdbi.ProvidenceJdbi.toField;
import static net.morimekta.providence.storage.jdbi.ProvidenceJdbi.toMessage;
import static net.morimekta.providence.storage.jdbi.ProvidenceJdbi.withColumn;
import static net.morimekta.providence.storage.jdbi.ProvidenceJdbi.withType;
import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BASE64_DATA;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BINARY_MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BLOB_DATA;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BLOB_MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.CLOB_MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.ID;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.TIMESTAMP_MS;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.TIMESTAMP_S;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageRowMapperTest {
    @Rule
    public TestDatabase db = new TestDatabase("/mappings.sql")
            .dumpOnFailure("mappings.default_mappings");

    @Rule
    public SimpleGeneratorWatcher generator = SimpleGeneratorWatcher.create();

    private Clock clock = Clock.systemUTC();

    @Test
    public void testDefaultMapping() {
        generator.setFillRate(1.0)
                 .setMaxCollectionItems(16);
        OptionalFields expected = generator.generate(OptionalFields.kDescriptor)
                                           .mutate()
                                           .setId(1234)
                                           .setTimestampS((int) clock.instant().getEpochSecond())
                                           // Since a number of DBs (MySQL <= 5.5) stores timestamp as second, not MS.
                                           .setTimestampMs(clock.instant().getEpochSecond() * 1000)
                                           .build();
        OptionalFields empty = OptionalFields.builder()
                                             .setId(2345)
                                             .build();

        try (Handle handle = db.getDBI().open()) {
            handle.createUpdate("INSERT INTO mappings.default_mappings (" +
                                "  id, present, tiny, small, medium, large, real," +
                                "  fib, name, data, compact," +
                                "  timestamp_s, timestamp_ms," +
                                "  binary_message, blob_message, other_message," +
                                "  blob_data, base64_data" +
                                ") VALUES (" +
                                "  :e.id," +
                                "  :e.present," +
                                "  :e.tiny," +
                                "  :e.small," +
                                "  :e.medium," +
                                "  :e.large," +
                                "  :e.real," +
                                "  :e.fib," +
                                "  :e.name," +
                                "  :e.data," +
                                "  :e.message," +

                                "  :timestamp_s," +
                                "  :e.timestamp_ms," +
                                "  :e.binary_message," +
                                "  :e.blob_message," +
                                "  :e.clob_message," +
                                "  :e.blob_data," +
                                "  :e.base64_data" +
                                ")")
                  .bind("timestamp_s", toField(expected, TIMESTAMP_S, Types.TIMESTAMP))
                  .bindNamedArgumentFinder(forMessage("e", expected,
                                                      withType(TIMESTAMP_MS, Types.TIMESTAMP),
                                                      withType(BINARY_MESSAGE, Types.BINARY),
                                                      withType(BLOB_MESSAGE, Types.BLOB),
                                                      withType(CLOB_MESSAGE, Types.CLOB),
                                                      withType(BLOB_DATA, Types.BLOB),
                                                      withType(BASE64_DATA, Types.VARCHAR)))
                  .execute();
            handle.createUpdate("INSERT INTO mappings.default_mappings (" +
                                "  id, present, tiny, small, medium, large, real," +
                                "  fib, name, data, compact," +
                                "  timestamp_s, timestamp_ms," +
                                "  binary_message, blob_message, other_message," +
                                "  blob_data, base64_data" +
                                ") VALUES (" +
                                "  :e.id," +
                                "  :e.present," +
                                "  :e.tiny," +
                                "  :e.small," +
                                "  :e.medium," +
                                "  :e.large," +
                                "  :e.real," +
                                "  :e.fib," +
                                "  :e.name," +
                                "  :e.data," +
                                "  :e.message," +

                                "  :timestamp_s," +
                                "  :e.timestamp_ms," +
                                "  :e.binary_message," +
                                "  :e.blob_message," +
                                "  :e.clob_message," +
                                "  :e.blob_data," +
                                "  :e.base64_data" +
                                ")")
                  .bind("timestamp_s", toField(empty, TIMESTAMP_S, Types.TIMESTAMP))
                  .bindNamedArgumentFinder(forMessage("e", empty,
                                                      withType(TIMESTAMP_MS, Types.TIMESTAMP),
                                                      withType(BINARY_MESSAGE, Types.BINARY),
                                                      withType(BLOB_MESSAGE, Types.BLOB),
                                                      withType(CLOB_MESSAGE, Types.CLOB),
                                                      withType(BLOB_DATA, Types.BLOB),
                                                      withType(BASE64_DATA, Types.VARCHAR)))
                  .execute();

            OptionalFields val = handle.createQuery("SELECT * FROM mappings.default_mappings WHERE id = :id")
                                       .bind("id", toField(expected, ID))
                                       .map(toMessage(OptionalFields.kDescriptor,
                                                      columnsFromAllFields(),
                                                      withColumn("compact", MESSAGE),
                                                      withColumn("other_message", CLOB_MESSAGE)))
                                       .findFirst()
                                       .orElseThrow(() -> new AssertionError("No content in default_mappings"));
            OptionalFields val2 = handle.createQuery("SELECT * FROM mappings.default_mappings WHERE id = :id")
                                        .bind("id", toField(empty, ID))
                                        .map(toMessage(OptionalFields.kDescriptor,
                                                       columnsFromAllFields(),
                                                       withColumn("compact", MESSAGE),
                                                       withColumn("other_message", CLOB_MESSAGE)))
                                        .findFirst()
                                        .orElseThrow(() -> new AssertionError("No content in default_mappings"));

            assertThat(val, is(equalToMessage(expected)));
            assertThat(val2, is(equalToMessage(empty)));
        }
    }
}
