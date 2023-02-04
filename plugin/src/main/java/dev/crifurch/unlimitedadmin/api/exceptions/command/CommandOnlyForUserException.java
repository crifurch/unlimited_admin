package dev.crifurch.unlimitedadmin.api.exceptions.command;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;

public class CommandOnlyForUserException extends NotifibleException {
    public CommandOnlyForUserException() {
        super(LangConfig.ONLY_FOR_PLAYER_COMMAND.getText());
    }

    public CommandOnlyForUserException(String message) {
        super(message);
    }
}
