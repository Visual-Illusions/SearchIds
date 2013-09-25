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

import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.utils.VersionChecker;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command Executor for Bukkit
 *
 * @author Jason (darkdiplomat)
 */
public class BukkitSearchCommandExecutor extends VisualIllusionsBukkitPluginInformationCommand {

    private final BukkitSearchIds searchids;

    BukkitSearchCommandExecutor(BukkitSearchIds searchids) {
        super(searchids);
        this.searchids = searchids;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("search")) {
            if (args.length > 0) {
                String query = StringUtils.join(args, ' ', 0, args.length - 1);
                if (sender instanceof Player) {
                    searchids.printSearchResults((Player)sender, BukkitSearchIds.parser.search(query, SearchIdsProperties.base), query);
                }
                else {
                    searchids.printConsoleSearchResults(BukkitSearchIds.parser.search(query, SearchIdsProperties.base), query);
                }
            }
            else {
                sender.sendMessage("§cCorrect usage is: /search [item to search for]");
            }
            return true;
        }
        else if (label.equals("searchids")) {
            for (String msg : about) {
                if (msg.equals("$VERSION_CHECK$")) {
                    VersionChecker vc = plugin.getVersionChecker();
                    Boolean islatest = vc.isLatest();
                    if (islatest == null) {
                        sender.sendMessage(center(ChatColor.DARK_GRAY + "VersionCheckerError: " + vc.getErrorMessage()));
                    }
                    else if (!islatest) {
                        sender.sendMessage(center(ChatColor.DARK_GRAY + vc.getUpdateAvailibleMessage()));
                    }
                    else {
                        sender.sendMessage(center(ChatColor.GREEN + "Latest Version Installed"));
                    }
                }
                else {
                    sender.sendMessage(msg);
                }
            }
        }
        return false;
    }
}
