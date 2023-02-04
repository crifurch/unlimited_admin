package dev.crifurch.unlimitedadmin.modules.chat.interfaces;

public interface ISpiedChat extends IChatChanel {
    default String getSpyPrefix() {
        return "[" + getName() + "]";
    }
}
