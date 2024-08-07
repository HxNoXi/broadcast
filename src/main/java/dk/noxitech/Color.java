package dk.noxitech;

import java.util.List;
import net.md_5.bungee.api.ChatColor;

public class Color {
    public static String plain(String s) {
        return s.replace(",", "&");
    }

    public static String[] getColored(String... strings) {
        if (strings == null)
            return null;
        for (int i = 0; i < strings.length; i++)
            strings[i] = getColored(strings[i]);
        return strings;
    }

    public static List<String> getColored(List<String> strings) {
        if (strings == null)
            return null;
        strings.replaceAll(Color::getColored);
        return strings;
    }

    public static String getColored(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}