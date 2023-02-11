package dev.crifurch.unlimitedadmin.fixes.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(ServerboundHelloPacket.class)
public class NameSpaceFixMixin {
    @Shadow
    @Final
    private GameProfile gameProfile;

    private GameProfile profile;

    /**
     * @author Crifurch
     * @reason Fix space in name
     */
    @Overwrite
    public GameProfile getGameProfile() {
        if(profile!=null) return profile;
        String name = this.gameProfile.getName();
        if(name.length() > 16) {
            name = name.substring(0, 16);
        }
        name = fixRussian(name);
        if(name.length() > 16) {
            name = name.substring(0, 16);
        }
        profile = new GameProfile(this.gameProfile.getId(), name);
        return profile;
    }

    private static String fixRussian(String name) {
        char[] abcCyr = {' ', 'а', 'б', 'в', 'г', 'д', 'ѓ', 'е', 'ж', 'з', 'ѕ', 'и', 'ј', 'к', 'л', 'љ', 'м', 'н', 'њ', 'о', 'п', 'р', 'с', 'т', 'ќ', 'у', 'ф', 'х', 'ц', 'ч', 'џ', 'ш', 'А', 'Б', 'В', 'Г', 'Д', 'Ѓ', 'Е', 'Ж', 'З', 'Ѕ', 'И', 'Ј', 'К', 'Л', 'Љ', 'М', 'Н', 'Њ', 'О', 'П', 'Р', 'С', 'Т', 'Ќ', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Џ', 'Ш', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '/', '-', 'Й', 'й', 'Ё', 'ё',};
        String[] abcLat = {"", "a", "b", "v", "g", "d", "]", "e", "zh", "z", "y", "i", "j", "k", "l", "q", "m", "n", "w", "o", "p", "r", "s", "t", "'", "u", "f", "h", "c", "ch", "x", "{", "A", "B", "V", "G", "D", "}", "E", "Zh", "Z", "Y", "I", "J", "K", "L", "Q", "M", "N", "W", "O", "P", "R", "S", "T", "KJ", "U", "F", "H", "C", ":", "X", "{", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "/", "-", "IY", "iy", "e", "e",};

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean found = false;
            for (int x = 0; x < abcCyr.length; x++) {
                if (c == abcCyr[x]) {
                    builder.append(abcLat[x]);
                    found = true;
                }
            }
            if (!found) {
                if (c > 0 && c <= 127) {
                    builder.append(c);
                } else {
                    builder.append("_");
                }
            }
        }
        return builder.toString();
    }
}
