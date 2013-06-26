package net.visualillusionsent.searchids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.canarymod.chat.Colors;
import net.canarymod.chat.TextFormat;
import net.visualillusionsent.utils.StringUtils;

public final class SearchIdsInformation {

    private final List<String> about;

    public SearchIdsInformation(SearchIds plugin) {
        List<String> pre = new ArrayList<String>();
        pre.add(center(Colors.CYAN + "--- " + Colors.LIGHT_GREEN + plugin.getName() + Colors.ORANGE + " v" + plugin.getRawVersion() + Colors.CYAN + " ---"));
        pre.add("$VERSION_CHECK$");
        pre.add(Colors.CYAN + "Build: " + Colors.LIGHT_GREEN + plugin.getBuildNumber());
        pre.add(Colors.CYAN + "Built: " + Colors.LIGHT_GREEN + plugin.getBuildTime());
        pre.add(Colors.CYAN + "Developer: " + Colors.LIGHT_GREEN + "DarkDiplomat");
        pre.add(Colors.CYAN + "Website: " + Colors.LIGHT_GREEN + "http://wiki.visualillusionsent.net/" + plugin.getName());
        pre.add(Colors.CYAN + "Issues: " + Colors.LIGHT_GREEN + "https://github.com/Visual-Illusions/" + plugin.getName() + "/issues");

        // Next line should always remain at the end of the About
        pre.add(center("§aCopyright © 2012-2013 §2Visual §6I§9l§bl§4u§as§2i§5o§en§7s §2Entertainment"));
        about = Collections.unmodifiableList(pre);
    }

    public final List<String> getAbout() {
        return about;
    }

    public final String center(String toCenter) {
        String strColorless = TextFormat.removeFormatting(toCenter);
        return StringUtils.padCharLeft(toCenter, (int) (Math.floor(63 - strColorless.length()) / 2), ' ');
    }
}
