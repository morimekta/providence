/**
 * Reflective thrift IDL description.
 *
 * Comments are gathered before the start of the next statement.
 *
 * - Line comments are accumulated with newline delimiter.
 *   Each line is individually trimmed.
 * - Block comments replace the entire comment.
 *   The first space after '*' on each line is ignored.
 */
namespace java net.morimekta.providence.model
namespace js morimekta.providence.model

/**
 * <name> (= <value>)
 */
struct EnumValue {
    1: optional string documentation;
    2: required string name;
    3: optional i32    id;
    4: optional map<string,string> annotations (container = "SORTED");
}

/**
 * enum {
 *   (<value> ([;,])?)*
 * }
 */
struct EnumType {
    1: optional string documentation;
    2: required string name;
    3:          list<EnumValue> values;
    4: optional map<string,string> annotations (container = "SORTED");
}

/**
 * typedef <type> <name>
 */
struct TypedefType {
    1: optional string documentation;
    2: required string type;
    3: required string name;
}

/**
 * Struct variant for StructType. The lower-case of the enum value is the
 * thrift keyword.
 *
 * struct: No 'required' fields must be present (set to non-null value).
 * UNION: No required fields. Only one field set to be valid.
 * EXCEPTION: No 'cause' field, 'message' field *must* be a string (java).
 */
enum MessageVariant {
    STRUCT = 1,
    UNION = 2,
    EXCEPTION = 3,
}

/**
 * The requirement of the field.
 */
enum FieldRequirement {
    DEFAULT = 0,
    OPTIONAL = 1,
    REQUIRED = 2,
}

/**
 * For fields:
 *   (<key>:)? (required|optional)? <type> <name> (= <default_value>)?
 * For const:
 *   const <type> <name> = <default_value>
 *
 * Fields without key is assigned values ranging from 65335 and down (2^16-1)
 * in order of appearance. Because of the "in order of appearance" the field
 * *must* be filled by the IDL parser.
 *
 * Consts are always given the key '0'.
 */
struct FieldType {
    1: optional string documentation;
    2: required i32    id;
    3: optional FieldRequirement requirement = DEFAULT;
    4: required string type;
    5: required string name;
    6: optional string default_value;
    7: optional map<string,string> annotations (container = "SORTED");

    // Note the start of the default value in the parsed thrift file, this
    // can be used for making more accurate exception / parse data from the
    // const parser.
    10: optional i32 start_line_no;
    11: optional i32 start_line_pos;
}

/**
 * <variant> {
 *   (<field> ([,;])?)*
 * }
 */
struct MessageType {
    1: optional string documentation;
    2: optional MessageVariant variant = MessageVariant.STRUCT;
    3: required string name;
    4:          list<FieldType> fields;
    5: optional map<string,string> annotations (container = "SORTED");
}

/**
 * (oneway)? <return_type> <name>'('<param>*')' (throws '(' <exception>+ ')')?
 */
struct FunctionType {
    1: optional string documentation;
    2: optional bool one_way = false;
    3: optional string return_type
    4: required string name;
    5:          list<FieldType> params = [];
    6: optional list<FieldType> exceptions = [];
    7: optional map<string,string> annotations (container = "SORTED");
}

/**
 * service (extends <extend>)? {
 *   (<method> [;,]?)*
 * }
 */
struct ServiceType {
    1: optional string documentation;
    2: required string name;
    3: optional string extend;
    4:          list<FunctionType> methods = [];
    5: optional map<string,string> annotations = {} (container = "SORTED");
}

/**
 * const <type> <name> = <value>
 */
struct ConstType {
    1: optional string documentation;
    4: required string type;
    5: required string name;
    6: required string value;
    7: optional map<string,string> annotations = {} (container = "SORTED");

    // Note the start of the const in the parsed thrift file, this can be used
    // for making more accurate exception / parse data from the const parser.
    10: optional i32 start_line_no;
    11: optional i32 start_line_pos;
}

/**
 * ( <enum> | <typedef> | <struct> | <service> | <const> )
 */
union Declaration {
    1: EnumType    decl_enum;
    2: TypedefType decl_typedef;
    3: MessageType decl_message;
    4: ServiceType decl_service;
    5: ConstType   decl_const;
}

/**
 * <namespace>* <include>* <declataion>*
 */
struct ProgramType {
    /**
     * Program documentation must come before the first statement of the header.
     */
    1: optional string documentation;

    /**
     * The program name, deducted from the .thrift IDL file name.
     */
    2: required string program_name;

    /**
     * List of included thrift files. Same as from the actual thrift file.
     *
     * include "<program>.thrift"
     */
    3: optional list<string> includes = [];

    /**
     * Map of language to laguage dependent namespace identifier.
     *
     * namespace <key> <value>
     */
    4: optional map<string,string> namespaces = {} (container = "SORTED");

    /**
     * List of declarations in the program file. Same order as in the thrift file.
     */
    5: optional list<Declaration> decl = [];
}

/**
 * Set of words used in thrift IDL as specific meanings.
 */
const set<string> kThriftKeywords = [
  // File header.
  "include", "namespace",
  // Primitive types.
  "bool", "byte", "i8", "i16", "i32", "i64", "double", "string", "binary",
  // Containers
  "list", "set", "map",
  // Defined Types keywords.
  "enum", "struct", "union", "exception", "const", "typedef", "service",
  // Field modifiers
  "required", "optional",
  // Extra keywords related to services.
  "extends", "throws", "oneway", "void"
];

/**
 * Other words that are reserved for language compat reasons. E.g. to enable
 * C++ classes to follow getter method naming convention to be raw field name
 * (java naming convention is typename safe):
 *
 * Apache Thrift has a pretty extensive list of reserved words. In order to be
 * compatible with Apache Thrift, all of these must be blocked from:
 *
 * - enum names
 * - enum value names
 * - message names
 * - message field names
 * - service names
 * - service method names
 *
 * This compatibility check can be turned off in the compiler and generator
 * if wanted, but should generally be kept on.
 */
const set<string> kReservedWords = [
    "abstract"
    "alias"
    "and"
    "args"
    "as"
    "assert"
    "begin"
    "break"
    "case"
    "catch"
    "class"
    "clone"
    "continue"
    "declare"
    "def"
    "default"
    "del"
    "delete"
    "do"
    "dynamic"
    "elif"
    "else"
    "elseif"
    "elsif"
    "end"
    "enddeclare"
    "endfor"
    "endforeach"
    "endif"
    "endswitch"
    "endwhile"
    "ensure"
    "except"
    "exec"
    "finally"
    "float"
    "for"
    "foreach"
    "from"
    "function"
    "global"
    "goto"
    "if"
    "implements"
    "import"
    "in"
    "inline"
    "instanceof"
    "interface"
    "is"
    "int"
    "lambda"
    "long"
    "module"
    "native"
    "new"
    "next"
    "nil"
    "not"
    "or"
    "package"
    "pass"
    "public"
    "print"
    "private"
    "protected"
    "raise"
    "redo"
    "rescue"
    "retry"
    "register"
    "return"
    "self"
    "short"
    "sizeof"
    "static"
    "super"
    "switch"
    "synchronized"
    "then"
    "this"
    "throw"
    "transient"
    "try"
    "undef"
    "unless"
    "unsigned"
    "until"
    "use"
    "var"
    "virtual"
    "volatile"
    "when"
    "while"
    "with"
    "xor"
    "yield"
];
