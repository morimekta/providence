namespace java net.morimekta.providence
namespace js morimekta.providence

/**
 * Dummy struct that is just empty. Can be used to "ignore" everything.
 */
struct Empty {}

/**
 * A message containing anything.
 */
struct Any {
    /**
     * The thrift / providence program + message type name. This should refer to a
     * message type. Enums will need to be wrapped in a message to be contained.
     */
    1: required string type;

    /**
     * The media type used for encoding. There will need to exist a serializer
     * registered for this. If the media type is not set, it is assumed to be
     * 'application/vnd.apache.thrift.binary', the default thrift serialization.
     */
    2: optional string media_type = "application/vnd.apache.thrift.binary";

    /**
     * The actual content binary data.
     */
    4: optional binary data (one_of = "content");

    /**
     * Optional string encoded content for non-binary media types. If this is filled
     * in, then the 'bin' field is not needed.
     */
    5: optional string text (one_of = "content");
}