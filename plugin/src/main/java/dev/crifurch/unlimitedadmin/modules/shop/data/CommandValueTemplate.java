package dev.crifurch.unlimitedadmin.modules.shop.data;

import java.util.Objects;

public class CommandValueTemplate {
    public static CommandValueTemplate PLAYER_NAME = new CommandValueTemplate("$playerName$", null);
    public static CommandValueTemplate ONLINE_PLAYERS = new CommandValueTemplate("$onlinePlayers$", null);
    public static CommandValueTemplate OTHER = new CommandValueTemplate(null, null);

    private final String template;
    private final String value;


    CommandValueTemplate(String template, String value) {
        this.template = template;
        this.value = value;
    }

    public static CommandValueTemplate getTypeFromValue(String value) {
        if (value.startsWith("$playerName$")) {
            return PLAYER_NAME;
        } else if (value.startsWith("$onlinePlayers$")) {
            return ONLINE_PLAYERS;
        } else {
            return new CommandValueTemplate(null, value);
        }
    }

    public String getValue() {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandValueTemplate that = (CommandValueTemplate) o;
        return Objects.equals(template, that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, value);
    }
}
