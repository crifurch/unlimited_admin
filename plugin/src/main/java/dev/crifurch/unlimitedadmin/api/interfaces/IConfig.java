package dev.crifurch.unlimitedadmin.api.interfaces;

public interface IConfig {

    String getPath();

    String getDescription();

    Object getDefaultValue();

    default boolean isOptional() {
        return false;
    }
}
