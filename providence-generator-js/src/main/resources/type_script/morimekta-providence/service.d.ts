// Generated with pvdc v1.0.0-SNAPSHOT

/**
 * The service call type is a base distinction of what the message means, and
 * lets the server or client select the proper message to be serialized or
 * deserialized from the service method descriptor.
 */
export declare enum PServiceCallType {
    /**
     * The service method request.
     */
    CALL = 1,
    /**
     * Normal method call reply. This includes declared exceptions on the
     * service method.
     */
    REPLY = 2,
    /**
     * An application exception, i.e. either a non-declared exception, or a
     * providence service or serialization exception. This is also happens when
     * such exceptions happen on the server side, it will try to send an
     * application exception back to the client.
     */
    EXCEPTION = 3,
    /**
     * A one-way call is a request that does not expect a response at all. The
     * client will return as soon as the request is sent.
     */
    ONEWAY = 4
}

/**
 * Get the value of the enum, given value or name
 *
 * @param {number|string} id Identification for enum value
 * @param {boolean=} opt_keepNumeric Optional arg to keep numeric values even if invalid.
 * @return {_service.PServiceCallType?} The enum value if valid.
 */
namespace PServiceCallType {
    export function valueOf(id:any, opt_keepNumeric?:boolean):number;

    /**
     * Get the string name of the enum value.
     *
     * @param {service.PServiceCallType} value The enum value
     * @return {string?} The enum name.
     */
    export function nameOf(value:any, opt_keepNumeric?:boolean):string;

}
/**
 * General type of exception on the application level.
 */
export declare enum PApplicationExceptionType {
    /**
     * Unknown or unidentified exception, should usually not be uased.
     */
    UNKNOWN = 0,
    /**
     * There is no such method defined on the service.
     */
    UNKNOWN_METHOD = 1,
    /**
     * The service call type does not make sense, or is plain wrong, e.g.
     * sending &#39;reply&#39; or &#39;exception&#39; as the request.
     */
    INVALID_MESSAGE_TYPE = 2,
    /**
     * The response came back with a non-matching method name.
     */
    WRONG_METHOD_NAME = 3,
    /**
     * The response came back with a non-matching sequence ID.
     */
    BAD_SEQUENCE_ID = 4,
    /**
     * The response did not have a defined non-null result.
     * 
     * NOTE: This is the default behavior from thrift, and we may need to keep
     * it this way as long as thrift compatibility is expected.
     */
    MISSING_RESULT = 5,
    /**
     * The service handler or client handler experienced internal problem.
     */
    INTERNAL_ERROR = 6,
    /**
     * Serialization or deserialization failed or the deserialized content was
     * not valid for the requested message.
     * 
     * NOTE: In providence this is valid for server (processor) side
     * serialization errors.
     */
    PROTOCOL_ERROR = 7,
    /**
     * NOTE: This value is apparently not in use in thrift.
     */
    INVALID_TRANSFORM = 8,
    /**
     * The requested protocol (or version) is not supported.
     */
    INVALID_PROTOCOL = 9,
    /**
     * NOTE: This value is apparently not in use in thrift.
     */
    UNSUPPORTED_CLIENT_TYPE = 10
}

/**
 * Get the value of the enum, given value or name
 *
 * @param {number|string} id Identification for enum value
 * @param {boolean=} opt_keepNumeric Optional arg to keep numeric values even if invalid.
 * @return {_service.PApplicationExceptionType?} The enum value if valid.
 */
namespace PApplicationExceptionType {
    export function valueOf(id:any, opt_keepNumeric?:boolean):number;

    /**
     * Get the string name of the enum value.
     *
     * @param {service.PApplicationExceptionType} value The enum value
     * @return {string?} The enum name.
     */
    export function nameOf(value:any, opt_keepNumeric?:boolean):string;

}
export declare class PApplicationException {
    private _message: string;
    private _id: PApplicationExceptionType;

    /**
     * Base exception thrown on non-declared exceptions on a service call, and
     * other server-side service call issues.
     */
    constructor(opt_json?:any);

    /**
     * Exception message.
     */
    getMessage():string;

    /**
     * Exception message.
     */
    setMessage(value?:string):void;

    /**
     * The application exception type.
     */
    getId():PApplicationExceptionType;

    /**
     * The application exception type.
     */
    setId(value?:PApplicationExceptionType):void;

    /**
     * Make a JSON compatible object representation of the message.
     *
     * @param {boolean=} opt_named Optional use named json.
     * @return {Object} Json representation.
     */
    toJson(opt_named?:boolean): any {

    /**
     * Make a JSON string representation of the message.
     *
     * @param {boolean=} opt_named Optional use named json.
     * @return {string} The stringified json.
     */
    toJsonString(opt_named?:boolean):string;

    /**
     * String representation of the message.
     *
     * @return {string} Message as string.
     */
    toString():string;

}