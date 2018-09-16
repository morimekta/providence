namespace java net.morimekta.providence
namespace js morimekta.providence

/**
 * The service call type is a base distinction of what the message means, and
 * lets the server or client select the proper message to be serialized or
 * deserialized from the service method descriptor.
 */
enum PServiceCallType {
    /**
     * Normal service method call request.
     */
    CALL = 1;

    /**
     * Normal method call reply. This includes declared exceptions on the
     * service method.
     */
    REPLY = 2;

    /**
     * An application exception, i.e. either a non-declared exception, or a
     * providence service or serialization exception. This is also happens when
     * such exceptions happen on the server side, it will try to send an
     * application exception back to the client.
     */
    EXCEPTION = 3;

    /**
     * A one-way call is a request that does not expect a response at all. The
     * client will return as soon as the request is sent.
     */
    ONEWAY = 4;
}

/**
 * General type of exception on the application level.
 */
enum PApplicationExceptionType {
  /**
   * Unknown or unidentified exception, should usually not be uased.
   */
  UNKNOWN = 0;

  /**
   * There is no such method defined on the service.
   */
  UNKNOWN_METHOD = 1;

  /**
   * The service call type does not make sense, or is plain wrong, e.g.
   * sending 'reply' or 'exception' as the request.
   */
  INVALID_MESSAGE_TYPE = 2;

  /**
   * The response came back with a non-matching method name.
   */
  WRONG_METHOD_NAME = 3;

  /**
   * The response came back with a non-matching sequence ID.
   */
  BAD_SEQUENCE_ID = 4;

  /**
   * The response did not have a defined non-null result.
   *
   * NOTE: This is the default behavior from thrift, and we may need to keep
   * it this way as long as thrift compatibility is expected.
   */
  MISSING_RESULT = 5;

  /**
   * The service handler or client handler experienced internal problem.
   */
  INTERNAL_ERROR = 6;

  /**
   * Serialization or deserialization failed or the deserialized content was
   * not valid for the requested message.
   *
   * NOTE: In providence this is valid for server (processor) side
   * serialization errors.
   */
  PROTOCOL_ERROR = 7;

  /**
   * NOTE: This value is apparently not in use in thrift.
   */
  INVALID_TRANSFORM = 8;

  /**
   * The requested protocol (or version) is not supported.
   */
  INVALID_PROTOCOL = 9;

  /**
   * NOTE: This value is apparently not in use in thrift.
   */
  UNSUPPORTED_CLIENT_TYPE = 10;
}

/**
 * Base exception thrown on non-declared exceptions on a service call, and
 * other server-side service call issues.
 */
exception PApplicationException {
    /**
     * Exception message.
     */
    1: optional string message;

    /**
     * The application exception type.
     */
    2: optional PApplicationExceptionType id = PApplicationExceptionType.UNKNOWN;
} (java.exception.class = "java.io.IOException",
   java.public.constructor = "")
