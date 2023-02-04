package dev.crifurch.unlimitedadmin.modules.chat.interfaces;

public interface ILoggedChat extends IChatChanel {
    default String getLogPrefix() {
        return "[" + getName() + "]";
    }


}
