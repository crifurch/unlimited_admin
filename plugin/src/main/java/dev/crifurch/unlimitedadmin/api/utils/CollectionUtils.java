package dev.crifurch.unlimitedadmin.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
    public static <T> List<T> removeDuplicates(List<T> list) {
        ArrayList<T> newList = new ArrayList<>();
        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    public static <T> String join(List<T> list, @NotNull String separator) {
        StringBuilder result = new StringBuilder();
        for (T i : list) {
            result.append(i.toString());
            result.append(separator);
        }

        return result.substring(0, Math.max(0, result.length() - 1));
    }
}
