package net.morimekta.test.providence.core;

import net.morimekta.providence.descriptor.PList;

@SuppressWarnings("unused")
public class Providence_Constants {
    private Providence_Constants() {}

    public static final java.util.List<net.morimekta.test.providence.core.CompactFields> kDefaultCompactFields;
    static {
        kDefaultCompactFields = new PList.DefaultBuilder<CompactFields>()
                .add(net.morimekta.test.providence.core.CompactFields.builder()
                         .setName("Tut-Ankh-Amon")
                         .setId(1333)
                         .setLabel("dead")
                         .build())
                .add(net.morimekta.test.providence.core.CompactFields.builder()
                         .setName("Ramses II")
                         .setId(1279)
                         .build())
                .build();
    }
}