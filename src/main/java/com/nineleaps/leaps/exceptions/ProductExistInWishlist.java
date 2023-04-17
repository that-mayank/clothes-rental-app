package com.nineleaps.leaps.exceptions;

public class ProductExistInWishlist extends IllegalArgumentException{
    public ProductExistInWishlist(String msg) {
        super(msg);
    }
}
