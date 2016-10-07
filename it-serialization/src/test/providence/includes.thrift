namespace java net.morimekta.providence.it.serialization

include "number.thrift"

struct HasNumber {
    1: number.Imaginary i;
}
