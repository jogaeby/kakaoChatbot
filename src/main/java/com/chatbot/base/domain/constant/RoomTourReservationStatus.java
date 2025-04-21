package com.chatbot.base.domain.constant;

import com.chatbot.base.domain.member.constant.MemberRole;

public enum RoomTourReservationStatus {
    RECEIPT("접수"),
    ASSIGNMENT ("배정 완료"),
    ;

    private String name;

    RoomTourReservationStatus(String name) {
        this.name = name;
    }

    public static RoomTourReservationStatus fromString(String name) {
        for (RoomTourReservationStatus role : RoomTourReservationStatus.values()) {
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
