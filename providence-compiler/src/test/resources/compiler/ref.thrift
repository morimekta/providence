namespace java net.morimekta.test.compiler_ref


struct Request {
    1: string text;
}

struct Response {
    1: string text;
}

exception Failure {
    1: string text;
}
