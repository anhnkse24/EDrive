package com.swp391.edrive.enums;

public enum Gender {
    NAM("Nam"),
    NU("Nữ"),
    KHAC("Khác");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
