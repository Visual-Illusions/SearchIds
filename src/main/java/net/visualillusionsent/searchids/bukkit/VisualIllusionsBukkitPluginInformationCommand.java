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
package net.visualillusionsent.searchids.bukkit;

import net.visualillusionsent.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visual Illusions Bukkit Plugin Information command
 *
 * @author Jason (darkdiplomat)
 */
public abstract class VisualIllusionsBukkitPluginInformationCommand implements CommandExecutor {
    protected final List<String> about;
    protected final VisualIllusionsBukkitPlugin plugin;

    public VisualIllusionsBukkitPluginInformationCommand(VisualIllusionsBukkitPlugin plugin) {
        this.plugin = plugin;
        List<String> pre = new ArrayList<String>();
        pre.add(center(ChatColor.AQUA + "---" + ChatColor.GREEN + plugin.getName() + " " + ChatColor.GOLD + "v" + plugin.getDescription().getVersion() + ChatColor.AQUA + " ---"));
        pre.add("$VERSION_CHECK$");
        pre.add(ChatColor.AQUA + "Jenkins Build: " + ChatColor.GREEN + plugin.getBuild());
        pre.add(ChatColor.AQUA + "Built On: " + ChatColor.GREEN + plugin.getBuildTime());
        pre.add(ChatColor.AQUA + "Developer(s): " + ChatColor.GREEN + plugin.getDevelopers());
        pre.add(ChatColor.AQUA + "Website: " + ChatColor.GREEN + plugin.getWikiURL());
        pre.add(ChatColor.AQUA + "Issues: " + ChatColor.GREEN + plugin.getIssuesURL());

        // Next line should always remain at the end of the About
        pre.add(center("§BCopyright © 2012-2013 §AVisual §6I§9l§Bl§4u§As§2i§5o§En§7s §6Entertainment"));
        about = Collections.unmodifiableList(pre);
    }

    protected final String center(String toCenter) {
        String strColorless = ChatColor.stripColor(toCenter);
        return StringUtils.padCharLeft(toCenter, (int)(Math.floor(63 - strColorless.length()) / 2), ' ');
    }
}
