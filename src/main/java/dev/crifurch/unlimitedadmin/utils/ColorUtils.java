package dev.crifurch.unlimitedadmin.utils;

public class ColorUtils {
    public static String replaceColorsSymbols(char altColorChar, String message) {
        char[] b = message.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '\u00A7';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String replaceColorsSymbols(String message) {
        return replaceColorsSymbols('&', message);
    }
}
