namespace java net.morimekta.test.naming


struct FieldNames {
    1: i32 lowercase;
    2: i32 camelCase;
    3: i32 PascalCase;
    4: i32 c_case;
    5: i32 UPPER_CASE;
    6: i32 Mixed_Case;
}

enum EnumNames {
    lowercase,
    camelCase,
    PascalCase,
    c_case,
    UPPER_CASE,
    Mixed_Case,
}

enum Enum {
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
    1: Factory Factory;
}

struct Field {
    1: Field Field;
    2: Factory Factory;
    3: Builder Builder;
    4: Enum Enum;

    5: i32 descriptor;
    6: i32 kDescriptor;
}
