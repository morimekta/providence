namespace java net.morimekta.test.providence.testing.math

include "../calculator/number.thrift"

struct OtherCalculator {
    1: optional number.Imaginary math;
}

const string kValue = "constant value";

const map<string,i32> kMap = {
    kValue : 12
};