namespace java org.apache.test.naming


enum Fields {
  sDescriptor,
  mName,
  mValue,
  Field
}

exception Builder {
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
}
