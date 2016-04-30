namespace java net.morimekta.providence.serializer

enum ApplicationExceptionType {
  UNKNOWN = 0;
  UNKNOWN_METHOD = 1;
  INVALID_MESSAGE_TYPE = 2;
  WRONG_METHOD_NAME = 3;
  BAD_SEQUENCE_ID = 4;
  MISSING_RESULT = 5;
  INTERNAL_ERROR = 6;
  PROTOCOL_ERROR = 7;
  INVALID_TRANSFORM = 8;
  INVALID_PROTOCOL = 9;
  UNSUPPORTED_CLIENT_TYPE = 10;
}

/**
 * Base exception thrown on non-declared exceptions on a service call, and
 * other server-side service call issues.
 */
exception ApplicationException {
    1: string message;
    2: i32 id = 0;
}
