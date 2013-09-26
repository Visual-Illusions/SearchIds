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

import net.visualillusionsent.searchids.Result;
import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.utils.VersionChecker;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;

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
        // Initialize Commands
        searchids.getCommand("search").setExecutor(this);
        searchids.getCommand("searchids").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("search")) {
            if (args.length > 0) {
                String query = StringUtils.join(args, ' ', 0, args.length - 1);
                printSearchResults((Player)sender, searchids.getParser().search(query, searchids.properties.base()), query);
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
                        sender.sendMessage(center(ChatColor.DARK_GRAY.toString().concat("VersionCheckerError: ").concat(vc.getErrorMessage())));
                    }
                    else if (!islatest) {
                        sender.sendMessage(center(ChatColor.DARK_GRAY.toString().concat(vc.getUpdateAvailibleMessage())));
                    }
                    else {
                        sender.sendMessage(center(ChatColor.GREEN.toString().concat("Latest Version Installed")));
                    }
                }
                else {
                    sender.sendMessage(msg);
                }
            }
        }
        return false;
    }

    private void printSearchResults(CommandSender sender, ArrayList<Result> results, String query) {
        if (results != null && !results.isEmpty()) {
            sender.sendMessage(ChatColor.AQUA.toString().concat("Search results for \"").concat(query).concat("\":"));
            Iterator<Result> itr = results.iterator();
            String line = "";
            int num = 0;
            while (itr.hasNext()) {
                num++;
                Result result = itr.next();
                line += (SearchIdsProperties.rightPad(result.getFullValue(), result.getValuePad()) + " " + searchids.properties.delimiter() + " " + SearchIdsProperties.rightPad(result.getName(), searchids.properties.nameWidth()));
                if (num % 2 == 0 || !itr.hasNext()) {
                    sender.sendMessage(ChatColor.GOLD.toString().concat(line.trim()));
                    line = "";
                }
                if (num > 16) {
                    sender.sendMessage(ChatColor.RED.toString().concat("Not all results are displayed. Make your term more specific!"));
                    break;
                }
            }
        }
        else {
            sender.sendMessage(ChatColor.RED.toString().concat("No results found."));
        }
    }
}
