namespace java net.morimekta.test.providence.testing.includes

include "providence_model.thrift"

struct Program {
    1: providence_model.ProgramType program;
}
