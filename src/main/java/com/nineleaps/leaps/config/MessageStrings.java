package com.nineleaps.leaps.config;

public class MessageStrings {
    public static final String USER_NOT_PERMITTED = "User is not permitted to perform this operation";
    public static final String AUTH_TOKEN_NOT_PRESENT = "Authentication Token Not Present";
    public static final String AUTH_TOKEN_NOT_VALID = "Authentication Token Not Valid";
    public static final String USER_CREATED = "User Created Successfully";
    public static final String ID_NOT_PRESENT = "Primary key is required for updating";
    public static final String WRONG_CREDENTIAL = "Please Check The Credentials";
    public static final String ORDER_ITEM_UNAUTHORIZED_ACCESS = "OrderItem does not belong to current user";
    public static final String CART_ITEM_INVALID = "Cart Item is invalid: ";
    public static final String DELETED_PRODUCT_FILTER = "deletedProductFilter";
    public static final String DISABLED_PRODUCT_FILTER = "disabledProductFilter";
    public static final String DISABLED = "isDisabled";
    public static final String DELETED = "isDeleted";
    public static final String DEAR_PREFIX = "Dear ";
    public static final String TOTAL_NUMBER = "totalNumberOfItems";
    public static final String TOTAL_INCOME = "totalEarnings";

    private MessageStrings() {
        // Private constructor to prevent instantiation
    }
}
