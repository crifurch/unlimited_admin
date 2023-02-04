package dev.crifurch.unlimitedadmin.api.exceptions.command;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;

public class CommandOtherPermissionsException extends NotifibleException {
    public CommandOtherPermissionsException() {
        super(LangConfig.NO_PERMISSIONS_USE_ON_OTHER.getText());
    }

}
