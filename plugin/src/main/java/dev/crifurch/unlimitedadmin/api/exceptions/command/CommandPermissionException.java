package dev.crifurch.unlimitedadmin.api.exceptions.command;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;

public class CommandPermissionException extends NotifibleException {
    public CommandPermissionException() {
        super(LangConfig.NO_PERMISSIONS.getText());
    }
}
