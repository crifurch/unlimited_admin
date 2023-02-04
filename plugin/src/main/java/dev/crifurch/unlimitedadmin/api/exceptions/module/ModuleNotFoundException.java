package dev.crifurch.unlimitedadmin.api.exceptions.module;

import dev.crifurch.unlimitedadmin.api.LangConfig;
import dev.crifurch.unlimitedadmin.api.exceptions.NotifibleException;

public class ModuleNotFoundException extends NotifibleException {
    public ModuleNotFoundException() {
        super(LangConfig.NO_SUCH_MODULE.getText());
    }
}
