package net.morimekta.providence.jdbi.v2;

import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;
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
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.DATA;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.FIB;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.ID;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.INT_BOOL;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.LARGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.MEDIUM;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.MESSAGE;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.NAME;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.PRESENT;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.REAL;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.SMALL;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.TIMESTAMP_MS;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.TIMESTAMP_S;
import static net.morimekta.test.providence.storage.jdbc.OptionalFields._Field.TINY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageInserterTest {
    private static final MessageInserter<OptionalFields, OptionalFields._Field> INSERTER =
            new MessageInserter.Builder<>(OptionalFields.kDescriptor, "mappings.default_mappings")
                    .set(ID, PRESENT, TINY, SMALL, MEDIUM, LARGE, REAL, NAME, DATA, FIB)
                    .set("compact", MESSAGE)
                    .set(TIMESTAMP_S, Types.TIMESTAMP)
                    .set(TIMESTAMP_MS, Types.TIMESTAMP)
                    .set(BINARY_MESSAGE, Types.VARBINARY)
                    .set(BLOB_MESSAGE, Types.BLOB)
                    .set("other_message", CLOB_MESSAGE, Types.CLOB)
                    .set(BLOB_DATA, Types.BLOB)
                    .set(BASE64_DATA, Types.VARCHAR)
                    .set(INT_BOOL, Types.INTEGER)
                    .onDuplicateKeyIgnore()
                    .build();

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
                                           // Since a number of DBs (MySQL 5) stores timestamp as second, not MS.
                                           .setTimestampMs(clock.instant().getEpochSecond() * 1000)
                                           .build();
        OptionalFields empty = OptionalFields.builder()
                                             .setId(2345)
                                             .build();

        try (Handle handle = db.getDBI().open()) {
            INSERTER.execute(handle, expected, empty);

            OptionalFields val = handle.createQuery("SELECT * FROM mappings.default_mappings WHERE id = :id")
                                       .bind("id", expected.getId())
                                       .map(ProvidenceJdbi.toMessage(OptionalFields.kDescriptor,
                                                                     ProvidenceJdbi.columnsFromAllFields(),
                                                                     ProvidenceJdbi.withColumn("compact", MESSAGE),
                                                                     ProvidenceJdbi.withColumn("other_message", CLOB_MESSAGE)))
                                       .first();
            OptionalFields val2 = handle.createQuery("SELECT * FROM mappings.default_mappings WHERE id = :id")
                                        .bind("id", empty.getId())
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
