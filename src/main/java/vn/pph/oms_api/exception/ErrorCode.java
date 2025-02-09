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
    MISSING_SKU(1120, "Product need at least one sku", HttpStatus.BAD_REQUEST),
    ATTR_EXISTED(1150, "Attribute already existed", HttpStatus.BAD_REQUEST),
    ATTR_NOT_EXIST(1150, "Attribute not exist", HttpStatus.BAD_REQUEST),
    USER_EMAIL_EXISTED(1200, "User email already existed", HttpStatus.BAD_REQUEST),
    USER_NAME_EXISTED(1201, "User name already existed", HttpStatus.BAD_REQUEST),
    USER_ID_DIFF_ID_IN_TOKEN(1204, "User id in token is different", HttpStatus.BAD_REQUEST),
    USER_CREATE_FAILED(1202, "User create failed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1203, "Can not find user", HttpStatus.BAD_REQUEST),
    INVALID_PRIVATE_KEY(1250, "Invalid private key", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1251, "Login failed", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(1252, "Token has been expired", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(1253, "Token with user id not found", HttpStatus.BAD_REQUEST),
    SKU_NOT_FOUND(1300, "Sku not found", HttpStatus.BAD_REQUEST),
    SKU_INCOMPATIBLE_PRODUCT(1301, "Product did not contain this sku", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_QUANTITY(1400, "Maximum quantity must be greater than max discount user can use", HttpStatus.BAD_REQUEST),
    DISCOUNT_EXISTED(1401, "Shop already has that discount code", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_BELONG_TO_SHOP(1402, "Some product not belong to shop", HttpStatus.BAD_REQUEST),
    INVALID_PERCENT_VALUE(1403, "Percent must in [0, 100]", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_FOUND(1404, "Discount code doesn't exists", HttpStatus.BAD_REQUEST),
    DISCOUNT_INACTIVE(1405, "Discount code is inactive user can't see it", HttpStatus.BAD_REQUEST),
    DISCOUNT_OUT_OF_QUANTITY(1406, "Discount code is out of quantity", HttpStatus.BAD_REQUEST),
    DISCOUNT_HAS_USED(1407, "Discount has at least used by one user", HttpStatus.BAD_REQUEST),
    CANNOT_USE_DISCOUNT(1408, "Discount time out", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_BELONG_TO_SHOP(1409, "Shop don't have that discount", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1408, "Can not find cart", HttpStatus.BAD_REQUEST),
    CART_STATUS_INVALID(1409, "Cart is inactive", HttpStatus.BAD_REQUEST),
    INCONSISTENCY(1410, "Two value are in-consistence", HttpStatus.BAD_REQUEST),
    INVENTORY_NOT_FOUND(1411, "This sku doesn't have inventory", HttpStatus.BAD_REQUEST),
    INVENTORY_OUT_OF_STOCK(1412, "This sku is out of stock", HttpStatus.BAD_REQUEST),
    PRODUCT_IS_PRIVATE(1413, "This sku is not publish", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1500, "Order doesn't exist", HttpStatus.BAD_REQUEST),
    ORDER_NOT_BELONG_TO_SHOP(1501, "Order not belong to shop", HttpStatus.BAD_REQUEST),
    ORDER_NOT_BELONG_TO_USER(1502, "Order not belong to user", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_NOT_CHANGEABLE(1503, "Order stats can't not change", HttpStatus.BAD_REQUEST),
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
