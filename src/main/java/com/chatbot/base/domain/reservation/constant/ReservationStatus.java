package com.chatbot.base.domain.reservation.constant;

public enum ReservationStatus {
    APPLY("신청"),
    ADMISSION("승인"),
    COMPLETE("완료"),
    CANCEL("취소"),
    ;

    private String name;

    ReservationStatus(String name) {
        this.name = name;
    }

    public static ReservationStatus fromString(String name) {
        for (ReservationStatus role : ReservationStatus.values()) {
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
