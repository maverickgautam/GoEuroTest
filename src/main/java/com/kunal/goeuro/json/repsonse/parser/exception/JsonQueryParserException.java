package com.kunal.goeuro.json.repsonse.parser.exception;

import lombok.Getter;

/**
 * Checked Exception to be used
 */

public class JsonQueryParserException extends Exception {

    @Getter
    private final ErrorCode errorCode;

    public JsonQueryParserException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNKNOWN;
    }

    public JsonQueryParserException(String message, Throwable e) {
        super(message, e);
        this.errorCode = ErrorCode.UNKNOWN;
    }

    public JsonQueryParserException() {
        super();
        this.errorCode = ErrorCode.UNKNOWN;
    }


    public JsonQueryParserException(ErrorCode errorCode, Throwable e) {
        super(e);
        this.errorCode = errorCode;
    }

    public JsonQueryParserException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


    public JsonQueryParserException(ErrorCode errorCode, String message, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
    }


    public enum ErrorCode {
        HTTP_STATUS_CODE_ERROR,RESPONSE_FETCH_ERROR, JSON_PARSING_ERROR,FILE_WRITING_EXCEPTION ,UNKNOWN
    }
}
