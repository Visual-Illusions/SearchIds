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

import net.visualillusionsent.searchids.Result;
import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.utils.VersionChecker;
import org.spout.api.Server;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.CommandDescription;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.vanilla.ChatStyle;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Command Executor for Spout
 *
 * @author Jason (darkdiplomat)
 */
public class SpoutSearchCommandExecutor extends VisualIllusionsSpoutPluginInformationCommand {

    private final SpoutSearchIds searchids;

    SpoutSearchCommandExecutor(SpoutSearchIds searchids) {
        super(searchids);
        this.searchids = searchids;
    }

    @CommandDescription(aliases = {"search"}, usage = "<query>", desc = "Searches.")
    @Permissible("searchids.search")
    public void search(CommandSource source, CommandArguments args) throws CommandException {
        if (args.length() > 0) {
            printSearchResults(source, searchids.getParser().search(args.toString(), SearchIdsProperties.base), args.toString());
        }
        else {
            source.sendMessage("§cCorrect usage is: /search [item to search for]");
        }
    }

    @CommandDescription(aliases = {"searchids"}, desc = "SearchIds information command.")
    public void information(CommandSource source, CommandArguments args) throws CommandException {
        for (String msg : about) {
            if (msg.equals("$VERSION_CHECK$")) {
                VersionChecker vc = plugin.getVersionChecker();
                Boolean islatest = vc.isLatest();
                if (islatest == null) {
                    source.sendMessage(center(ChatStyle.DARK_GRAY.toString().concat("VersionCheckerError: ").concat(vc.getErrorMessage())));
                }
                else if (!islatest) {
                    source.sendMessage(center(ChatStyle.DARK_GRAY.toString().concat(vc.getUpdateAvailibleMessage())));
                }
                else {
                    source.sendMessage(center(ChatStyle.GREEN.toString().concat("Latest Version Installed")));
                }
            }
            else {
                source.sendMessage(msg);
            }
        }
    }

    private void printSearchResults(CommandSource source, ArrayList<Result> results, String query) {
        if (results != null && !results.isEmpty()) {
            source.sendMessage(ChatStyle.AQUA.toString().concat("Search results for \"").concat(query).concat("\":"));
            Iterator<Result> itr = results.iterator();
            String line = "";
            int num = 0;
            while (itr.hasNext()) {
                num++;
                Result result = itr.next();
                line += (SearchIdsProperties.rightPad(result.getFullValue(), result.getValuePad()) + " " + SearchIdsProperties.delimiter + " " + SearchIdsProperties.rightPad(result.getName(), SearchIdsProperties.nameWidth));
                if (num % 2 == 0 || !itr.hasNext()) {
                    source.sendMessage(ChatStyle.GOLD.toString().concat(line.trim()));
                    line = "";
                }
                if (num > 16) {
                    source.sendMessage(ChatStyle.RED.toString().concat("Not all results are displayed. Make your term more specific!"));
                    break;
                }
            }
        }
        else {
            source.sendMessage(ChatStyle.RED.toString().concat("No results found."));
        }
    }
}
