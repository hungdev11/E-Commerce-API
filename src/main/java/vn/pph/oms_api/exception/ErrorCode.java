package vn.pph.oms_api.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    SOME_THING_WENT_WRONG(999, "Not OK", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1100, "Product not found", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(1101, "Product already existed", HttpStatus.BAD_REQUEST),
    ATTR_EXISTED(1150, "Attribute already existed", HttpStatus.BAD_REQUEST),
    USER_EMAIL_EXISTED(1200, "User email already existed", HttpStatus.BAD_REQUEST),
    USER_NAME_EXISTED(1201, "User name already existed", HttpStatus.BAD_REQUEST),
    USER_ID_DIFF_ID_IN_TOKEN(1204, "User id in token is different", HttpStatus.BAD_REQUEST),
    USER_CREATE_FAILED(1202, "User create failed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1203, "Can not find user", HttpStatus.BAD_REQUEST),
    INVALID_PRIVATE_KEY(1250, "Invalid private key", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1251, "Login failed", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(1252, "Token has been expired", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(1253, "Token with user id not found", HttpStatus.BAD_REQUEST)
    ;
    int code;
    String message;
    HttpStatus statusCode;
    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
