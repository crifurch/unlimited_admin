package dev.crifurch.unlimitedadmin.modules.playersmap.data;

import java.time.LocalDateTime;

public class CachedPlayer {
    public final String uuid;
    public final String name;
    public LocalDateTime lastSign;

    public CachedPlayer(String uuid, String name, LocalDateTime lastSign) {
        this.uuid = uuid;
        this.name = name;
        this.lastSign = lastSign;
    }
}
