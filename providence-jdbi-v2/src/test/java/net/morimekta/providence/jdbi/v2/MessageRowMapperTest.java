package net.morimekta.providence.jdbi.v2;

import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;
import net.morimekta.test.providence.storage.jdbc.CompactFields;
import net.morimekta.test.providence.storage.jdbc.NormalFields;
import net.morimekta.test.providence.storage.jdbc.OptionalFields;

import org.junit.Rule;
import org.junit.Test;
import org.skife.jdbi.v2.Handle;

import java.sql.Types;
import java.time.Clock;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BASE64_DATA;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BINARY_MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BLOB_DATA;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.BLOB_MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.CLOB_MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.ID;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.INT_BOOL;
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
                 .setMaxCollectionItems(16)
                 .withGenerator(CompactFields.kDescriptor, g -> {
                     g.setValueGenerator(CompactFields._Field.NAME, ctx -> ctx.getFairy().textProducer().latinWord());
                     g.setValueGenerator(CompactFields._Field.LABEL, ctx -> ctx.getFairy().textProducer().word());
                 }).withGenerator(NormalFields.kDescriptor, g -> {
                     g.setValueGenerator(NormalFields._Field.NAME, ctx -> ctx.getFairy().textProducer().latinWord());
                     g.setValueGenerator(NormalFields._Field.LABEL, ctx -> ctx.getFairy().textProducer().word());
                 });
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
            handle.createStatement("INSERT INTO mappings.default_mappings (" +
                                "  id, present, tiny, small, medium, large, real," +
                                "  fib, name, data, compact," +
                                "  timestamp_s, timestamp_ms," +
                                "  binary_message, blob_message, other_message," +
                                "  blob_data, base64_data, int_bool" +
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
                                "  :e.base64_data," +
                                "  :e.int_bool" +
                                ")")
                  .bind("timestamp_s", ProvidenceJdbi.toField(expected, TIMESTAMP_S, Types.TIMESTAMP))
                  .bindNamedArgumentFinder(ProvidenceJdbi.forMessage("e", expected,
                                                                     ProvidenceJdbi.withType(TIMESTAMP_MS, Types.TIMESTAMP),
                                                                     ProvidenceJdbi.withType(BINARY_MESSAGE, Types.BINARY),
                                                                     ProvidenceJdbi.withType(BLOB_MESSAGE, Types.BLOB),
                                                                     ProvidenceJdbi.withType(CLOB_MESSAGE, Types.CLOB),
                                                                     ProvidenceJdbi.withType(BLOB_DATA, Types.BLOB),
                                                                     ProvidenceJdbi.withType(BASE64_DATA, Types.VARCHAR)))
                  .execute();
            handle.createStatement("INSERT INTO mappings.default_mappings (" +
                                "  id, present, tiny, small, medium, large, real," +
                                "  fib, name, data, compact," +
                                "  timestamp_s, timestamp_ms," +
                                "  binary_message, blob_message, other_message," +
                                "  blob_data, base64_data, int_bool" +
                                ") VALUES (" +
                                "  :id," +
                                "  :present," +
                                "  :tiny," +
                                "  :small," +
                                "  :medium," +
                                "  :large," +
                                "  :real," +
                                "  :fib," +
                                "  :name," +
                                "  :data," +
                                "  :message," +

                                "  :timestamp_s," +
                                "  :timestamp_ms," +
                                "  :binary_message," +
                                "  :blob_message," +
                                "  :clob_message," +
                                "  :blob_data," +
                                "  :base64_data," +
                                "  :int_bool" +
                                ")")
                  .bind("timestamp_s", ProvidenceJdbi.toField(empty, TIMESTAMP_S, Types.TIMESTAMP))
                  .bindNamedArgumentFinder(ProvidenceJdbi.forMessage(empty,
                                                                     ProvidenceJdbi.withType(TIMESTAMP_MS, Types.TIMESTAMP),
                                                                     ProvidenceJdbi.withType(BINARY_MESSAGE, Types.BINARY),
                                                                     ProvidenceJdbi.withType(BLOB_MESSAGE, Types.BLOB),
                                                                     ProvidenceJdbi.withType(CLOB_MESSAGE, Types.CLOB),
                                                                     ProvidenceJdbi.withType(BLOB_DATA, Types.BLOB),
                                                                     ProvidenceJdbi.withType(BASE64_DATA, Types.VARCHAR),
                                                                     ProvidenceJdbi.withType(INT_BOOL, Types.INTEGER)))
                  .execute();

            OptionalFields val = handle.createQuery("SELECT m.* FROM mappings.default_mappings m WHERE id = :id")
                                       .bind("id", ProvidenceJdbi.toField(expected, ID))
                                       .map(ProvidenceJdbi.toMessage("default_mappings", OptionalFields.kDescriptor,
                                                                     ProvidenceJdbi.columnsFromAllFields(),
                                                                     ProvidenceJdbi.withColumn("compact", MESSAGE),
                                                                     ProvidenceJdbi.withColumn("other_message", CLOB_MESSAGE)))
                                       .first();
            OptionalFields val2 = handle.createQuery("SELECT * FROM mappings.default_mappings WHERE id = :id")
                                        .bind("id", ProvidenceJdbi.toField(empty, ID))
                                        .map(ProvidenceJdbi.toMessage(OptionalFields.kDescriptor,
                                                                      ProvidenceJdbi.columnsFromAllFields(),
                                                                      ProvidenceJdbi.withColumn("compact", MESSAGE),
                                                                      ProvidenceJdbi.withColumn("other_message", CLOB_MESSAGE)))
                                        .first();

            assertThat(val, is(equalToMessage(expected)));
            assertThat(val2, is(equalToMessage(empty)));
        }
    }
}
