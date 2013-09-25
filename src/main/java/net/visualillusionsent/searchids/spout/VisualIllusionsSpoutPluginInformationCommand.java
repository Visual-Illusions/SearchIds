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
package net.visualillusionsent.searchids.spout;

import net.visualillusionsent.utils.StringUtils;
import org.spout.vanilla.ChatStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visual Illusions Spout Plugin Information command
 *
 * @author Jason (darkdiplomat)
 */
public abstract class VisualIllusionsSpoutPluginInformationCommand {
    protected final List<String> about;
    protected final VisualIllusionsSpoutPlugin plugin;

    public VisualIllusionsSpoutPluginInformationCommand(VisualIllusionsSpoutPlugin plugin) {
        this.plugin = plugin;
        List<String> pre = new ArrayList<String>();
        pre.add(center(ChatStyle.AQUA + "---" + ChatStyle.GREEN + plugin.getName() + " " + ChatStyle.GOLD + "v" + plugin.getDescription().getVersion() + ChatStyle.AQUA + " ---"));
        pre.add("$VERSION_CHECK$");
        pre.add(ChatStyle.AQUA + "Jenkins Build: " + ChatStyle.GREEN + plugin.getBuild());
        pre.add(ChatStyle.AQUA + "Built On: " + ChatStyle.GREEN + plugin.getBuildTime());
        pre.add(ChatStyle.AQUA + "Developer(s): " + ChatStyle.GREEN + plugin.getDevelopers());
        pre.add(ChatStyle.AQUA + "Website: " + ChatStyle.GREEN + plugin.getWikiURL());
        pre.add(ChatStyle.AQUA + "Issues: " + ChatStyle.GREEN + plugin.getIssuesURL());

        // Next line should always remain at the end of the About
        pre.add(center("§BCopyright © 2012-2013 §AVisual §6I§9l§Bl§4u§As§2i§5o§En§7s §6Entertainment"));
        about = Collections.unmodifiableList(pre);
    }

    protected final String center(String toCenter) {
        String strColorless = toCenter.replaceAll("\u00A7[A-FK-NRa-fk-nr0-9]", "");
        return StringUtils.padCharLeft(toCenter, (int)(Math.floor(63 - strColorless.length()) / 2), ' ');
    }
}
