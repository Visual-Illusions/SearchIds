/*
 * This file is part of SearchIds.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * SearchIds is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * SearchIds is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with SearchIds.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.searchids.canary;

import net.canarymod.Canary;
import net.canarymod.chat.Colors;
import net.canarymod.chat.TextFormat;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.visualillusionsent.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visual Illusions Canary Plugin Information command
 *
 * @author Jason (darkdiplomat)
 */
public abstract class VisualIllusionsCanaryPluginInformationCommand implements CommandListener {
    protected final List<String> about;
    protected final VisualIllusionsCanaryPlugin plugin;

    public VisualIllusionsCanaryPluginInformationCommand(VisualIllusionsCanaryPlugin plugin) {
        this.plugin = plugin;
        List<String> pre = new ArrayList<String>();
        pre.add(center(Colors.CYAN + "---" + Colors.LIGHT_GREEN + plugin.getName() + " " + Colors.ORANGE + "v" + plugin.getVersion() + Colors.CYAN + " ---"));
        pre.add("$VERSION_CHECK$");
        pre.add(Colors.CYAN + "Jenkins Build: " + Colors.LIGHT_GREEN + plugin.getBuild());
        pre.add(Colors.CYAN + "Built On: " + Colors.LIGHT_GREEN + plugin.getBuildTime());
        pre.add(Colors.CYAN + "Developer(s): " + Colors.LIGHT_GREEN + plugin.getDevelopers());
        pre.add(Colors.CYAN + "Website: " + Colors.LIGHT_GREEN + plugin.getWikiURL());
        pre.add(Colors.CYAN + "Issues: " + Colors.LIGHT_GREEN + plugin.getIssuesURL());

        // Next line should always remain at the end of the About
        pre.add(center("§BCopyright © 2012-2013 §AVisual §6I§9l§Bl§4u§As§2i§5o§En§7s §6Entertainment"));
        about = Collections.unmodifiableList(pre);
    }

    protected final String center(String toCenter) {
        String strColorless = TextFormat.removeFormatting(toCenter);
        return StringUtils.padCharLeft(toCenter, (int)(Math.floor(63 - strColorless.length()) / 2), ' ');
    }
}
