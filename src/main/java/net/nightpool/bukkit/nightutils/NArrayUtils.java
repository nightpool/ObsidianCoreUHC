package net.nightpool.bukkit.nightutils;

import org.bukkit.ChatColor;

public class NArrayUtils {

    public static String toList(String[] array) {
        return toListColor(array, null, null);
    }

    public static String toListColor(String[] array, ChatColor stringsColor, ChatColor seperatorsColor) {
        String ret = "";
        String strings = (stringsColor == null) ? "" : stringsColor.toString();
        String seperators = (seperatorsColor == null) ? "" : seperatorsColor.toString();
        
        if (array.length > 0) {
            ret = strings + array[0];
            for (int i = 1; i < array.length; i++) {
                ret += seperators + ", " + strings + array[i];
            }
        }

        return ret;
    }
}
