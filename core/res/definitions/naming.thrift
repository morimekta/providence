namespace java net.morimekta.test.naming


enum Fields {
  sDescriptor,
  mName,
  mValue,
  Field
}

/** @deprecated */
exception Builder {
    /** @deprecated */
    1: Builder Builder;
}

union Factory {
    1: Provider Factory;
}

struct Provider {
    1: Provider Provider;
    2: Factory Factory;
    3: Builder Builder;
    4: Fields Fields;

    5: i32 descriptor;
    6: i32 kDescriptor;
}
