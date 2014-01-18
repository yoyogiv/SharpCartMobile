package com.sharpcart.android.dao;

public class ShoppingItemDAO {
    private static final ShoppingItemDAO instance = new ShoppingItemDAO();

    private ShoppingItemDAO() {
    }

    public static ShoppingItemDAO getInstance() {
    	return instance;
    }
}
