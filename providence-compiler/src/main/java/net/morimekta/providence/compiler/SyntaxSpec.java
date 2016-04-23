package net.morimekta.providence.compiler;

/**
 * While thrift input file syntax to expect. Each syntax has some
 * limitations, like how to reference between different files.
 */
enum SyntaxSpec {
    thrift,
    json
}
