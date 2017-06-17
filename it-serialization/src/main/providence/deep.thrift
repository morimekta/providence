namespace java net.morimekta.test.providence.serialization.deep

include "common.thrift"

struct LevelFour {
    1: optional common.RequiredFields requiredFields;
    2: optional common.DefaultFields defaultFields;
    3: optional common.OptionalFields optionalFields;
    4: optional common.UnionFields unionFields;
    6: optional common.DefaultValues defaultValues;
    7: optional common.CompactFields compactFields;
}

struct LevelTree {
    1: required LevelFour four1;
    2: required LevelFour four2;
}

struct LevelTwo {
    1: required LevelTree tree1;
    2: required LevelTree tree2;
}

struct DeepStructure {
    1: required LevelTwo two1;
    2: required LevelTwo two2;
}