namespace java net.morimekta.test.providence.core.number


typedef double real

struct Imaginary {
    1: required double v;
    2: double i = 0.0;
} (java.public.constructor)

const Imaginary kSqrtMinusOne = {
  "v": 0.0,
  "i": -1.0
};
