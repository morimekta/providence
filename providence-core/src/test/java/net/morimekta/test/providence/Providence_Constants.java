package net.morimekta.test.providence;

@SuppressWarnings("unused")
public class Providence_Constants {
    private Providence_Constants() {}

    public static final java.util.List<net.morimekta.test.providence.CompactFields> kDefaultCompactFields;
    static {
        kDefaultCompactFields = new net.morimekta.providence.descriptor.PList.ImmutableListBuilder<net.morimekta.test.providence.CompactFields>()
                .add(net.morimekta.test.providence.CompactFields.builder()
                        .setName("Tut-Ankh-Amon")
                        .setId(1333)
                        .setLabel("dead")
                        .build())
                .add(net.morimekta.test.providence.CompactFields.builder()
                        .setName("Ramses II")
                        .setId(1279)
                        .build())
                .build();
    }
}