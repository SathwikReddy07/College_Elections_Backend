package com.election.enums;

public enum Branch {
    ECE("04", "ECE"),
    CSE("05", "CSE");

    private final String code;
    private final String displayName;

    Branch(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Branch fromCode(String code) {
        for (Branch branch : values()) {
            if (branch.code.equalsIgnoreCase(code)) {
                return branch;
            }
        }
        throw new IllegalArgumentException("Unknown branch code: " + code);
    }
}
