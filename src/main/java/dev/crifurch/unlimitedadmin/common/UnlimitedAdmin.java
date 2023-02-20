package dev.crifurch.unlimitedadmin.common;

import dev.crifurch.unlimitedadmin.api.compat.playermeta.IPlayerMetaProvider;
import dev.crifurch.unlimitedadmin.api.compat.playermeta.PlayerMetaProvider;

public class UnlimitedAdmin {
    public static UnlimitedAdmin instance;

    static {
        instance = new UnlimitedAdmin();
    }

    public final IPlayerMetaProvider playerMetaProvider;


    private UnlimitedAdmin() {
        playerMetaProvider = new PlayerMetaProvider();
    }

}
