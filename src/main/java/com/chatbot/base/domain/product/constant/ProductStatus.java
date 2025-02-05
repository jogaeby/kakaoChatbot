package com.chatbot.base.domain.product.constant;

public enum ProductStatus {
    REGISTRATION("등록"),
    DISPLAY("오늘매물"),
    DELETE("삭제"),
    ;

    private String name;

    ProductStatus(String name) {
        this.name = name;
    }

    public static ProductStatus fromString(String name) {
        for (ProductStatus role : ProductStatus.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + name);
    }
    public String getName() {
        return name;
    }
}
