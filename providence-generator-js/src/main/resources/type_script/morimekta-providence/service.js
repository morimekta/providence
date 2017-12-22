// Generated with pvdc v1.0.0-SNAPSHOT

'use strict';
var _service = module.exports = exports;


/**
 * The service call type is a base distinction of what the message means, and
 * lets the server or client select the proper message to be serialized or
 * deserialized from the service method descriptor.
 */
_service.PServiceCallType = {
    /**
     * The service method request.
     */
    CALL: 1,
    /**
     * Normal method call reply. This includes declared exceptions on the
     * service method.
     */
    REPLY: 2,
    /**
     * An application exception, i.e. either a non-declared exception, or a
     * providence service or serialization exception. This is also happens when
     * such exceptions happen on the server side, it will try to send an
     * application exception back to the client.
     */
    EXCEPTION: 3,
    /**
     * A one-way call is a request that does not expect a response at all. The
     * client will return as soon as the request is sent.
     */
    ONEWAY: 4
};

/**
 * Get the value of the enum, given value or name
 */
_service.PServiceCallType.valueOf = function(id, opt_keepNumeric) {
    switch(id) {
        case 1:
        case '1':
        case 'call':
            return _service.PServiceCallType.CALL;
        case 2:
        case '2':
        case 'reply':
            return _service.PServiceCallType.REPLY;
        case 3:
        case '3':
        case 'exception':
            return _service.PServiceCallType.EXCEPTION;
        case 4:
        case '4':
        case 'oneway':
            return _service.PServiceCallType.ONEWAY;
        default:
            if (opt_keepNumeric && 'number' === typeof(id)) {
                return id;
            }
            return null;
    }
};

/**
 * Get the string name of the enum value.
 */
_service.PServiceCallType.nameOf = function(value, opt_keepNumeric) {
    switch(value) {
        case 1:
            return 'call';
        case 2:
            return 'reply';
        case 3:
            return 'exception';
        case 4:
            return 'oneway';
        default:
            if (!!opt_keepNumeric) return value;
            return null;
    }
};

/**
 * General type of exception on the application level.
 */
_service.PApplicationExceptionType = {
    /**
     * Unknown or unidentified exception, should usually not be uased.
     */
    UNKNOWN: 0,
    /**
     * There is no such method defined on the service.
     */
    UNKNOWN_METHOD: 1,
    /**
     * The service call type does not make sense, or is plain wrong, e.g.
     * sending &#39;reply&#39; or &#39;exception&#39; as the request.
     */
    INVALID_MESSAGE_TYPE: 2,
    /**
     * The response came back with a non-matching method name.
     */
    WRONG_METHOD_NAME: 3,
    /**
     * The response came back with a non-matching sequence ID.
     */
    BAD_SEQUENCE_ID: 4,
    /**
     * The response did not have a defined non-null result.
     * 
     * NOTE: This is the default behavior from thrift, and we may need to keep
     * it this way as long as thrift compatibility is expected.
     */
    MISSING_RESULT: 5,
    /**
     * The service handler or client handler experienced internal problem.
     */
    INTERNAL_ERROR: 6,
    /**
     * Serialization or deserialization failed or the deserialized content was
     * not valid for the requested message.
     * 
     * NOTE: In providence this is valid for server (processor) side
     * serialization errors.
     */
    PROTOCOL_ERROR: 7,
    /**
     * NOTE: This value is apparently not in use in thrift.
     */
    INVALID_TRANSFORM: 8,
    /**
     * The requested protocol (or version) is not supported.
     */
    INVALID_PROTOCOL: 9,
    /**
     * NOTE: This value is apparently not in use in thrift.
     */
    UNSUPPORTED_CLIENT_TYPE: 10
};

/**
 * Get the value of the enum, given value or name
 */
_service.PApplicationExceptionType.valueOf = function(id, opt_keepNumeric) {
    switch(id) {
        case 0:
        case '0':
        case 'UNKNOWN':
            return _service.PApplicationExceptionType.UNKNOWN;
        case 1:
        case '1':
        case 'UNKNOWN_METHOD':
            return _service.PApplicationExceptionType.UNKNOWN_METHOD;
        case 2:
        case '2':
        case 'INVALID_MESSAGE_TYPE':
            return _service.PApplicationExceptionType.INVALID_MESSAGE_TYPE;
        case 3:
        case '3':
        case 'WRONG_METHOD_NAME':
            return _service.PApplicationExceptionType.WRONG_METHOD_NAME;
        case 4:
        case '4':
        case 'BAD_SEQUENCE_ID':
            return _service.PApplicationExceptionType.BAD_SEQUENCE_ID;
        case 5:
        case '5':
        case 'MISSING_RESULT':
            return _service.PApplicationExceptionType.MISSING_RESULT;
        case 6:
        case '6':
        case 'INTERNAL_ERROR':
            return _service.PApplicationExceptionType.INTERNAL_ERROR;
        case 7:
        case '7':
        case 'PROTOCOL_ERROR':
            return _service.PApplicationExceptionType.PROTOCOL_ERROR;
        case 8:
        case '8':
        case 'INVALID_TRANSFORM':
            return _service.PApplicationExceptionType.INVALID_TRANSFORM;
        case 9:
        case '9':
        case 'INVALID_PROTOCOL':
            return _service.PApplicationExceptionType.INVALID_PROTOCOL;
        case 10:
        case '10':
        case 'UNSUPPORTED_CLIENT_TYPE':
            return _service.PApplicationExceptionType.UNSUPPORTED_CLIENT_TYPE;
        default:
            if (opt_keepNumeric && 'number' === typeof(id)) {
                return id;
            }
            return null;
    }
};

/**
 * Get the string name of the enum value.
 */
_service.PApplicationExceptionType.nameOf = function(value, opt_keepNumeric) {
    switch(value) {
        case 0:
            return 'UNKNOWN';
        case 1:
            return 'UNKNOWN_METHOD';
        case 2:
            return 'INVALID_MESSAGE_TYPE';
        case 3:
            return 'WRONG_METHOD_NAME';
        case 4:
            return 'BAD_SEQUENCE_ID';
        case 5:
            return 'MISSING_RESULT';
        case 6:
            return 'INTERNAL_ERROR';
        case 7:
            return 'PROTOCOL_ERROR';
        case 8:
            return 'INVALID_TRANSFORM';
        case 9:
            return 'INVALID_PROTOCOL';
        case 10:
            return 'UNSUPPORTED_CLIENT_TYPE';
        default:
            if (!!opt_keepNumeric) return value;
            return null;
    }
};

/**
 * Base exception thrown on non-declared exceptions on a service call, and
 * other server-side service call issues.
 */
_service.PApplicationException = function(opt_json) {
    Error.captureStackTrace(this, this.constructor);
    this.message = 'PApplicationException';

    this._message = null;
    this._id = null;

    if ('string' === typeof(opt_json)) {
        opt_json = JSON.parse(opt_json);
    }
    if ('object' === typeof(opt_json)) {
        for (var key in opt_json) {
            if (opt_json.hasOwnProperty(key)) {
                switch (key) {
                    case '1':
                    case 'message':
                        this._message = String(opt_json[key]);
                        break;
                    case '2':
                    case 'id':
                        this._id = _service.PApplicationExceptionType.valueOf(opt_json[key], true);
                        break;
                    default:
                        break;
                }
            }
        }
    } else if ('undefined' !== typeof(opt_json)){
        throw 'Bad json input type: ' + typeof(opt_json);
    }
};
_service.PApplicationException.prototype = new Error;

/**
 * Exception message.
 */
_service.PApplicationException.prototype.getMessage = function() {
    if (this._message === null) {
        return "";
    } else {
        return this._message;
    }
};

/**
 * Exception message.
 */
_service.PApplicationException.prototype.setMessage = function(value) {
    if (value !== null && value !== undefined) {
        this._message = String(value);
    } else {
        this._message = null;
    }
};

/**
 * The application exception type.
 */
_service.PApplicationException.prototype.getId = function() {
    if (this._id === null) {
        return PApplicationExceptionType.UNKNOWN;
    } else {
        return this._id;
    }
};

/**
 * The application exception type.
 */
_service.PApplicationException.prototype.setId = function(value) {
    if (value !== null && value !== undefined) {
        this._id = value;
    } else {
        this._id = null;
    }
};

/**
 * Make a JSON compatible object representation of the message.
 */
_service.PApplicationException.prototype.toJson = function(opt_named) {
    var obj = {};
    if (opt_named) {
        if (this._message !== null) {
            obj['message'] = this._message;
        }
        if (this._id !== null) {
            obj['id'] = _service.PApplicationExceptionType.nameOf(this._id, true);
        }
    } else {
        if (this._message !== null) {
            obj['1'] = this._message;
        }
        if (this._id !== null) {
            obj['2'] = this._id;
        }
    }
    return obj;
};

/**
 * Make a JSON string representation of the message.
 */
_service.PApplicationException.prototype.toJsonString = function(opt_named) {
    return JSON.stringify(this.toJson(opt_named));
};

/**
 * String representation of the message.
 */
_service.PApplicationException.prototype.toString = function() {
    return 'PApplicationException' + JSON.stringify(this.toJson(true));
};
