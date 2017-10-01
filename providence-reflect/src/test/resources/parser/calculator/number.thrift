namespace java net.morimekta.test.number


typedef double real

struct Imaginary {
    1: required real v;
    2: double i = 0.0;
} (compact = "true")

typedef Imaginary I

const Imaginary kSqrtMinusOne = {
  "v": 0.0,
  "i": -1.0
};
