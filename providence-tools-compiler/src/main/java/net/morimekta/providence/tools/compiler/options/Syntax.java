package net.morimekta.providence.tools.compiler.options;

/**
 * While thrift input file syntax to expect. Each syntax has some
 * limitations, like how to reference between different files.
 */
public enum Syntax {
    thrift,
    json
}
