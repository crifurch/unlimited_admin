package dev.crifurch.unlimitedadmin.api.exceptions.command;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;

public class CommandErrorException extends NotifibleException {

    public CommandErrorException() {
        super(LangConfig.ERROR_WHILE_COMMAND.getText());
    }

    public CommandErrorException(String message) {
        super(message);
    }
}
