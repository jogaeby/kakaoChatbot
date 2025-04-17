package com.chatbot.base.domain.reservation.constant;

public enum ReservationType {
    TRIAL("체험", 1),
    INTERVIEW("인터뷰", 2);
    private String name;
    private int priority;  // 우선순위를 위한 필드 추가

    ReservationType(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public static ReservationType fromString(String name) {
        for (ReservationType status : ReservationType.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + name);
    }
}
