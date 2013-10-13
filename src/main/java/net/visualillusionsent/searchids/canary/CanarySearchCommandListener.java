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
import net.canarymod.chat.MessageReceiver;
import net.canarymod.chat.TextFormat;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPluginInformationCommand;
import net.visualillusionsent.searchids.Result;
import net.visualillusionsent.searchids.SearchIdsProperties;
import net.visualillusionsent.utils.StringUtils;
import net.visualillusionsent.utils.VersionChecker;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * SearchIds Command Listener
 *
 * @author Jason (darkdiplomat)
 */
public class CanarySearchCommandListener extends VisualIllusionsCanaryPluginInformationCommand {

    private final CanarySearchIds searchids;

    public CanarySearchCommandListener(CanarySearchIds searchids) throws CommandDependencyException {
        super(searchids);
        this.searchids = searchids;
        Canary.commands().registerCommands(this, searchids, false);
    }

    @Command(aliases = {"search"},
            description = "Searches for the Id of a Item or Block",
            permissions = {"searchids.search"},
            toolTip = "/search <Item|Block name>")
    public void searchCommand(MessageReceiver receiver, String[] cmd) {
        if (cmd.length > 1) {
            String query = StringUtils.joinString(cmd, " ", 1);
            printSearchResults(receiver, searchids.getParser().search(query, searchids.properties.base()), query);
        } else {
            receiver.notice("Correct usage is: /search <Item|Block name>");
        }
    }

    @Command(aliases = {"searchids"},
            description = "SearchIds information command",
            permissions = {""},
            toolTip = "/searchids")
    public void infomationCommand(MessageReceiver msgrec, String[] cmd) {
        for (String msg : about) {
            if (msg.equals("$VERSION_CHECK$")) {
                VersionChecker vc = plugin.getVersionChecker();
                Boolean islatest = vc.isLatest();
                if (islatest == null) {
                    msgrec.message(center(TextFormat.GRAY.concat("VersionCheckerError: ").concat(vc.getErrorMessage())));
                } else if (!islatest) {
                    msgrec.message(center(TextFormat.GRAY.concat(vc.getUpdateAvailibleMessage())));
                } else {
                    msgrec.message(center(TextFormat.LIGHT_GREEN.concat("Latest Version Installed")));
                }
            } else {
                msgrec.message(msg);
            }
        }
    }

    private void printSearchResults(MessageReceiver msgrec, ArrayList<Result> results, String query) {
        if (results != null && !results.isEmpty()) {
            msgrec.message(TextFormat.CYAN.concat("Search results for \"").concat(query).concat("\":"));
            Iterator<Result> itr = results.iterator();
            String line = "";
            int num = 0;
            while (itr.hasNext()) {
                num++;
                Result result = itr.next();
                line += (SearchIdsProperties.rightPad(result.getFullValue(), result.getValuePad()) + " " + searchids.properties.delimiter() + " " + SearchIdsProperties.rightPad(result.getName(), searchids.properties.nameWidth()));
                if (num % 2 == 0 || !itr.hasNext()) {
                    msgrec.message(TextFormat.ORANGE.concat(line.trim()));
                    line = "";
                }
                if (num > 16) {
                    msgrec.notice("Not all results are displayed. Make your term more specific!");
                    break;
                }
            }
        } else {
            msgrec.notice("No results found.");
        }
    }
}
