package com.chatbot.base.domain.product.constant;

public enum ProductStatus {
    REGISTRATION("등록", 3),
    DISPLAY("오늘매물", 1),
    PRE_DISPLAY("이전매물", 2),
    DELETE("삭제", 4);

    private String name;
    private int priority;  // 우선순위를 위한 필드 추가

    ProductStatus(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public static ProductStatus fromString(String name) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + name);
    }
}
