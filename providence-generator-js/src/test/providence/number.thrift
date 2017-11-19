namespace java net.morimekta.test.providence.testing.number
namespace js pvd.testing


typedef double real

struct Imaginary {
    1: required double v;
    2: optional double i = 0.0;
}

const Imaginary kSqrtMinusOne = {
  "v": 0.0,
  "i": -1.0
};
