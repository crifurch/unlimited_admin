package dev.crifurch.unlimitedadmin.integrations.permissions;

public enum PermissionStatus {
    PERMISSION_UNSET,
    PERMISSION_TRUE,
    PERMISSION_FALSE;

    public boolean isPermittedOrUnset() {
        return this == PERMISSION_TRUE || this == PERMISSION_UNSET;
    }

    public boolean isDeniedOrUnset() {
        return this == PERMISSION_FALSE || this == PERMISSION_UNSET;
    }
}
