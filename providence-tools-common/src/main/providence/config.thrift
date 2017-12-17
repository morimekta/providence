namespace java net.morimekta.providence.tools.common

struct ProvidenceTools {
    // -------------------------
    // --  CONVERTER  /  RPC  --
    // -------------------------
    /**
     * Base path for includes in the list below. If not
     * supplied all includes below must be absolute
     * paths.
     */
    1: optional string includes_base_path;

    /**
     * List of included paths that the
     */
    2: optional list<string> includes;

    // -------------------------
    // --      COMPILER       --
    // -------------------------

    /**
     * Extra path locations where the pvdc compiler can find
     * generators. These must point to directories where each
     * .jar file is a packaged generator. See documentation
     * on 'GeneratorFactory' for details.
     */
    3: optional list<string> generator_paths;
}
