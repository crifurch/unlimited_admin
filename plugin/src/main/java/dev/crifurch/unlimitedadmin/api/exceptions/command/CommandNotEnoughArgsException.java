package dev.crifurch.unlimitedadmin.api.exceptions.command;

import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;

public class CommandNotEnoughArgsException extends NotifibleException {
    public CommandNotEnoughArgsException(String usage) {
        super(usage);
    }
}
