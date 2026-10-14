package com.election.enums;

public enum StudentType {
    REGULAR("51"),
    LATERAL_ENTRY("55");

    private final String code;

    StudentType(String code) {
        this.code = code;
    }

    public static StudentType fromCode(String code) {
        for (StudentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown student type code: " + code);
    }
}
