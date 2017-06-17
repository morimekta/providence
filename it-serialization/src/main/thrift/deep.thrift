namespace java net.morimekta.test.thrift.serialization.deep

include "common.thrift"

struct OneLevel {
    1: optional common.RequiredFields requiredFields;
    2: optional common.DefaultFields defaultFields;
    3: optional common.OptionalFields optionalFields;
    4: optional common.UnionFields unionFields;
    5: optional common.ExceptionFields exceptionFields;
    6: optional common.DefaultValues defaultValues;
    7: optional common.CompactFields compactFields;
}

struct TwoLevels {
    1: optional OneLevel one1;
    2: optional OneLevel one2;
}

struct ThreeLevels {
    1: optional TwoLevels two1;
    2: optional TwoLevels two2;
}

struct DeepStructure {
    1: optional ThreeLevels three1;
    2: optional ThreeLevels three2;
}